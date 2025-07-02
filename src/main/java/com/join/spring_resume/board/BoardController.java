package com.join.spring_resume.board;

import com.join.spring_resume.member.Member;
import com.join.spring_resume.member.MemberRepository;
import com.join.spring_resume.session.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final MemberRepository memberRepository;

    private Member getLoggedInMember(HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) {
            throw new Exception401("로그인이 필요합니다.");
        }
        return memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
    }

    @GetMapping("/new")
    public String newBoardForm(HttpSession session, Model model) {
        getLoggedInMember(session); // 로그인 체크
        model.addAttribute("isEdit", false);
        model.addAttribute("boardTitle", "");
        model.addAttribute("boardContent", "");
        model.addAttribute("tags", "");
        return "board/form";
    }

    @GetMapping("/{id}/edit")
    public String editBoardForm(@PathVariable Long id,
                                Model model,
                                HttpSession session) {
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

    @PostMapping
    public String createBoard(@ModelAttribute BoardCreateDto dto,
                              HttpSession session) {
        Member member = getLoggedInMember(session);
        boardService.create(dto, member);
        return "redirect:/board/list";
    }

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

    @GetMapping("/list")
    public String listBoards(@PageableDefault(size = 10) Pageable pageable,
                             Model model,
                             HttpSession session) {

        Page<Board> boardPage = boardService.findAll(pageable);
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");

        boardPage.forEach(board -> {
            if (sessionUser != null && board.isOwner(sessionUser.getId())) {
                board.setAuthor(true);
            } else {
                board.setAuthor(false);
            }
            board.setFormattedCreatedAt(board.getFormattedUpdatedAt());
        });

        model.addAttribute("boardList", boardPage.getContent());
        model.addAttribute("sessionUser", sessionUser);
        return "board/list";
    }

    @GetMapping("/{id}")
    public String viewBoard(@PathVariable Long id, Model model, HttpSession session) {
        Board board = boardService.findByIdAndIncreaseHits(id);
        if (board.getTags() == null) board.setTags("");
        model.addAttribute("board", board);

        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser != null && board.isOwner(sessionUser.getId())) {
            model.addAttribute("isAuthor", true);
        }

        model.addAttribute("formattedCreatedAt", board.getFormattedCreatedAt());
        model.addAttribute("formattedUpdatedAt", board.getFormattedUpdatedAt());
        model.addAttribute("sessionUser", sessionUser);

        return "board/detail";
    }

    @PostMapping("/{id}/delete")
    public String deleteBoard(@PathVariable Long id,
                              HttpSession session) {
        Member member = getLoggedInMember(session);
        Board board = boardService.findById(id);

        if (!board.isOwner(member.getMemberIdx())) {
            throw new Exception401("삭제 권한이 없습니다.");
        }

        boardService.delete(id);
        return "redirect:/board/list";
    }

    // 예외 정의 (내부에 둬도 되지만 별도 파일 권장)
    public static class Exception401 extends RuntimeException {
        public Exception401(String message) {
            super(message);
        }
    }

    @GetMapping("/my")
    private String listMyBoards(@PageableDefault(size = 10) Pageable pageable,
                                HttpSession session,
                                Model model) {
        Member member = getLoggedInMember(session);
        Page<Board> myBoards = boardService.findByMember(member.getMemberIdx(), pageable);
        myBoards.forEach(board -> {
            board.setAuthor(true); // 내거만 조회니까 true
            board.setFormattedCreatedAt(board.getFormattedUpdatedAt());
        });
        model.addAttribute("boardList", myBoards.getContent());
        model.addAttribute("sessionUser", session.getAttribute("session"));
        return "board/list";
    }
}
