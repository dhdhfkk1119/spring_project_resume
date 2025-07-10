package com.join.spring_resume.recruit_like;

import com.join.spring_resume.recruit.RecruitResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

public class RecruitLikeResponse {

    @Data
    @AllArgsConstructor
    public static class LikeDTO {
        private Long likeIdx;
        private RecruitResponse.RecruitListDTO recruit; // 공고 정보
        private String createdAtFormatted;

        public static LikeDTO fromEntity(RecruitLike like) {
            return new LikeDTO(
                    like.getLikeIdx(),
                    RecruitResponse.RecruitListDTO.fromEntity(like.getRecruit()),
                    like.getFormattedAppliedAt()
            );
        }
    }

}
