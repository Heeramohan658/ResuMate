package com.resumate.service;
import com.resumate.model.Resume;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.*;
@Service
public class PdfService {
 public byte[] create(Resume r) {
  try(PDDocument doc=new PDDocument(); ByteArrayOutputStream out=new ByteArrayOutputStream()){
   PDPage page=new PDPage(PDRectangle.A4);doc.addPage(page);
   PDFont font=new PDType1Font(Standard14Fonts.FontName.HELVETICA), bold=new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
   try(PDPageContentStream c=new PDPageContentStream(doc,page)){
    float x=48,y=790;
    y=line(c,bold,19,x,y,r.getFullName(),page);
    y=line(c,font,10,x,y,join(r.getHeadline(),r.getEmail(),r.getPhone(),r.getLocation()),page);
    y-=10;
    String[][] sections={{"PROFESSIONAL SUMMARY",r.getSummary()},{"SKILLS",r.getSkills()},{"EXPERIENCE",r.getExperience()},{"PROJECTS",r.getProjects()},{"EDUCATION",r.getEducation()},{"CERTIFICATIONS",r.getCertifications()}};
    for(String[] s:sections){if(s[1]!=null&&!s[1].isBlank()){y=line(c,bold,12,x,y,s[0],page)-3;for(String ln:s[1].split("\\R"))y=line(c,font,10,x,y,ln,page);y-=9;}}
   }
   doc.save(out);return out.toByteArray();
  }catch(Exception e){throw new IllegalStateException("Could not create PDF",e);}
 }
 private float line(PDPageContentStream c,PDFont f,float size,float x,float y,String text,PDPage p)throws IOException{
  if(y<55){return y;}String s=text==null?"":text.replaceAll("[^\\x20-\\x7E]"," "); if(s.length()>115)s=s.substring(0,115);
  c.beginText();c.setFont(f,size);c.newLineAtOffset(x,y);c.showText(s);c.endText();return y-size-5;
 }
 private String join(String...v){return Arrays.stream(v).filter(s->s!=null&&!s.isBlank()).reduce((a,b)->a+" | "+b).orElse("");}
}
