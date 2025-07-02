package com.join.spring_resume.board;

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

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final MemberRepository memberRepository;
    private final CommentService commentService;

    // 로그인 유저 조회
    private Member getLoggedInMember(HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) throw new Exception401("로그인이 필요합니다.");
        return memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
    }

    // 글쓰기 페이지
    @GetMapping("/new")
    public String newBoardForm(HttpSession session, Model model) {
        getLoggedInMember(session); // 로그인 체크
        model.addAttribute("isEdit", false);
        model.addAttribute("boardTitle", "");
        model.addAttribute("boardContent", "");
        model.addAttribute("tags", "");
        return "board/form";
    }

    // 글 수정 폼
    @GetMapping("/{id}/edit")
    public String editBoardForm(@PathVariable Long id, Model model, HttpSession session) {
        Member member = getLoggedInMember(session);
        Board board = boardService.findById(id);
        if (!board.isOwner(member.getMemberIdx())) throw new Exception401("수정 권한이 없습니다.");

        model.addAttribute("isEdit", true);
        model.addAttribute("boardIdx", board.getBoardIdx());
        model.addAttribute("boardTitle", board.getBoardTitle());
        model.addAttribute("boardContent", board.getBoardContent());
        model.addAttribute("tags", board.getTags());
        return "board/form";
    }

    // 글 작성 요청
    @PostMapping
    public String createBoard(@ModelAttribute BoardCreateDto dto, HttpSession session) {
        Member member = getLoggedInMember(session);
        boardService.create(dto, member);
        return "redirect:/board/list";
    }

    // 글 수정 요청
    @PostMapping("/{id}/edit")
    public String updateBoard(@PathVariable Long id,
                              @ModelAttribute BoardUpdateDto dto,
                              HttpSession session) {
        Member member = getLoggedInMember(session);
        Board board = boardService.findById(id);
        if (!board.isOwner(member.getMemberIdx())) throw new Exception401("수정 권한이 없습니다.");

        boardService.update(id, dto);
        return "redirect:/board/list";
    }

    // 글 삭제 요청
    @PostMapping("/{id}/delete")
    public String deleteBoard(@PathVariable Long id, HttpSession session) {
        Member member = getLoggedInMember(session);
        Board board = boardService.findById(id);
        if (!board.isOwner(member.getMemberIdx())) throw new Exception401("삭제 권한이 없습니다.");

        boardService.delete(id);
        return "redirect:/board/list";
    }

    // 게시글 목록
    @GetMapping("/list")
    public String listBoards(@RequestParam(defaultValue = "createdAt") String sort,
                             @RequestParam(defaultValue = "desc") String direction,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Model model, HttpSession session) {

        Sort sorting = direction.equals("asc") ?
                Sort.by(sort).ascending() : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page, size, sorting);
        Page<Board> boardPage = boardService.findAll(pageable);
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");

        boardPage.forEach(board -> {
            board.setAuthor(sessionUser != null && board.isOwner(sessionUser.getId()));
            board.setFormattedCreatedAt(board.getFormattedUpdatedAt());
        });

        model.addAttribute("boardList", boardPage.getContent());
        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        model.addAttribute("isSortCreatedAt", sort.equals("createdAt"));
        model.addAttribute("isSortBoardHits", sort.equals("boardHits"));
        model.addAttribute("isSortBoardTitle", sort.equals("boardTitle"));
        model.addAttribute("isAsc", direction.equals("asc"));
        model.addAttribute("isDesc", direction.equals("desc"));
        return "board/list";
    }

    // 내 게시글 목록
    @GetMapping("/mine")
    private String listMyBoards(@RequestParam(defaultValue = "createdAt") String sort,
                                @RequestParam(defaultValue = "asc") String direction,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                HttpSession session, Model model) {

        Member member = getLoggedInMember(session);
        Sort sorting = direction.equals("asc") ?
                Sort.by(sort).ascending() : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page, size, sorting);
        Page<Board> myBoards = boardService.findByMember(member.getMemberIdx(), pageable);

        myBoards.forEach(board -> {
            board.setAuthor(true);
            board.setFormattedCreatedAt(board.getFormattedCreatedAt());
        });

        model.addAttribute("boardList", myBoards.getContent());
        model.addAttribute("sessionUser", session.getAttribute("session"));
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        model.addAttribute("isSortCreatedAt", sort.equals("createdAt"));
        model.addAttribute("isSortBoardHits", sort.equals("boardHits"));
        model.addAttribute("isSortBoardTitle", sort.equals("boardTitle"));
        model.addAttribute("isAsc", direction.equals("asc"));
        model.addAttribute("isDesc", direction.equals("desc"));
        return "board/list";
    }

    // 게시글 상세 보기 + 댓글 목록 전달
    @GetMapping("/{id}")
    public String viewBoard(@PathVariable Long id, Model model, HttpSession session) {
        Board board = boardService.findByIdAndIncreaseHits(id);
        if (board.getTags() == null) board.setTags("");

        model.addAttribute("board", board);
        model.addAttribute("formattedCreatedAt", board.getFormattedCreatedAt());
        model.addAttribute("formattedUpdatedAt", board.getFormattedUpdatedAt());

        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser != null && board.isOwner(sessionUser.getId())) {
            model.addAttribute("isAuthor", true);
        }

        List<Comment> commentEntities = commentService.findCommentsByBoard(id);
        List<CommentResponseDto> commentDtos = commentEntities.stream().map(parent -> {
            CommentResponseDto dto = new CommentResponseDto(parent);
            for (Comment reply : parent.getReplies()) {
                dto.addReply(new CommentResponseDto(reply));
            }
            return dto;
        }).toList();

        model.addAttribute("comments", commentDtos);
        model.addAttribute("sessionUser", sessionUser);
        return "board/detail";
    }

    // 댓글 작성 및 대댓글
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

    // 예외 클래스
    public static class Exception401 extends RuntimeException {
        public Exception401(String message) {
            super(message);
        }
    }
}
