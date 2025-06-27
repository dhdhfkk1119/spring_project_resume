package com.join.spring_resume.carrer;

import com.join.spring_resume.resume.Resume;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
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
    @JoinColumn (name = "resumeIdx")
    private Resume resume;

    private String corpName;
    private String careerContent;

    private Timestamp startAt;
    private Timestamp endAt;

}
