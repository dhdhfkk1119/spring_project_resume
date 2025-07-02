package com.join.spring_resume.recruit;

import com.join.spring_resume.corp.Corp;
import com.join.spring_resume.session.SessionUser;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

// request 전용 (데이터 보여주기 formatted 해서 보내주기)
public class RecruitResponseDTO {

    private Long recruitIdx;
    private String recruitTitle;
    private String startAtFormatted;
    private String endAtFormatted;
    private int recruitNumber;
    private String area;
    private String career;
    private String education;
    private String workType;
    private String recruitContent;
    private CorpDTO corp;
    private boolean isOwner;

    public RecruitResponseDTO(Recruit recruit) {
        this.recruitIdx = recruit.getRecruitIdx();
        this.recruitTitle = recruit.getRecruitTitle();
        this.startAtFormatted = recruit.getStartAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        this.endAtFormatted = recruit.getEndAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        this.recruitNumber = recruit.getRecruitNumber();
        this.area = recruit.getArea();
        this.career = recruit.getCareer();
        this.education = recruit.getEducation();
        this.workType = recruit.getWorkType();
        this.recruitContent = recruit.getRecruitContent();
        this.corp = new CorpDTO(recruit.getCorp());
        // this.isOwner = isRecruitOwner(recruit,sessionUser);
    }

    @Getter
    public static class CorpDTO {
        private final String corpName;

        public CorpDTO(Corp corp) {
            this.corpName = corp.getCorpName();
        }
    }

}
