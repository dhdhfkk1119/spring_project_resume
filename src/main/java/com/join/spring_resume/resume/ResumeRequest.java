package com.join.spring_resume.resume;

import com.join.spring_resume.carrer.CareerRequest;
import com.join.spring_resume.member.Member;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class ResumeRequest {

    //이력서 저장 DTO
    @Data
    public static class saveDTO {
        private String resumeTitle;
        private String resumeContent;
        private List<CareerRequest.SaveDTO> careers = new ArrayList<>();

        public Resume toEntity(Member member){
            return Resume.builder()
                    .resumeTitle(this.resumeTitle)
                    .resumeContent(this.resumeContent)
                    .member(member)
                    .build();
        }

        //유효성 검사
        public void validate() {
            if (resumeTitle == null || resumeTitle.trim().isEmpty()) {
                throw new IllegalArgumentException("제목은 필수입니다");
            }
            if (this.resumeContent == null || resumeContent.trim().isEmpty()) {
                throw new IllegalArgumentException("내용은 필수입니다");
            }
        }
    }//saveDTO


    //이력서 수정 DTO
    @Data
    public static class UpdateDTO {
        private String resumeTitle;
        private String resumeContent;
        private Boolean isRep;

        private List<CareerRequest.UpdateDTO> careers = new ArrayList<>();
        private List<Long> deletedCareerIds = new ArrayList<>();

        //유효성 검사
        public void validate() {
            if (resumeTitle == null || resumeTitle.trim().isEmpty()) {
                throw new IllegalArgumentException("제목은 필수입니다");
            }
            if (resumeContent == null || resumeContent.trim().isEmpty()) {
                throw new IllegalArgumentException("내용은 필수입니다");
            }
        }//validate
    }//UpdateDTO

}//
