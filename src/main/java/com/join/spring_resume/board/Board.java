package com.join.spring_resume.board;

import com.join.spring_resume.comment.Comment;
import com.join.spring_resume.Like.Like;
import com.join.spring_resume.member.Member;
import com.join.spring_resume.util.DateUtil;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board_tb")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardIdx;

    private String boardTitle;
    /**
     * string 이나 byte[]  대용량 데이터로 저장하도록 지정 --> Lob
     * LONGTEXT 4GB 까지 저장 가능한 텍스트 타입
     */
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String boardContent;

    private String tags;

    private int boardHits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Transient
    private boolean isAuthor;

    @Transient
    private String formattedCreatedAt;

    public boolean isOwner(Long userid) {
        return this.member != null && this.member.getMemberIdx().equals(userid);
    }

    public String getFormattedCreatedAt() {
        return DateUtil.format(createdAt);
    }

    public String getFormattedUpdatedAt() {
        return DateUtil.format(updatedAt);
    }

    /**
     * tags가 null 이면 빈 문자열 "" 반환
     * 값이 있으면 그대로 반환
     */
    public String getTags() {
        return tags == null ? "" : tags;
    }

    /**
     *  외부에서 setter를 통해 설정하는 경우도 있음 그에 대비해서 열어놓은것
     *  createdAt 을 포맷팅해서 보여주기 위한 필드
     *  템플릿에서 {{formattedCreatedAt}} 사용 시 필수
     * @param formattedCreatedAt
     */
    @SuppressWarnings("unused")
    public void setFormattedCreatedAt(String formattedCreatedAt) {
        this.formattedCreatedAt = formattedCreatedAt;
    }
}
