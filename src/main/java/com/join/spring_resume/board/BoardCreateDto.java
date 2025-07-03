package com.join.spring_resume.board;

import com.join.spring_resume.member.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardCreateDto {
    private String boardTitle;
    private String boardContent;
    private String tags;

    public Board toEntity(Member member) {
        Board board = new Board();
        board.setBoardTitle(this.boardTitle);
        board.setBoardContent(this.boardContent);
        board.setTags(this.tags);
        board.setBoardHits(0);
        board.setMember(member);
        return board;
    }
}