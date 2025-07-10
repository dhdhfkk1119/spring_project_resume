package com.join.spring_resume.board;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class BoardListResponseDto {

    private Long boardIdx;
    private String boardTitle;
    private String boardContent;
    private Integer boardHits;
    private LocalDateTime createdAt;


    private Long commentCount;


    private Long likeCount;

    private Long memberIdx;
    private String username;


    private boolean author;


    public BoardListResponseDto(Long boardIdx,
                                String boardTitle,
                                String boardContent,
                                Integer boardHits,
                                LocalDateTime createdAt,
                                Long commentCount,
                                Long likeCount,
                                Long memberIdx,
                                String username,
                                boolean author) {
        this.boardIdx = boardIdx;
        this.boardTitle = boardTitle;
        this.boardContent = boardContent;
        this.boardHits = boardHits != null ? boardHits : 0;
        this.createdAt = createdAt;
        this.commentCount = commentCount != null ? commentCount : 0L;
        this.likeCount = likeCount != null ? likeCount : 0L;
        this.memberIdx = memberIdx;
        this.username = username;
        this.author = author;
    }


    public String getFormattedCreatedAt() {
        if (createdAt == null) return "";
        return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }


    public void setFormattedCreatedAt(String formattedDate) {
        // mustache 오류 방지를 위해 남겨둠
        // {{formattedCreatedAt}} <-- 사용 하고 있어서
    }
}
