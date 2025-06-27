package com.join.spring_resume.member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.sql.Timestamp;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    private String addressDefault; // 기본 주소
    private String addressDetail; // 상세 주소
    
    @CreationTimestamp
    private Timestamp createdAt;
}
