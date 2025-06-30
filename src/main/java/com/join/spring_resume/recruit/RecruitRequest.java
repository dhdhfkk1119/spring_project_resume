package com.join.spring_resume.recruit;

import com.join.spring_resume.corp.Corp;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class RecruitRequest {

    @Data
    public static class RecruitDTO{
        private String recruitTitle;
        private String area; // 모집 지역
        private int recruitNumber;
        private String recruitContent;
        private String career; // 경력 (신입 , 경력)
        private String education; // 최종학력
        private String workType; // 근무형태 (계약직, 정규직)
        private String startAt; // 모집일, yyyy-MM-dd 형태로 들어옴
        private String endAt;   // 마감일

        public Recruit toEntity(Corp corp) {
            return Recruit.builder()
                    .corp(corp)
                    .recruitTitle(recruitTitle)
                    .area(area)
                    .recruitNumber(recruitNumber)
                    .recruitContent(recruitContent)
                    .career(career)
                    .education(education)
                    .workType(workType)
                    .startAt(toStartOfDay(startAt))  // 00:00:00으로 변환 변환안하면 오류남
                    .endAt(toEndOfDay(endAt))        // 23:59:59.999999999로 변환
                    .build();
        }

        private LocalDateTime toStartOfDay(String dateStr) {
            if (dateStr == null || dateStr.isEmpty()) return null;
            LocalDate date = LocalDate.parse(dateStr);   // yyyy-MM-dd 파싱
            return date.atStartOfDay();                   // yyyy-MM-ddT00:00:00 반환
        }

        private LocalDateTime toEndOfDay(String dateStr) {
            if (dateStr == null || dateStr.isEmpty()) return null;
            LocalDate date = LocalDate.parse(dateStr);
            return date.atTime(LocalTime.MAX);            // yyyy-MM-ddT23:59:59.999999999 반환
        }


        public void validate() {
            if(recruitTitle == null || recruitTitle.trim().isEmpty()){
                throw new IllegalArgumentException("모집 제목을 입력해주시기 바랍니다");
            }
            if(area == null || area.trim().isEmpty()){
                throw new IllegalArgumentException("모집 지역을 선택해주시기 바랍니다");
            }
            if(recruitNumber == 0){
                throw new IllegalArgumentException("모집인원을 0명 이상 해주시기 바랍니다");
            }
            if(recruitContent == null || recruitContent.trim().isEmpty()){
                throw new IllegalArgumentException("모집 내욜을 작성해주시기 바랍니다");
            }if(startAt == null ){
                throw new IllegalArgumentException("모집 시작일을 설정해주시기 바랍니다");
            }if(endAt == null ){
                throw new IllegalArgumentException("모집 끝나는일을 설정해주시기 바랍니다");
            }

        }
    }

    @Data
    public static class RecruitUpdateDTO{
        private Corp corpIdx; // 기업 번호
        private String recruitTitle;
        private String area; // 모집 지역
        private int recruitNumber;
        private String recruitContent;
        private Timestamp startAt; // 모집일
        private Timestamp endAt; // 끝나는 일

    }
}
