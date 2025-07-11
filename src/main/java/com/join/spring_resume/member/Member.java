package com.join.spring_resume.member;

import com.join.spring_resume.util.DateUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.sql.Timestamp;
import java.time.LocalDateTime;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "member_tb")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 pk 및 auto increment 설정
    private Long memberIdx;

    private String username;
    private String password;
    private String memberId;
    private String email;
    private String sex;
    private int age;
    
    private String address; // 지번

    @Column(name = "address_default")
    private String addressDefault; // 기본 주소

    @Column(name = "address_detail")
    private String addressDetail; // 상세 주소

    private String phoneNumber; // 전화번호

    @CreationTimestamp
    private Timestamp createdAt;


    // 시간 변환 View 에서 예쁘게 보여주기
    public String getCreatedAtFormatted() {
        if (this.createdAt == null) return "";
        return DateUtil.timestampFormat(this.createdAt);
    }

    private String memberImage = "basic.png";
}
