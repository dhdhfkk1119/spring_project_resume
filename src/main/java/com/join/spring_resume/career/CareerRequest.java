package com.join.spring_resume.career;

import com.join.spring_resume.resume.Resume;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

public class CareerRequest {

    //경력사항 저장 DTO
    @Data
    public static class SaveDTO {
        @NotBlank(message = "회사명은 필수 입력 값입니다.")
        @Size(max = 50, message = "회사명은 50자 이내로 작성해주세요.")
        private String corpName;

        @Size(max = 50, message = "직책은 50자 이내로 작성해주세요.")
        private String position;

        @NotBlank(message = "직무내용은 필수 입력 값입니다.")
        @Size(max = 500, message = "직무내용은 500자 이내로 작성해주세요.")
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

    }


    //경력사항 수정 DTO
    @Data
    public static class UpdateDTO {
        // 어떤 경력을 수정할지 식별하기 위한 ID
        private Long careerIdx;
        private String corpName;
        private String position;
        private String careerContent;
        private LocalDate startAt;
        private LocalDate endAt;

    }

}
