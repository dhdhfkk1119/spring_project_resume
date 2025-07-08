package com.join.spring_resume.board;

import com.join.spring_resume.member.Member;
import com.join.spring_resume.util.DateUtil;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

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

    @Column(columnDefinition = "TEXT")
    private String boardContent;

    private String tags;

    private int boardHits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx", nullable = false)
    private Member member;

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

    public String getTags() {
        return tags == null ? "" : tags;
    }

    @SuppressWarnings("unused")
    public void setFormattedCreatedAt(String formattedCreatedAt) {
        this.formattedCreatedAt = formattedCreatedAt;
    }
}
