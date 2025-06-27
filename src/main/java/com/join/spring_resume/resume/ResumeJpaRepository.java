package com.join.spring_resume.resume;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResumeJpaRepository extends JpaRepository {

    @Query("SELECT DISTINCT r FROM Resume r LEFT JOIN FETCH r.careers c " +
            "WHERE r.member.id = :memberId ORDER BY r.resumeIdx DESC ")
    List<Resume> findAllJoinUser(@Param("memberIdx") Long memberId);

    @Query("SELECT r FROM Resume r LEFT JOIN FETCH r.careers c " +
            "WHERE r.resumeIdx = :resumeId ")
    Optional<Resume> findByIdWithCareers(@Param("resumeId") Long resumeId);

}

