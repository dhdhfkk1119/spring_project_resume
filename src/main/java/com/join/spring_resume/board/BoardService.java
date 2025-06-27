package com.join.spring_resume.board;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    // 게시글 저장
    public Board create(Board board) {
        return boardRepository.save(board);
    }

    // 게시글 전체 조회
    public List<Board> findAll() {
        return boardRepository.findAll();
    }

    // 게시글 단건 조회
    public Optional<Board> findById(Long boardIdx) {
        return boardRepository.findById(boardIdx);
    }



}
