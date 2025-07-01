package com.join.spring_resume.resume;

import com.join.spring_resume.carrer.CareerRequest;
import lombok.Data;

import java.util.List;

public class ResumeRequest {

    //이력서 저장 DTO
    @Data
    public static class saveDTO {
        private String resumeContent;
        private List<CareerRequest.SaveDTO> careers;

        public Resume toEntity(){
            return Resume.builder()
                    .resumeContent(this.resumeContent)
                    .build();
        }

        public void validate() {
            if (this.resumeContent == null || resumeContent.trim().isEmpty()) {
                throw new IllegalArgumentException("내용은 필수입니다");
            }
        }
    }//saveDTO


    //이력서 수정 DTO
    @Data
    public static class UpdateDTO {
        private String resumeContent;

        public void validate() {
            if (this.resumeContent == null || resumeContent.trim().isEmpty()) {
                throw new IllegalArgumentException("내용은 필수입니다");
            }
        }//validate
    }//UpdateDTO

}//
