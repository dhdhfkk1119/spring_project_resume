package com.join.spring_resume.resume;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// <Resume,Long> 제네릭 부분 빼먹어서 수정함
public interface ResumeJpaRepository extends JpaRepository<Resume,Long> {

    //이력서 전체조회
    // careeList 오타 수정함 ->  careerList
    @Query("SELECT DISTINCT r FROM Resume r LEFT JOIN FETCH r.careerList c " +
            "WHERE r.member.memberIdx = :memberIdx ORDER BY r.resumeIdx DESC")
    List<Resume> findAllByMemberIdx(@Param("memberIdx") Long memberId);


    //이력서 단건조회
    @Query("SELECT r FROM Resume r LEFT JOIN FETCH r.careerList c " +
            "WHERE r.resumeIdx = :resumeIdx ")
    Optional<Resume> findByResumeIdx(@Param("resumeIdx") Long resumeId);

}//

