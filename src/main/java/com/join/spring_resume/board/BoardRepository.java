package com.join.spring_resume.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b FROM Board b JOIN FETCH b.member WHERE b.boardIdx = :id")
    Board findByIdAndIncreaseHits(@Param("id") Long id);

}
