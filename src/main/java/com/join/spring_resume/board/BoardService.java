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

    @Transactional
    public Board create(BoardCreateDto dto, Member member) {
        if (dto.getBoardTitle() == null || dto.getBoardContent() == null) {
            throw new IllegalArgumentException("제목과 내용을 입력해주세요");
        }
        return boardRepository.save(dto.toEntity(member));
    }

    public Page<Board> findAll(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    public Board findById(Long boardIdx) {
        return boardRepository.findById(boardIdx)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다"));
    }

    @Transactional
    public void update(Long boardId, BoardUpdateDto dto) {
        Board board = findById(boardId);
        if (dto.getBoardTitle() == null || dto.getBoardContent() == null) {
            throw new IllegalArgumentException("제목과 내용을 입력해주세요.");
        }
        dto.applyTo(board);
    }

    public void delete(Long boardIdx) {
        Board board = findById(boardIdx);
        boardRepository.delete(board);
    }

    @Transactional
    public Board findByIdAndIncreaseHits(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        board.setBoardHits(board.getBoardHits() + 1);
        return board;
    }

    public Page<Board> findByMemberIdx(Long memberIdx, Pageable pageable) {
        return boardRepository.findByMember_MemberIdx(memberIdx, pageable);
    }

    // 수정된 부분: searchBoards는 repository의 searchBoards 메서드 호출
    public Page<Board> searchBoards(String keyword, Pageable pageable) {
        return boardRepository.searchBoards(keyword, pageable);
    }

}
