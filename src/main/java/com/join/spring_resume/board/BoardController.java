package com.join.spring_resume.board;

import com.join.spring_resume.Like.LikeService;
import com.join.spring_resume.comment.Comment;
import com.join.spring_resume.comment.CommentResponseDto;
import com.join.spring_resume.comment.CommentService;
import com.join.spring_resume.member.Member;
import com.join.spring_resume.member.MemberRepository;
import com.join.spring_resume.session.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.join.spring_resume.util.DateUtil.format;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final MemberRepository memberRepository;
    private final CommentService commentService;
    private final LikeService likeService;

    // 로그인 유저 조회, 없으면 예외 발생
    private Member getLoggedInMember(HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) {
            throw new Exception401("로그인이 필요합니다.");
        }
        return memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
    }

    // 게시글 목록 + 페이징 + 정렬
    @GetMapping("/list")
    public String listBoards(@RequestParam(defaultValue = "createdAt") String sort,
                             @RequestParam(defaultValue = "desc") String direction,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Model model,
                             HttpSession session) {

        Sort sorting = direction.equalsIgnoreCase("asc") ?
                Sort.by(sort).ascending() :
                Sort.by(sort).descending();

        Pageable pageable = PageRequest.of(page, size, sorting);
        Page<Board> boardPage = boardService.findAll(pageable);

        SessionUser sessionUser = (SessionUser) session.getAttribute("session");

        boardPage.forEach(board -> {
            board.setAuthor(sessionUser != null && board.isOwner(sessionUser.getId()));
            board.setFormattedCreatedAt(board.getFormattedCreatedAt());
        });

        model.addAttribute("boardList", boardPage.getContent());
        model.addAttribute("page", boardPage);
        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);

        return "board/list";
    }

    // 글쓰기 페이지 폼
    @GetMapping("/new")
    public String newBoardForm(HttpSession session, Model model) {
        getLoggedInMember(session); // 로그인 체크
        model.addAttribute("isEdit", false);
        model.addAttribute("boardTitle", "");
        model.addAttribute("boardContent", "");
        model.addAttribute("tags", "");
        return "board/form";
    }

    // 글 작성 요청 처리
    @PostMapping
    public String createBoard(@ModelAttribute BoardCreateDto dto, HttpSession session) {
        Member member = getLoggedInMember(session);
        boardService.create(dto, member);
        return "redirect:/board/list";
    }

    // 글 수정 폼
    @GetMapping("/{id}/edit")
    public String editBoardForm(@PathVariable Long id, Model model, HttpSession session) {
        Member member = getLoggedInMember(session);
        Board board = boardService.findById(id);

        if (!board.isOwner(member.getMemberIdx())) {
            throw new Exception401("수정 권한이 없습니다.");
        }

        model.addAttribute("isEdit", true);
        model.addAttribute("boardIdx", board.getBoardIdx());
        model.addAttribute("boardTitle", board.getBoardTitle());
        model.addAttribute("boardContent", board.getBoardContent());
        model.addAttribute("tags", board.getTags());
        return "board/form";
    }

    // 글 수정 요청 처리
    @PostMapping("/{id}/edit")
    public String updateBoard(@PathVariable Long id,
                              @ModelAttribute BoardUpdateDto dto,
                              HttpSession session) {

        Member member = getLoggedInMember(session);
        Board board = boardService.findById(id);

        if (!board.isOwner(member.getMemberIdx())) {
            throw new Exception401("수정 권한이 없습니다.");
        }

        boardService.update(id, dto);
        return "redirect:/board/list";
    }

    // 글 삭제 요청 처리
    @PostMapping("/{id}/delete")
    public String deleteBoard(@PathVariable Long id, HttpSession session) {
        Member member = getLoggedInMember(session);
        Board board = boardService.findById(id);

        if (!board.isOwner(member.getMemberIdx())) {
            throw new Exception401("삭제 권한이 없습니다.");
        }

        boardService.delete(id);
        return "redirect:/board/list";
    }

    // 게시글 상세 페이지 (댓글 + 좋아요)
    @GetMapping("/{id}")
    public String viewBoard(@PathVariable Long id, Model model, HttpSession session) {
        Board board = boardService.findByIdAndIncreaseHits(id);
        if (board.getTags() == null) board.setTags("");

        model.addAttribute("board", board);
        model.addAttribute("formattedCreatedAt", board.getFormattedCreatedAt());
        model.addAttribute("formattedUpdatedAt", board.getFormattedUpdatedAt());

        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        Long loginUserId = sessionUser != null ? sessionUser.getId() : null;

        if (sessionUser != null && board.isOwner(sessionUser.getId())) {
            model.addAttribute("isAuthor", true);
        }

        // 댓글 처리
        List<Comment> commentEntities = commentService.findCommentsByBoard(id);
        List<CommentResponseDto> commentDtos = commentEntities.stream()
                .map(parent -> {
                    CommentResponseDto dto = new CommentResponseDto(parent, loginUserId);
                    for (Comment reply : parent.getReplies()) {
                        dto.addReply(new CommentResponseDto(reply, loginUserId));
                    }
                    return dto;
                }).toList();

        model.addAttribute("comments", commentDtos);
        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("loginUserId", loginUserId);

        // 좋아요 처리
        boolean isLiked = false;
        long likeCount = likeService.countLikes(board);
        if (sessionUser != null) {
            Member member = memberRepository.findById(sessionUser.getId()).orElseThrow();
            isLiked = likeService.isLiked(member, board);
        }
        model.addAttribute("isLiked", isLiked);
        model.addAttribute("likeCount", likeCount);

        return "board/detail";
    }

    // 댓글 작성 및 대댓글 처리
    @PostMapping("/{boardId}/comment")
    public String writeComment(@PathVariable Long boardId,
                               @RequestParam String content,
                               @RequestParam(required = false) Long parentId,
                               HttpSession session) {

        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) return "redirect:/login-form";

        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        commentService.writeComment(boardId, content, member.getMemberIdx(), parentId);
        return "redirect:/board/" + boardId;
    }

    // 좋아요 토글
    @PostMapping("/{boardId}/like")
    public String toggleLike(@PathVariable Long boardId, HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) {
            return "redirect:/login-form";
        }

        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        Board board = boardService.findById(boardId);

        likeService.toggleLike(member, board);

        return "redirect:/board/" + boardId;
    }

    // 커스텀 예외 클래스
    public static class Exception401 extends RuntimeException {
        public Exception401(String message) {
            super(message);
        }
    }
}
