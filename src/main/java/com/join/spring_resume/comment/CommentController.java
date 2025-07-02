package com.join.spring_resume.comment;

import com.join.spring_resume.member.Member;
import com.join.spring_resume.member.MemberRepository;
import com.join.spring_resume.session.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final MemberRepository memberRepository;

    private Member getLoggedInMember(HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }
        return memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
    }

    // 댓글 작성 및 답글 작성
    @PostMapping("/write")
    public String writeComment(@RequestParam Long boardId,
                               @RequestParam String content,
                               @RequestParam(required = false) Long parentId,
                               HttpSession session) {

        Member member = getLoggedInMember(session);
        commentService.writeComment(boardId, content, member.getMemberIdx(), parentId);

        return "redirect:/board/" + boardId;
    }
}
