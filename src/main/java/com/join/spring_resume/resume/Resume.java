package com.join.spring_resume.resume;

import com.join.spring_resume.carrer.Career;
import com.join.spring_resume.member.Member;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberIdx")
    private Member member;

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
    public Resume(Long resumeIdx, Member member, String resumeContent, List<Career> careerList) {
        this.resumeIdx = resumeIdx;
        this.member = member;
        this.resumeContent = resumeContent;
        this.careerList = careerList;
    }

    //Resume 소유권 확인 메서드
    public boolean isOwner(Long memberIdx) {
        return this.member.getMemberIdx().equals(memberIdx);
    }

}
