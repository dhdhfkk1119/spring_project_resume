package com.join.spring_resume.corp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "corp_tb")
public class Corp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long corpIdx;

    private String corpName;
    @Column(unique = true)
    private String corpId;
    private String email;
    private String password;

    @CreationTimestamp
    private Timestamp createdAt;

    @Builder.Default
    private String corpImage = "basic.png";

    public void isUseridCheck(String inputId) {
        if (this.corpId.equals(inputId)) {
            throw new IllegalArgumentException("중복된 아이디입니다.");
        }
    }

}
