package com.join.spring_resume.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    // 게시글 상세 보기
    @GetMapping("/{id}")
    public String viewBoard(@PathVariable Long id, Model model) {
        Board board = boardService.findById(id); // 예외는 서비스에서 처리
        model.addAttribute("board", board);
        return "board/detail"; // templates/board/detail.html 로 연결됨
    }
}
