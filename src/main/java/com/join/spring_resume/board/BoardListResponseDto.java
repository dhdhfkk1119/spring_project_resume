package com.join.spring_resume.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardListResponseDto {

    private Long boardIdx;
    private String boardTitle;
    private String boardContent;
    private int boardHits;
    private LocalDateTime createdAt;

    private Long commentCount;
    private Long likeCount;

    private Long memberIdx;
    private String username;

    private boolean author;


    private String formattedCreatedAt;

    public BoardListResponseDto(Long boardIdx, String boardTitle, String boardContent, Integer boardHits,
                                LocalDateTime createdAt, Long commentCount, Long likeCount,
                                Long memberIdx, String username) {
        this.boardIdx = boardIdx;
        this.boardTitle = boardTitle;
        this.boardContent = boardContent;
        this.boardHits = boardHits;
        this.createdAt = createdAt;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.memberIdx = memberIdx;
        this.username = username;
    }
    public String getFormattedCreatedAt() {
        if (formattedCreatedAt != null && !formattedCreatedAt.isEmpty()) {
            return formattedCreatedAt;
        }
        if (createdAt == null) return "";
        return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }


}
