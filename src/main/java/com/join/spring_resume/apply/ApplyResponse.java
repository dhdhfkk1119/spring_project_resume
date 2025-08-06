package com.join.spring_resume.apply;

import com.join.spring_resume.recruit.Recruit;
import com.join.spring_resume.recruit.RecruitResponse;
import com.join.spring_resume.resume.ResumeResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

public class ApplyResponse {
    
    // 내가 지원한 공고에 대한 목록 보여주기
    @Data
    @AllArgsConstructor
    public static class ApplyDTO{
        private Long applyIdx;
        private RecruitResponse.RecruitListDTO recruit;
        private String createdAtFormatted;

        public static ApplyDTO fromEntity(Apply apply){
            return new ApplyDTO(
                    apply.getApplyIdx(),
                    RecruitResponse.RecruitListDTO.fromEntity(apply.getRecruit()),
                    apply.getFormattedAppliedAt()
            );
        }
    }

    // 기업이 올린 공고에 누가 지원했는지 알기 위한
    @Data
    @AllArgsConstructor
    public static class ApplicantDTO {
        private Long applyIdx;
        private String username;
        private String email;
        private Long resumeIdx;
        private String resumeTitle;
        private String formattedAppliedAt;

        public static ApplicantDTO fromEntity(Apply apply) {
            return new ApplicantDTO(
                    apply.getApplyIdx(),
                    apply.getResume().getMember().getUsername(),
                    apply.getResume().getMember().getEmail(),
                    apply.getResume().getResumeIdx(),
                    apply.getResume().getResumeTitle(),
                    apply.getFormattedAppliedAt()
            );
        }
    }

    // 일반 유저의 지원한 공고 확인하기
    @Data
    public static class RecruitWithApplyCountDTO {
        private Recruit recruit;
        private long applyCount; // 몇개 지원했는지 확인 하기 위함

        public RecruitWithApplyCountDTO(Recruit recruit, long applyCount) {
            this.recruit = recruit;
            this.applyCount = applyCount;
        }

    }

}
