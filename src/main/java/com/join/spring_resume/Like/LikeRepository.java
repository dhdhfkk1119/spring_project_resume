package com.join.spring_resume.Like;

import com.join.spring_resume.board.Board;
import com.join.spring_resume.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like , Long> {
    Optional<Like> findByMemberAndBoard(Member member, Board board);
    long countByBoard(Board board);
    void deleteByMemberAndBoard(Member member, Board board);
}
