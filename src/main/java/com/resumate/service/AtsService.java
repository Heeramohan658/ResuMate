package com.resumate.service;
import com.resumate.model.Resume;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.*;
@Service
public class AtsService {
 private static final Set<String> STOP=Set.of("the","and","for","with","you","are","our","will","this","that","from","have","has","your","their","they","job","role","work","team","years","year","using","use","into","about","who","what","all","can","able","strong","good");
 public Map<String,Object> analyze(Resume r,String jd){
  String text=String.join(" ",safe(r.getHeadline()),safe(r.getSummary()),safe(r.getExperience()),safe(r.getProjects()),safe(r.getSkills()),safe(r.getEducation()),safe(r.getCertifications())).toLowerCase(Locale.ROOT);
  Set<String> resumeWords=words(text), jobWords=words(safe(jd).toLowerCase(Locale.ROOT));
  Set<String> matched=new TreeSet<>(jobWords); matched.retainAll(resumeWords);
  Set<String> missing=new TreeSet<>(jobWords); missing.removeAll(resumeWords);
  int keyword=jobWords.isEmpty()?0:(int)Math.round(100.0*matched.size()/jobWords.size());
  int sections=0; if(!blank(r.getFullName())&&!blank(r.getEmail()))sections+=15;
  if(!blank(r.getSummary()))sections+=15;if(!blank(r.getExperience()))sections+=20;
  if(!blank(r.getEducation()))sections+=15;if(!blank(r.getSkills()))sections+=15;
  if(!blank(r.getProjects()))sections+=10;if(!blank(r.getPhone()))sections+=5;if(!blank(r.getHeadline()))sections+=5;
  int score=jd==null||jd.isBlank()?sections: (int)Math.round(keyword*.65+sections*.35);
  List<String> tips=new ArrayList<>();
  if(blank(r.getSummary()))tips.add("Add a concise 2–4 line professional summary.");
  if(blank(r.getExperience()))tips.add("Add experience, internships, volunteering or relevant practical work.");
  if(blank(r.getProjects()))tips.add("Add 2–3 projects with tools, actions and measurable outcomes.");
  if(blank(r.getSkills()))tips.add("Add a focused skills section using skills you genuinely possess.");
  if(!blank(jd)&&!missing.isEmpty())tips.add("If accurate, incorporate relevant missing job keywords: "+String.join(", ",missing.stream().limit(15).toList())+".");
  if(blank(r.getEmail())||blank(r.getPhone()))tips.add("Add reliable contact details.");
  Map<String,Object> out=new LinkedHashMap<>();out.put("score",score);out.put("keywordMatch",keyword);out.put("sectionCompleteness",sections);
  out.put("matchedKeywords",matched);out.put("missingKeywords",missing.stream().limit(40).toList());out.put("suggestions",tips);
  out.put("disclaimer","Heuristic estimate, not a guarantee of passing an employer's ATS. Different systems score resumes differently.");return out;
 }

 public Map<String,Object> analyzeUploadedText(String resumeText,String jd){
  String normalized=safe(resumeText).toLowerCase(Locale.ROOT);
  Set<String> resumeWords=words(normalized), jobWords=words(safe(jd).toLowerCase(Locale.ROOT));
  Set<String> matched=new TreeSet<>(jobWords); matched.retainAll(resumeWords);
  Set<String> missing=new TreeSet<>(jobWords); missing.removeAll(resumeWords);
  int keyword=jobWords.isEmpty()?0:(int)Math.round(100.0*matched.size()/jobWords.size());
  String[] headings={"experience","work history","employment","education","skills","technical skills","projects","certifications","summary","profile"};
  int found=0; for(String h:headings) if(normalized.contains(h)) found++;
  int completeness=(int)Math.round(100.0*found/headings.length);
  int score=jobWords.isEmpty()?completeness:(int)Math.round(keyword*.8+completeness*.2);
  List<String> tips=new ArrayList<>();
  if(resumeText==null||resumeText.isBlank()) tips.add("No readable text was extracted. Try a text-based PDF or DOCX file.");
  if(found<5) tips.add("Use clear section headings such as Summary, Skills, Experience, Projects and Education.");
  if(!missing.isEmpty()) tips.add("If accurate, add relevant job-description keywords: "+String.join(", ",missing.stream().limit(15).toList())+".");
  if(normalized.length()<500) tips.add("Your resume text is quite short. Add relevant achievements, projects and measurable results.");
  Map<String,Object> out=new LinkedHashMap<>(); out.put("score",score);out.put("keywordMatch",keyword);out.put("sectionCompleteness",completeness);
  out.put("matchedKeywords",matched);out.put("missingKeywords",missing.stream().limit(40).toList());out.put("suggestions",tips);
  out.put("disclaimer","Estimated ATS compatibility score based on text and keyword overlap. It is not a guarantee of passing an employer's ATS.");return out;
 }

 private Set<String> words(String s){Set<String> out=new HashSet<>();Matcher m=Pattern.compile("[a-z][a-z0-9+#.]{1,}").matcher(s);while(m.find()){String w=m.group();if(!STOP.contains(w))out.add(w);}return out;}
 private boolean blank(String s){return s==null||s.isBlank();} private String safe(String s){return s==null?"":s;}
}
