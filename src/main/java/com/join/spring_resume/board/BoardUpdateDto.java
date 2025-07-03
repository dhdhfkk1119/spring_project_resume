package com.join.spring_resume.board;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardUpdateDto {
    private String boardTitle;
    private String boardContent;
    private String tags;

    public void applyTo(Board board) {
        board.setBoardTitle(this.boardTitle);
        board.setBoardContent(this.boardContent);
        board.setTags(this.tags);
    }
}