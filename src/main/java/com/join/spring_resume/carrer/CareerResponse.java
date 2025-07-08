package com.join.spring_resume.carrer;

import lombok.Data;

import java.time.format.DateTimeFormatter;

public class CareerResponse {

    @Data
    public static class DTO {
        private String corpName;
        private String position;
        private String careerContent;
        private String startAt;
        private String endAt;

        public DTO(Career career) {
            this.corpName = career.getCorpName();
            this.position = career.getPosition();
            this.careerContent = career.getCareerContent();

            //    LocalDate를 'yyyy-MM-dd' 형식의 문자열로 변환
            if (career.getStartAt() != null) {
                this.startAt = career.getStartAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            if (career.getEndAt() != null) {
                this.endAt = career.getEndAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
        }
    }
}
