package com.join.spring_resume.recruit_like;

import com.join.spring_resume.member.Member;
import com.join.spring_resume.member.MemberRequest;
import com.join.spring_resume.member.MemberResponse;
import com.join.spring_resume.recruit.Recruit;
import com.join.spring_resume.recruit.RecruitResponse;
import lombok.Data;

public class RecruitLikeRequest {

    // 저장해주는 용도 ( 좋아요 )
    @Data
    public static class SaveDTO{
        public RecruitLike toEntity(Member member, Recruit recruit){
            return RecruitLike.builder()
                    .member(member)
                    .recruit(recruit)
                    .build();

        }
    }
}
