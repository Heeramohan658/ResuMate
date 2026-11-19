package com.resumate.model;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="resumes")
public class Resume {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private String fullName="", email="", phone="", location="", website="", linkedin="", github="";
 private String headline="", summary="", education="", experience="", projects="", skills="", certifications="";
 private String templateName="classic"; private String accentColor="#2563eb";
 private Instant updatedAt=Instant.now();
 @PreUpdate public void touch(){updatedAt=Instant.now();}
 @PrePersist public void init(){updatedAt=Instant.now();}
 public Long getId(){return id;} public void setId(Long v){id=v;}
 public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;}
 public String getEmail(){return email;} public void setEmail(String v){email=v;}
 public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
 public String getLocation(){return location;} public void setLocation(String v){location=v;}
 public String getWebsite(){return website;} public void setWebsite(String v){website=v;}
 public String getLinkedin(){return linkedin;} public void setLinkedin(String v){linkedin=v;}
 public String getGithub(){return github;} public void setGithub(String v){github=v;}
 public String getHeadline(){return headline;} public void setHeadline(String v){headline=v;}
 public String getSummary(){return summary;} public void setSummary(String v){summary=v;}
 public String getEducation(){return education;} public void setEducation(String v){education=v;}
 public String getExperience(){return experience;} public void setExperience(String v){experience=v;}
 public String getProjects(){return projects;} public void setProjects(String v){projects=v;}
 public String getSkills(){return skills;} public void setSkills(String v){skills=v;}
 public String getCertifications(){return certifications;} public void setCertifications(String v){certifications=v;}
 public String getTemplateName(){return templateName;} public void setTemplateName(String v){templateName=v;}
 public String getAccentColor(){return accentColor;} public void setAccentColor(String v){accentColor=v;}
 public Instant getUpdatedAt(){return updatedAt;}
}
