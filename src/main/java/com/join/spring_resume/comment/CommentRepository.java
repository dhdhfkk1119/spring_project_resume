package com.join.spring_resume.comment;

import com.join.spring_resume.board.Board;
import com.join.spring_resume.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {


    List<Comment> findByBoardAndParentIsNullOrderByCreatedAtAsc(Board board);


    @Query("SELECT DISTINCT c.board FROM Comment c WHERE c.user = :user ")
    List<Board> findBoardsCommentedByUser(@Param("user") Member member);
}
