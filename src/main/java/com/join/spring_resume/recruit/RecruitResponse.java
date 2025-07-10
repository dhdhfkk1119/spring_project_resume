package com.join.spring_resume.recruit;

import com.join.spring_resume.corp.CorpResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class RecruitResponse {

    /**
     * 공고 리스트용 DTO (간단 목록 뷰)
     */
    @Data
    @AllArgsConstructor
    public static class RecruitListDTO {
        private Long recruitIdx;
        private String recruitTitle;
        private String area;
        private String career;
        private String education;
        private String workType;
        private String recruitContent;
        private String endAtFormatted;
        private CorpResponse.CorpDTO corp;

        public static RecruitListDTO fromEntity(Recruit recruit) {
            return new RecruitListDTO(
                    recruit.getRecruitIdx(),
                    recruit.getRecruitTitle(),
                    recruit.getArea(),
                    recruit.getCareer(),
                    recruit.getEducation(),
                    recruit.getWorkType(),
                    recruit.getRecruitContent(),
                    recruit.getEndAtFormatted(),
                    CorpResponse.CorpDTO.fromEntity(recruit.getCorp())
            );
        }
    }

}
