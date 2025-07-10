package com.join.spring_resume.Like;

import com.join.spring_resume.board.Board;
import com.join.spring_resume.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "LikeEntity")
@Table(name = "likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "board_id"})
})
@Getter
@Setter
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;


    @Column(nullable = false)
    private boolean likeYn = true;

}
