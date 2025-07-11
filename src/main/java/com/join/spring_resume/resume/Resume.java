package com.join.spring_resume.resume;

import com.join.spring_resume.career.Career;
import com.join.spring_resume.member.Member;
import com.join.spring_resume.util.DateUtil;
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

    private String resumeTitle;
    @Lob
    private String resumeContent;

    //대표이력서 (notnull 제거)
    private Boolean isRep = false;

    @CreationTimestamp
    private Timestamp createdAt;

    //목록에서 이력서 내용 첫줄만 보여주기
    public String getShortContent() {
        if (resumeContent == null || resumeContent.length() <= 75) {
            return resumeContent;
        }
        return resumeContent.substring(0, 75) + "..";
    }

    // 시간 변환 View 에서 예쁘게 보여주기
    public String getCreatedAtFormatted() {
        if (this.createdAt == null) return "";
        return DateUtil.timestampFormat(this.createdAt);
    }

    //member 테이블 가져오기
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx")
    private Member member;

    //career 테이블 가져오기
    @OrderBy("startAt DESC")
    @OneToMany(mappedBy = "resume",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE)
    List<Career> careerList = new ArrayList<>();
    //선언과 동시에 초기화

    //Resume 소유권 확인 메서드
    public boolean isOwner(Long memberIdx) {
        return this.member.getMemberIdx().equals(memberIdx);
    }

}
