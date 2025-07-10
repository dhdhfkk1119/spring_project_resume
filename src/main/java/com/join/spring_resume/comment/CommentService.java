package com.join.spring_resume.comment;

import com.join.spring_resume.board.Board;
import com.join.spring_resume.board.BoardHtmlSanitizer;
import com.join.spring_resume.board.BoardRepository;
import com.join.spring_resume.member.Member;
import com.join.spring_resume.member.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Comment writeComment(Long boardId, String content, Long userId, Long parentId, boolean isSecret) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));


        String sanitizedContent = BoardHtmlSanitizer.sanitizeExceptImg(content);

        Comment comment = Comment.builder()
                .comment(sanitizedContent)
                .board(board)
                .user(member)
                .isSecret(isSecret)
                .build();

        if (parentId != null) {
            Comment parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));
            comment.setParent(parent);
        }

        return commentRepository.save(comment);
    }

    public List<Comment> findCommentsByBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        return commentRepository.findByBoardAndParentIsNullOrderByCreatedAtAsc(board);
    }

    public List<CommentResponseDto> getCommentDtoList(Long boardId, Long loginUserId) {
        List<Comment> comments = findCommentsByBoard(boardId);
        List<CommentResponseDto> dtoList = new ArrayList<>();

        for (Comment comment : comments) {
            CommentResponseDto parentDto = new CommentResponseDto(comment, loginUserId);
            for (Comment reply : comment.getReplies()) {
                parentDto.addReply(new CommentResponseDto(reply, loginUserId));
            }
            dtoList.add(parentDto);
        }
        return dtoList;
    }

    public Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
    }

    @Transactional
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }

    @Transactional
    public void updateContent(Long id, String newContent) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));


        String plainText = Jsoup.parse(newContent).text();
        comment.setComment(plainText);
    }

    public List<Board> getBoardsCommented(Member member) {
        return commentRepository.findBoardsCommentedByUser(member);
    }

}
