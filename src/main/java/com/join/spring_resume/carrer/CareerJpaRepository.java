package com.join.spring_resume.carrer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareerJpaRepository extends JpaRepository<Career, Long> {
    List<Career> findByResume_ResumeIdx(Long resumeIdx);
}
