package com.join.spring_resume.recruit_like;

import com.join.spring_resume.member.Member;
import com.join.spring_resume.recruit.Recruit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RecruitLikeRepository extends JpaRepository<RecruitLike,Long> {

    @Query("SELECT r FROM RecruitLike r WHERE r.member = :member AND r.recruit = :recruit")
    Optional<RecruitLike> findByMemberAndRecruit(@Param("member") Member member, @Param("recruit") Recruit recruit);

    @Query("SELECT COUNT(a) > 0 FROM RecruitLike a WHERE a.member.id = :memberId AND a.recruit.id = :recruitId")
    boolean existsByMemberIdAndRecruitId(@Param("memberId") Long memberId, @Param("recruitId") Long recruitId);
}
