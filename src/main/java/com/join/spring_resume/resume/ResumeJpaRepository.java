package com.join.spring_resume.resume;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResumeJpaRepository extends JpaRepository {

    //이력서 전체조회
    @Query("SELECT DISTINCT r FROM Resume r LEFT JOIN FETCH r.careeList c " +
            "WHERE r.member.memberIdx = :memberIdx ORDER BY r.resumeIdx DESC ")
    List<Resume> findAll(@Param("memberIdx") Long memberId);

    //이력서 단건조회
    @Query("SELECT r FROM Resume r LEFT JOIN FETCH r.careerList c " +
            "WHERE r.resumeIdx = :resumeIdx ")
    Optional<Resume> findById(@Param("resumeIdx") Long resumeId);

}

