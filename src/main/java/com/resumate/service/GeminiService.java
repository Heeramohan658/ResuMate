package com.resumate.service;
import com.resumate.model.Resume;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.*;
@Service
public class GeminiService {
 @Value("${gemini.api.key:}") private String apiKey;
 @Value("${gemini.model:gemini-2.0-flash}") private String model;
 public Map<String,Object> suggest(Resume r,String jobDescription){
  if(apiKey==null||apiKey.isBlank()) return Map.of("enabled",false,"message","Gemini is not configured. Set GEMINI_API_KEY and restart the app.","suggestions",List.of());
  String prompt="Act as a resume coach. Give concise, truthful, ATS-aware improvement suggestions. Never invent qualifications. Resume: "+summary(r)+" Job description: "+(jobDescription==null?"":jobDescription);
  try {
   Map<String,Object> part=Map.of("text",prompt);
   Map<String,Object> body=Map.of("contents",List.of(Map.of("parts",List.of(part))));
   Map response=RestClient.create().post().uri("https://generativelanguage.googleapis.com/v1beta/models/"+model+":generateContent?key="+apiKey)
    .contentType(MediaType.APPLICATION_JSON).body(body).retrieve().body(Map.class);
   Object candidates=response.get("candidates");
   if(candidates instanceof List<?> list&&!list.isEmpty()){
    Map c=(Map)list.get(0);Map content=(Map)c.get("content");List parts=(List)content.get("parts");
    return Map.of("enabled",true,"text",((Map)parts.get(0)).getOrDefault("text","No response"));
   }
   return Map.of("enabled",true,"text","Gemini returned no suggestions.");
  } catch(Exception e){return Map.of("enabled",true,"error","Gemini request failed. Check API key, model and network settings.");}
 }
 private String summary(Resume r){return "name="+r.getFullName()+", headline="+r.getHeadline()+", summary="+r.getSummary()+", skills="+r.getSkills()+", experience="+r.getExperience()+", projects="+r.getProjects()+", education="+r.getEducation();}
}
