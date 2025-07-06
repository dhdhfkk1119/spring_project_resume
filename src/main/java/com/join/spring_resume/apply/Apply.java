package com.join.spring_resume.apply;

import com.join.spring_resume.recruit.Recruit;
import com.join.spring_resume.resume.Resume;
import com.join.spring_resume.util.DateUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "apply_tb")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Apply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applyIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_idx")
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_idx")
    private Recruit recruit;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public String getFormattedAppliedAt() {
        return DateUtil.formatDate(this.createdAt);
    }
}
