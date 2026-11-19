package com.resumate.repo;
import com.resumate.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ResumeRepository extends JpaRepository<Resume,Long> {}
