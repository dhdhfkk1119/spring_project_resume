package com.join.spring_resume.board;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardUpdateDto {
    private String boardTitle;
    private String boardContent;
    private String tags;

    // 기존 Board 엔티티에 업데이트 적용
    public void applyTo(Board board) {
        board.setBoardTitle(this.boardTitle);
        board.setBoardContent(this.boardContent);
        board.setTags(this.tags);
    }
}
