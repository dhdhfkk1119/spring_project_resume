package com.join.spring_resume.comment;

import com.join.spring_resume.board.Board;
import com.join.spring_resume.member.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "comment_tb")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String comment;

    // 작성자 (Member 엔티티와 연관)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx", nullable = false)
    private Member user;

    // 댓글이 달린 게시글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    // 부모 댓글 (대댓글 구조)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    // 자식 댓글들 (답글 리스트)
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<Comment> replies = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public Comment(Long id, String comment, Member user, Board board, Comment parent, LocalDateTime createdAt) {
        this.id = id;
        this.comment = comment;
        this.user = user;
        this.board = board;
        this.parent = parent;
        this.createdAt = createdAt;
    }
}
