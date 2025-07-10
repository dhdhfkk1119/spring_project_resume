package com.join.spring_resume.Like;

import com.join.spring_resume.board.Board;
import com.join.spring_resume.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    // 1. 특정 회원이 특정 게시글을 좋아요 했는지 확인
    Optional<Like> findByMemberAndBoard(Member member, Board board);

    // 2. 게시글 좋아요 개수
    long countByBoard(Board board);

    // 3. 회원이 좋아요한 게시글 목록 가져오기
    List<Like> findByMember(Member member);
}
