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
    private String corpId;
    private String email;
    private String password;

    @CreationTimestamp
    private Timestamp createdAt;

}
