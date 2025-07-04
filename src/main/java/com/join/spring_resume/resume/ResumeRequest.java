package com.join.spring_resume.resume;

import com.join.spring_resume.carrer.CareerRequest;
import com.join.spring_resume.member.Member;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class ResumeRequest {

    //이력서 저장 DTO
    @Data
    public static class SaveDTO {
        @NotBlank(message = "제목은 필수 입력 값입니다.")
        @Size(max = 100, message = "제목은 100자 이내로 작성해주세요.")
        private String resumeTitle;
        @NotBlank(message = "내용은 필수 입력 값입니다.")
        @Size(max = 10000, message = "내용은 10000자 이내로 작성해주세요.")
        private String resumeContent;
        private Boolean isRep;

        @Valid
        private List<CareerRequest.SaveDTO> careers = new ArrayList<>();

        public Resume toEntity(Member member){
            return Resume.builder()
                    .resumeTitle(this.resumeTitle)
                    .resumeContent(this.resumeContent)
                    .isRep(this.isRep)
                    .member(member)
                    .build();
        }

    }//saveDTO


    //이력서 수정 DTO
    @Data
    public static class UpdateDTO {
        @NotBlank(message = "제목은 필수 입력 값입니다.")
        @Size(max = 100, message = "제목은 100자 이내로 작성해주세요.")
        private String resumeTitle;
        @NotBlank(message = "내용은 필수 입력 값입니다.")
        @Size(max = 10000, message = "내용은 10000자 이내로 작성해주세요.")
        private String resumeContent;
        private Boolean isRep;

        @Valid
        private List<CareerRequest.UpdateDTO> careers = new ArrayList<>();

        private List<Long> deletedCareerIds = new ArrayList<>();

    }//UpdateDTO

}//
