package com.join.spring_resume.board;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public Board findById(Long boardIdx) {
        return boardRepository.findById(boardIdx).orElseThrow(() ->
                new IllegalArgumentException("게시글이 존재하지 않습니다") {
                });
    }

    // 게시글 수정
    public Board update(Long boardIdx, Board updateData) {
        Board board = boardRepository.findById(boardIdx)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        board.setBoardTitle(updateData.getBoardTitle());
        board.setBoardContent(updateData.getBoardContent());
        board.setTags(updateData.getTags());

        return boardRepository.save(board);
    }

    // 게시글 삭제
    public void delete(Long boardIdx) {
        boardRepository.deleteById(boardIdx);
    }

    // 조회수 증가
    @Transactional
    public void increaseHits(Long boardIdx) {
        boardRepository.increaseHits(boardIdx); // 직접 증가시키는 쿼리 호출
    }







}
