package com.join.spring_resume.board;

import com.join.spring_resume.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "board_tb")
@Getter
@Setter
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardIdx;

    private String boardTitle;


    @Column(columnDefinition = "TEXT")
    private String boardContent;

    private String tags;

    private int boardHits;

    // 작성자 외래 키 (Member 객체 대신 ID만 저장)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx", nullable = false)
    private Member member;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // 화면에서 작성자 여부 판단용
    @Transient
    private boolean isAuthor;

    // 필요시 Member 객체와 연결도 가능하지만 현재는 memberIdx만 사용

    public boolean isOwner(Long userid){
        return this.member.getMemberIdx().equals(userid);
    }

}
