package com.join.spring_resume.comment;

import com.join.spring_resume.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardAndParentIsNullOrderByCreatedAtAsc(Board board);
}