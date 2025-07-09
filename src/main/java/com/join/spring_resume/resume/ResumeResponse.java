package com.join.spring_resume.resume;

import com.join.spring_resume.carrer.CareerResponse;
import com.join.spring_resume.member.MemberResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class ResumeResponse {

    //👨‍💻 기업 채용담당관을 위한 이력서 상세보기
    @Data
    public static class CorpDetailDTO {
        private Long resumeIdx;
        private String resumeTitle;
        private String resumeContent;

        //private Member member;
        private MemberResponse.MemberDTO member;

        //private List<Career> careerList;
        private List<CareerResponse.DTO> careerList;

        // Resume 엔티티를 받아서 이 DTO를 채우는 생성자
        public CorpDetailDTO(Resume resume) {
            this.resumeIdx = resume.getResumeIdx();
            this.resumeTitle = resume.getResumeTitle();
            this.resumeContent = resume.getResumeContent();

            //this.member = resume.getMember();
            this.member = MemberResponse.MemberDTO.fromEntity(resume.getMember());

            //this.careerList = resume.getCareerList();
            this.careerList = resume.getCareerList().stream()
                    .map(CareerResponse.DTO::new)
                    .collect(Collectors.toList());
        }
    }//

    // 📚 페이징을 위한 DTO
    @Data
    @AllArgsConstructor
    public static class ListDTO {
        private Resume repResume; //대표이력서
        private Page<Resume> resumePage; // 일반이력서(📚)
    }


}//
