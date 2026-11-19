package com.resumate.controller;
import com.resumate.model.Resume;import com.resumate.repo.ResumeRepository;import com.resumate.service.*;
import org.springframework.http.*;import org.springframework.web.bind.annotation.*;import org.springframework.web.multipart.MultipartFile;import org.springframework.web.server.ResponseStatusException;import org.apache.pdfbox.Loader;import org.apache.pdfbox.text.PDFTextStripper;import org.apache.poi.xwpf.usermodel.XWPFDocument;import org.apache.poi.xwpf.usermodel.XWPFParagraph;import java.io.*;import java.nio.charset.StandardCharsets;import java.util.*;
@RestController @RequestMapping("/api")
public class ResumeController {
 private final ResumeRepository repo;private final AtsService ats;private final GeminiService gemini;private final PdfService pdf;
 public ResumeController(ResumeRepository repo,AtsService ats,GeminiService gemini,PdfService pdf){this.repo=repo;this.ats=ats;this.gemini=gemini;this.pdf=pdf;}
 @GetMapping("/resumes") public List<Resume> all(){return repo.findAll();}
 @GetMapping("/resumes/{id}") public Resume get(@PathVariable Long id){return repo.findById(id).orElseThrow();}
 @PostMapping("/resumes") public Resume create(@RequestBody Resume r){r.setId(null);return repo.save(r);}
 @PutMapping("/resumes/{id}") public Resume update(@PathVariable Long id,@RequestBody Resume r){Resume old=get(id);r.setId(id);return repo.save(r);}
 @DeleteMapping("/resumes/{id}") public void delete(@PathVariable Long id){repo.deleteById(id);}
 @PostMapping(value="/ats/upload",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public Map<String,Object> analyzeUpload(@RequestParam("file") MultipartFile file,@RequestParam(value="jobDescription",required=false) String jobDescription) throws IOException {
  if(file.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Please choose a resume file.");
  if(file.getSize()>10*1024*1024) throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE,"File must be 10 MB or smaller.");
  String name=Objects.toString(file.getOriginalFilename(),"").toLowerCase(Locale.ROOT),text;
  if(name.endsWith(".pdf")){try(var doc=Loader.loadPDF(file.getBytes())){text=new PDFTextStripper().getText(doc);}}
  else if(name.endsWith(".docx")){try(var doc=new XWPFDocument(file.getInputStream())){StringJoiner j=new StringJoiner("\n");for(XWPFParagraph p:doc.getParagraphs())j.add(p.getText());text=j.toString();}}
  else if(name.endsWith(".txt")){text=new String(file.getBytes(),StandardCharsets.UTF_8);}
  else throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"Supported formats: PDF, DOCX and TXT.");
  if(text.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No readable text found. If this is a scanned PDF, run OCR first or upload a text-based PDF.");
  Map<String,Object> result=new LinkedHashMap<>(ats.analyzeUploadedText(text,jobDescription));result.put("extractedText",text);result.put("fileName",file.getOriginalFilename());return result;
 }
 @PostMapping("/ats/analyze") public Map<String,Object> analyze(@RequestBody AnalysisRequest q){return ats.analyze(q.resume(),q.jobDescription());}
 @PostMapping("/ai/suggestions") public Map<String,Object> ai(@RequestBody AnalysisRequest q){return gemini.suggest(q.resume(),q.jobDescription());}
 @PostMapping("/resumes/pdf") public ResponseEntity<byte[]> export(@RequestBody Resume r){return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=resumate-resume.pdf").contentType(MediaType.APPLICATION_PDF).body(pdf.create(r));}
 public record AnalysisRequest(Resume resume,String jobDescription){}
}
