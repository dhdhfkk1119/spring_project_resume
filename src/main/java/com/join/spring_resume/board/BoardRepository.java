package com.join.spring_resume.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {


    @Modifying
    @Query("UPDATE Board b SET b.boardHits = b.boardHits + 1 WHERE b.boardIdx = :id")
    void increaseHits(@Param("id") Long id);

}
