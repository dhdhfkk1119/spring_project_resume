package com.join.spring_resume.carrer;

import lombok.Data;

import java.time.format.DateTimeFormatter;

public class CareerResponse {

    // ✨ 'DTO'를 'InfoDTO'와 같이 더 구체적인 이름으로 변경
    @Data
    public static class InfoDTO {
        // ✨ 식별자 추가: View에서 특정 항목을 다룰 때 유용함
        private Long careerIdx;
        private String corpName;
        private String position;
        private String careerContent;
        private String startAt;
        private String endAt;

        public InfoDTO(Career career) {
            this.careerIdx = career.getCareerIdx();
            this.corpName = career.getCorpName();
            this.position = career.getPosition();
            this.careerContent = career.getCareerContent();

            // 📅 LocalDate를 'yyyy-MM-dd' 형식의 문자열로 변환
            if (career.getStartAt() != null) {
                this.startAt = career.getStartAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            if (career.getEndAt() != null) {
                this.endAt = career.getEndAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
        }
    }
}