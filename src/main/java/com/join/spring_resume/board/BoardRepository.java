package com.join.spring_resume.board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b FROM Board b JOIN FETCH b.member WHERE b.boardIdx = :id")
    Board findByIdWithMember(@Param("id") Long id);

    // 회원별 게시글 조회 페이징
    Page<Board> findByMember_MemberIdx(Long memberIdx, Pageable pageable);

    // 검색어로 제목 또는 내용 검색
    @Query("SELECT b FROM Board b WHERE b.boardTitle LIKE %:keyword% OR b.boardContent LIKE %:keyword%")
    Page<Board> searchBoards(@Param("keyword") String keyword, Pageable pageable);
}
