package com.join.spring_resume.comment;

import com.join.spring_resume.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 게시글에 달린 부모 댓글(대댓글 아님)만 조회
    List<Comment> findByBoardAndParentIsNullOrderByCreatedAtAsc(Board board);
}
