package com.join.spring_resume.apply;

import com.join.spring_resume.recruit.Recruit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
    Page<Apply> findAllAppliesByMemberIdx(@Param("memberIdx") Long memberIdx, Pageable pageable);

    // 등록한 등록한 공고에 지원한 지원자 정보 
    @Query("""
        SELECT a
        FROM Apply a
        WHERE a.recruit.id = :recruitIdx
    """)
    Page<Apply> findAllByRecruitIdx(@Param("recruitIdx") Long recruitIdx,Pageable pageable);

    // 각 등록한 공고의 인원 수
    @Query("SELECT COUNT(a) FROM Apply a WHERE a.recruit.id = :recruitId")
    long countByRecruitId(@Param("recruitId") Long recruitId);

    // 내가 지원한 공고에 갯수
    @Query("SELECT COUNT(a) FROM Apply a WHERE a.resume.member.memberIdx = :memberIdx")
    long countByMemberIdx(@Param("memberIdx") Long memberIdx);
    
    // 공고 삭제시 해당 Apply 테이블에서 데이터 전부 삭제
    @Query("delete from Apply a where a.recruit.id = :recruitIdx")
    @Modifying
    void deleteByRecruitIdx(@Param("recruitIdx")Long recruitIdx);

}
