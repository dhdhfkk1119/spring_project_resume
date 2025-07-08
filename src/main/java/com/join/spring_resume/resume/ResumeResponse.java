package com.join.spring_resume.resume;

import com.join.spring_resume.carrer.Career;
import com.join.spring_resume.carrer.CareerResponse;
import com.join.spring_resume.member.Member;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

public class ResumeResponse {

    //👨‍💻 기업 채용담당관을 위한 이력서 상세보기 기능 구현
    @Data
    public static class CorpDetailDTO {
        private Long resumeIdx;
        private String resumeTitle;
        private String resumeContent;

        private Member member;
        // private MemberDTO member;

        //private List<Career> careerList;
        private List<CareerResponse.DTO> careerList;

        // Resume 엔티티를 받아서 이 DTO를 채우는 생성자
        public CorpDetailDTO(Resume resume) {
            this.resumeIdx = resume.getResumeIdx();
            this.resumeTitle = resume.getResumeTitle();
            this.resumeContent = resume.getResumeContent();

            this.member = resume.getMember();
            // this.member = new MemberDTO(resume.getMember());

            //this.careerList = resume.getCareerList();
            this.careerList = resume.getCareerList().stream()
                    .map(CareerResponse.DTO::new)
                    .collect(Collectors.toList());
        }
    }

}
