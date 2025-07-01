package com.join.spring_resume.board;


import com.join.spring_resume.member.Member;
import com.join.spring_resume.member.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    // 게시글 저장
    @Transactional
    public Board create(BoardCreateDto dto,Member member) {
        Board board = new Board();
        board.setBoardTitle(dto.getBoardTitle());
        board.setBoardContent(dto.getBoardContent());
        board.setTags(dto.getTags());
        board.setBoardHits(0); // 초기 조회수 0 설정 등 필요에 따라 추가
        board.setMember(member);
        return boardRepository.save(board);
    }

    // 게시글 전체 조회
    public Page<Board> findAll(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    // 게시글 단건 조회
    public Board findById(Long boardIdx) {
        return boardRepository.findById(boardIdx).orElseThrow(() ->
                new IllegalArgumentException("게시글이 존재하지 않습니다") {
                });
    }

    // 게시글 수정
    @Transactional
    public Board update(Long boardIdx, BoardUpdateDto dto) {
        Board board = boardRepository.findById(boardIdx)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        board.setBoardTitle(dto.getBoardTitle());
        board.setBoardContent(dto.getBoardContent());
        board.setTags(dto.getTags());

        return board;
    }


    // 게시글 삭제
    public void delete(Long boardIdx) {
        Board board = findById(boardIdx);
        boardRepository.delete(board);

    }

    // 조회수 증가
    @Transactional
    public Board findByIdAndIncreaseHits(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        boardRepository.increaseHits(id);
        board.setBoardHits(board.getBoardHits() + 1); // optional: 메모리 반영
        return board;
    }


}
