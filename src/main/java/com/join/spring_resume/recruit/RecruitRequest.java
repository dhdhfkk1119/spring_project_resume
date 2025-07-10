package com.join.spring_resume.recruit;

import com.join.spring_resume.corp.Corp;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class RecruitRequest {
    
    // 공고 데이터 생성용
    @Data
    public static class RecruitDTO{

        @NotBlank(message = "공고 제목을 입력해주시기 바랍니다")
        private String recruitTitle;

        @NotBlank(message = "공고 모집 지역을 입력해주시기 바랍니다")
        private String area; // 모집 지역

        @NotNull(message = "고용인원을 선택해주시기바랍니다")
        @Min(value = 0, message = "고용인원은 0명이상 이어야 합니다")
        private Integer recruitNumber;

        @NotBlank(message = "공고의 직무 내용을 입력해주시기 바랍니다")
        private String recruitContent;

        @NotBlank(message = "공고의 경력을 입력해주시기 바랍니다")
        private String career; // 경력 (신입 , 경력)

        @NotBlank(message = "공고의 최종학력을 입력해주시기 바랍니다")
        private String education; // 최종학력

        @NotBlank(message = "직무의 근무형태를 입력해주시기 바랍니다")
        private String workType; // 근무형태 (계약직, 정규직)

        @NotBlank(message = "공고의 시작일을 입력해주시기 바랍니다")
        private String startAt; // 모집일, yyyy-MM-dd 형태로 들어옴

        @NotBlank(message = "공고의 마감일을 입력해주시기 바랍니다")
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

        public LocalDateTime getParsedStartAt() {
            return toStartOfDay(this.startAt);
        }

        public LocalDateTime getParsedEndAt() {
            return toEndOfDay(this.endAt);
        }

    }

    // 업데이트용
    @Data
    public static class RecruitUpdateDTO{
        private Corp corpIdx; // 기업 번호
        private String recruitTitle;
        private String area; // 모집 지역
        private int recruitNumber;
        private String recruitContent;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startAt;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endAt;

    }


}
