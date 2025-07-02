package com.join.spring_resume.comment;

import com.join.spring_resume.board.Board;
import com.join.spring_resume.board.BoardRepository;
import com.join.spring_resume.member.Member;
import com.join.spring_resume.member.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Comment writeComment(Long boardId, String content, Long userId, Long parentId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        Comment comment = new Comment();
        comment.setComment(content);
        comment.setBoard(board);
        comment.setUser(member);

        if (parentId != null) {
            Comment parentComment = commentRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));
            comment.setParent(parentComment);
        }

        return commentRepository.save(comment);
    }

    public List<Comment> findCommentsByBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        return commentRepository.findByBoardAndParentIsNullOrderByCreatedAtAsc(board);
    }
}
