package com.join.spring_resume.apply;

import com.join.spring_resume.corp.Corp;
import com.join.spring_resume.recruit.Recruit;
import com.join.spring_resume.resume.Resume;
import lombok.Data;

public class ApplyRequest {

    @Data
    public static class SaveDTO{
        public Apply toEntity(Recruit recruit , Resume resume) {
            return Apply.builder()
                    .recruit(recruit)
                    .resume(resume)
                    .build();
        }
    }
}
