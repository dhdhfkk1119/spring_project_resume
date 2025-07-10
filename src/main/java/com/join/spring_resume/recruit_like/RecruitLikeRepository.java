package com.join.spring_resume.recruit_like;

import com.join.spring_resume.member.Member;
import com.join.spring_resume.recruit.Recruit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecruitLikeRepository extends JpaRepository<RecruitLike,Long> {

    // 내가 공고에 대한 좋아요 누르기 ( db 넣기용)
    @Query("select r from RecruitLike r where r.member = :member and r.recruit = :recruit")
    Optional<RecruitLike> findByMemberAndRecruit(@Param("member") Member member, @Param("recruit") Recruit recruit);

    // 내가 좋아요 누른 공고에 대한 체크 하기 (존재 여부용)
    @Query("select count(a) > 0 from RecruitLike a where a.member.id = :memberId and a.recruit.id = :recruitId")
    boolean existsByMemberIdAndRecruitId(@Param("memberId") Long memberId, @Param("recruitId") Long recruitId);
    
    // 내가 좋아요 누른 게시물에 대한 정보
    @Query("select r from RecruitLike r where r.member.id = :memberIdx")
    Page<RecruitLike> findByMemberIdx(Long memberIdx, Pageable pageable);

    @Query("delete from RecruitLike r where r.recruit.id = :recruitIdx")
    @Modifying
    void deleteByRecruitIdx(@Param("recruitIdx")Long recruitIdx);
}
