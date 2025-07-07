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

    @Query("SELECT a FROM Apply a WHERE a.resume.member.id = :memberIdx")
    List<Apply> findAllAppliesByMemberIdx(@Param("memberIdx") Long memberIdx);
}
