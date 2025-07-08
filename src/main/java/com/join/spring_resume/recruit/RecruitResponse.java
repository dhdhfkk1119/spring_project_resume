package com.join.spring_resume.recruit;

import com.join.spring_resume.corp.Corp;
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


    /**
     * 공고 상세 조회용 DTO
     */
    @Data
    @AllArgsConstructor
    public static class RecruitDetailDTO {
        private Long recruitIdx;
        private String recruitTitle;
        private String area;
        private String career;
        private String education;
        private String workType;
        private Integer recruitNumber;
        private String recruitContent;
        private String startAtFormatted;
        private String endAtFormatted;
        private String logoImages;

        private CorpResponse.CorpDTO corp;

        public static RecruitDetailDTO fromEntity(Recruit recruit) {
            return new RecruitDetailDTO(
                    recruit.getRecruitIdx(),
                    recruit.getRecruitTitle(),
                    recruit.getArea(),
                    recruit.getCareer(),
                    recruit.getEducation(),
                    recruit.getWorkType(),
                    recruit.getRecruitNumber(),
                    recruit.getRecruitContent(),
                    recruit.getStartAtFormatted(),
                    recruit.getEndAtFormatted(),
                    recruit.getLogoImages(),
                    CorpResponse.CorpDTO.fromEntity(recruit.getCorp())
            );
        }
    }

    /**
     * 공고 페이지 응답 DTO (페이징 정보 + 리스트 포함)
     */
    @Data
    @AllArgsConstructor
    public static class RecruitPageDTO {
        private List<RecruitListDTO> content;
        private int page;
        private int size;
        private int totalPages;
        private long totalElements;
        private boolean last;

        public static RecruitPageDTO fromPage(Page<Recruit> recruitPage) {
            List<RecruitListDTO> dtoList = recruitPage.getContent().stream()
                    .map(RecruitListDTO::fromEntity)
                    .collect(Collectors.toList());

            return new RecruitPageDTO(
                    dtoList,
                    recruitPage.getNumber(),
                    recruitPage.getSize(),
                    recruitPage.getTotalPages(),
                    recruitPage.getTotalElements(),
                    recruitPage.isLast()
            );
        }
    }

}
