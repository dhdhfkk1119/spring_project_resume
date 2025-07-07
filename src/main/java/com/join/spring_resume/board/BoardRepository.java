package com.join.spring_resume.board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("SELECT b FROM Board b JOIN FETCH b.member WHERE b.boardIdx = :id")
    Board findByIdAndIncreaseHits(@Param("id") Long id);

    Page<Board> findByMember_MemberIdx(Long memberIdx, Pageable pageable);
    List<Board> findByMember_MemberIdx(Long memberIdx);
}