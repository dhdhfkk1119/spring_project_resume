package com.join.spring_resume.recruit_like;

import com.join.spring_resume.recruit.RecruitResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

public class RecruitLikeResponse {
    
    // 해당 공고에 대한 좋아요를 like db에서 찾아서 공고에 대한 정보를 DTO -> 엔티티로 바꾸기
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
