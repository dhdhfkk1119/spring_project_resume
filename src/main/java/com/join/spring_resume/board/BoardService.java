package com.join.spring_resume.board;

import com.join.spring_resume.member.Member;
import com.join.spring_resume.member.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Page<BoardListResponseDto> searchBoards(String keyword, Pageable pageable) {
        return boardRepository.searchBoardsByKeyword(keyword, pageable);
    }


    // 자신이 작성한 게시물에 대한 정보
    public List<Board> findByMemberIdx(Long memberIdx) {
        return boardRepository.findByMember_MemberIdx(memberIdx);
    }

    public Page<BoardListResponseDto> findBoardListByMemberIdx(Long memberIdx, Pageable pageable) {
        return boardRepository.findBoardListByMemberIdx(memberIdx, pageable);
    }

    public List<BoardListResponseDto> findBoardsLikedByMember(Member member) {
        return boardRepository.findBoardsLikedByMember(member.getMemberIdx());
    }

    public List<BoardListResponseDto> getBoardsCommentedByMember(Member member) {
        return boardRepository.findBoardsCommentedByMember(member.getMemberIdx());
    }

    public Page<BoardListResponseDto> getBoardList(String keyword, Pageable pageable) {
        return boardRepository.findBoardListWithCounts(keyword, pageable);
    }

    public Page<BoardListResponseDto> searchMyBoards(Long memberIdx, String keyword, Pageable pageable) {
        return boardRepository.searchMyBoardsByKeyword(memberIdx, keyword, pageable);
    }

    public Page<BoardListResponseDto> getMyBoards(Long memberIdx, Pageable pageable) {
        Page<BoardListResponseDto> page = boardRepository.findBoardListByMemberIdx(memberIdx, pageable);
        page.forEach(boardListResponseDto -> {
            boardListResponseDto.setAuthor(boardListResponseDto.getMemberIdx().equals(memberIdx));
        });
        return page;
    }

    public Page<BoardListResponseDto> getAllBoards(Pageable pageable) {
        return boardRepository.findAllBoards(pageable);
    }


}
