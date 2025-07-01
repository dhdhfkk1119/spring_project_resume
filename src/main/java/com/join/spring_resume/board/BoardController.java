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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final MemberRepository memberRepository;

    // 로그인 체크 + 사용자 조회 메서드
    private Member getLoggedInMember(HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session"); // 세션 키 "session"
        if (sessionUser == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
    }

    // 글쓰기 폼 페이지 (GET)
    @GetMapping("/new")
    public String newBoardForm(HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요한 서비스 입니다");
            return "redirect:/member/login";
        }
        model.addAttribute("isEdit", false); // 글쓰기 모드 표시
        return "board/form";  // 글쓰기/수정 공용 폼 뷰
    }

    // 수정 폼 페이지 (GET)
    @GetMapping("/{id}/edit")
    public String editBoardForm(@PathVariable Long id,
                                Model model,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요한 서비스입니다.");
            return "redirect:/member/login";
        }

        Board board = boardService.findById(id);
        if (!board.isOwner(sessionUser.getId())) {
            redirectAttributes.addFlashAttribute("message", "수정 권한이 없습니다.");
            return "redirect:/board/list";
        }

        model.addAttribute("board", board);
        model.addAttribute("isEdit", true);  // 수정 모드 표시
        return "board/form";  // 글쓰기/수정 공용 폼 뷰
    }

    // 게시글 저장 (POST) - 글쓰기
    @PostMapping
    public String createBoard(@ModelAttribute BoardCreateDto dto,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        try {
            Member member = getLoggedInMember(session);
            boardService.create(dto,member);
            redirectAttributes.addFlashAttribute("message", "게시글이 등록되었습니다.");
            return "redirect:/board/list";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/member/login";
        }
    }

    // 게시글 수정 처리 (POST)
    @PostMapping("/{id}/edit")
    public String updateBoard(@PathVariable Long id,
                              @ModelAttribute BoardUpdateDto dto,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        Member member = getLoggedInMember(session);
        Board board = boardService.findById(id);

        if (!board.isOwner(member.getMemberIdx())) {
            redirectAttributes.addFlashAttribute("message", "수정 권한이 없습니다.");
            return "redirect:/board/list";
        }

        boardService.update(id, dto);
        redirectAttributes.addFlashAttribute("updateMessage", "게시글이 수정되었습니다.");
        return "redirect:/board/list";
    }

    // 게시글 목록 (GET)
    @GetMapping("/list")
    public String listBoards(@PageableDefault(size = 10) Pageable pageable,
                             Model model,
                             HttpSession session) {

        Page<Board> boardPage = boardService.findAll(pageable);
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");

        // 게시글 작성자 여부 표시
        boardPage.forEach(board -> {
            // board.getMemberIdx()는 작성자 ID, sessionUser.getId()는 로그인 회원 ID
            if (sessionUser != null && board.isOwner(sessionUser.getId())) {
                board.setAuthor(true);
            } else {
                board.setAuthor(false);
            }
        });

        model.addAttribute("boardList", boardPage.getContent());
        model.addAttribute("sessionUser", sessionUser);  // 뷰에서 사용
        return "board/list";
    }

    // 게시글 상세보기 (GET)
    @GetMapping("/{id}")
    public String viewBoard(@PathVariable Long id, Model model, HttpSession session) {
        Board board = boardService.findByIdAndIncreaseHits(id);
        if (board.getTags() == null) board.setTags("");
        model.addAttribute("board", board);

        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser != null && board.isOwner(sessionUser.getId())) {
            model.addAttribute("isAuthor", true);
        }
        return "board/detail";
    }

    // 게시글 삭제 (POST)
    @PostMapping("/{id}/delete")
    public String deleteBoard(@PathVariable Long id,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        Member member = getLoggedInMember(session);
        Board board = boardService.findById(id);

        if (!board.isOwner(member.getMemberIdx())) {
            redirectAttributes.addFlashAttribute("message", "삭제 권한이 없습니다.");
            return "redirect:/board/list";
        }

        boardService.delete(id);
        redirectAttributes.addFlashAttribute("deleteMessage", "게시글이 삭제되었습니다.");
        return "redirect:/board/list";
    }
}
