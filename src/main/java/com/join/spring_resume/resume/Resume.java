package com.join.spring_resume.resume;

import com.join.spring_resume.carrer.Career;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "resume_tb")
@Entity
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resumeIdx;

    //TODO Member 객체

    @OneToMany(mappedBy = "resume",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)

    @JoinColumn(name = "careerIdx")
    private List<Career> careerList = new ArrayList<>();

    private String resumeContent;

    @CreationTimestamp
    private Timestamp createdAt;

    @Builder
    public Resume(Long resumeIdx, String resumeContent, List<Career> careerList) {
        this.resumeIdx = resumeIdx;
        this.resumeContent = resumeContent;
        this.careerList = careerList;
    }

}
