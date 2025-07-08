package com.join.spring_resume.board;

import com.join.spring_resume.member.Member;
import com.join.spring_resume.member.MemberRepository;
import com.join.spring_resume.Like.LikeRepository;
import com.join.spring_resume.comment.CommentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

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

    public Page<Board> searchBoards(String keyword, Pageable pageable) {
        return boardRepository.searchBoards(keyword, pageable);
    }

    // 좋아요/댓글수 포함된 목록 반환
    public Page<BoardListResponseDto> getBoardList(String keyword, Pageable pageable) {
        return boardRepository.findBoardListWithCounts(keyword, pageable);
    }

    // 내가 쓴 글 DTO 목록 반환 (페이징)
    public Page<BoardListResponseDto> findBoardListByMemberIdx(Long memberIdx, Pageable pageable) {
        return boardRepository.findBoardListByMemberIdx(memberIdx, pageable);
    }

    // 내가 좋아요 누른 게시글 목록 반환 (리스트)
    public List<BoardListResponseDto> getBoardsLikedByMember(Member member) {
        Long memberIdx = member.getMemberIdx();
        return boardRepository.findBoardsLikedByMember(memberIdx);
    }

    // 내가 댓글 단 게시글 목록 반환 (리스트)
    public List<BoardListResponseDto> getBoardsCommentedByMember(Member member) {
        Long memberIdx = member.getMemberIdx();
        return boardRepository.findBoardsCommentedByMember(memberIdx);
    }
}
