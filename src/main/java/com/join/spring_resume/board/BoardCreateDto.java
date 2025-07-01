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

    // 엔티티로 변환하는 메서드 (작성자 ID 전달받음)
    public Board toEntity(Member memberIdx) {
        Board board = new Board();
        board.setBoardTitle(this.boardTitle);
        board.setBoardContent(this.boardContent);
        board.setTags(this.tags);
        board.setBoardHits(0);
        board.setMember(memberIdx);
        return board;
    }

}


