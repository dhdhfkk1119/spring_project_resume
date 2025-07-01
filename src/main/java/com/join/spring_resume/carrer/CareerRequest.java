package com.join.spring_resume.carrer;

import com.join.spring_resume.resume.Resume;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;

public class CareerRequest {

    //경력사항 저장 DTO
    @Data
    public static class SaveDTO {
        private String corpName;
        private String position;
        private String careerContent;
        private LocalDate startAt;
        private LocalDate endAt;

        public Career toEntity(Resume resume) {
            return Career.builder()
                    .corpName(this.corpName)
                    .position(this.position)
                    .careerContent(this.careerContent)
                    .startAt(this.startAt)
                    .endAt(this.endAt)
                    .resume(resume)
                    .build();
        }

        public void validate() {
//            if (this.corpName == null || corpName.trim().isEmpty()) {
//                throw new IllegalArgumentException("회사명은 필수입니다");
//            }
//            if (this.careerContent == null || careerContent.trim().isEmpty()) {
//                throw new IllegalArgumentException("내용은 필수입니다");
//            }
//            if (this.startAt == null) {
//                throw new IllegalArgumentException("입사일은 필수입니다");
//            }
//            if (this.endAt == null) {
//                throw new IllegalArgumentException("퇴사일은 필수입니다");
//            }
        }

    }


    //경력사항 수정 DTO
    @Data
    public static class UpdateDTO {
        private String corpName;
        private String position;
        private String careerContent;
        private LocalDate startAt;
        private LocalDate endAt;

        public void validate() {
            if (this.corpName == null || corpName.trim().isEmpty()) {
                throw new IllegalArgumentException("회사명은 필수입니다");
            }
            if (this.position == null || position.trim().isEmpty()) {
                throw new IllegalArgumentException("직책은 필수입니다");
            }
            if (this.careerContent == null || careerContent.trim().isEmpty()) {
                throw new IllegalArgumentException("내용은 필수입니다");
            }
            if (this.startAt == null) {
                throw new IllegalArgumentException("입사일은 필수입니다");
            }
            if (this.endAt == null) {
                throw new IllegalArgumentException("퇴사일은 필수입니다");
            }
        }//validate
    }

}
