package com.join.spring_resume.recruit;

import com.join.spring_resume.corp.Corp;
import com.join.spring_resume.util.DateUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recruit_tb")
@Builder
public class Recruit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recruitIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "corp_idx") // recruit_tb 테이블의 외래키 컬럼명
    private Corp corp;

    private String recruitTitle;
    private String area;
    private Integer recruitNumber;
    private String recruitContent;

    private String career; // 경력 (신입 , 경력)
    private String education; // 최종학력
    private String workType; // 근무형태 (계약직, 정규직)

    private LocalDateTime startAt; // 모집 시작일
    private LocalDateTime endAt; //  모집 끝나는 일

    @CreationTimestamp
    private Timestamp createdAt;

    // 작성자 와 세션 아이디 비교
    public boolean isOwner(Long userId){
        return this.corp.getCorpIdx().equals(userId);
    }


    private String logoImages;

    // 시간 변환 View 에서 예쁘게 보여주기
    public String getStartAtFormatted() {
        if (this.startAt == null) return "";
        return DateUtil.formatDate(this.startAt);
    }

    // 시간 변환 View 에서 예쁘게 보여주기
    public String getEndAtFormatted() {
        if (this.endAt == null) return "";
        return DateUtil.formatDate(this.endAt);
    }

}
