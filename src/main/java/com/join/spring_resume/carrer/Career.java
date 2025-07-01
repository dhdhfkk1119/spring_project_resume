package com.join.spring_resume.carrer;

import com.join.spring_resume.resume.Resume;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Builder
@Data
@Table(name = "career_tb")
@Entity
public class Career {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long careerIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resumeIdx")
    private Resume resume;

    private String corpName;
    private String position;
    private String careerContent;

    private LocalDate startAt;
    private LocalDate endAt;

    @Builder
    public Career(Long careerIdx, Resume resume, String corpName, String position, String careerContent, LocalDate startAt, LocalDate endAt) {
        this.resume = resume;
        this.corpName = corpName;
        this.position = position;
        this.careerContent = careerContent;
        this.startAt = startAt;
        this.endAt = endAt;
    }
}
