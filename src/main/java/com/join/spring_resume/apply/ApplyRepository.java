package com.join.spring_resume.apply;

import com.join.spring_resume.recruit.Recruit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplyRepository extends JpaRepository<Apply,Long> {

    @Query("SELECT COUNT(a) > 0 FROM Apply a WHERE a.resume.member.id = :memberIdx AND a.recruit.id = :recruitIdx")
    boolean existsByMemberIdxAndRecruitIdx(@Param("memberIdx") Long memberIdx, @Param("recruitIdx") Long recruitIdx);

    // 자신이 지원한 공고 정보 가져오기
    @Query("SELECT a FROM Apply a WHERE a.resume.member.id = :memberIdx")
    List<Apply> findAllAppliesByMemberIdx(@Param("memberIdx") Long memberIdx);

    // 등록한 등록한 공고에 지원한 지원자 정보 
    @Query("""
        SELECT a
        FROM Apply a
        WHERE a.recruit.id = :recruitIdx
    """)
    List<Apply> findAllByRecruitIdx(@Param("recruitIdx") Long recruitIdx);

    // 각 등록한 공고의 인원 수
    @Query("SELECT COUNT(a) FROM Apply a WHERE a.recruit.id = :recruitId")
    long countByRecruitId(@Param("recruitId") Long recruitId);
}
