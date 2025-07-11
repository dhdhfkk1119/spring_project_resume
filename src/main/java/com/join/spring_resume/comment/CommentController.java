package com.join.spring_resume.comment;

import com.join.spring_resume._core.errors.exception.Exception401;
import com.join.spring_resume._core.errors.exception.Exception403;
import com.join.spring_resume._core.errors.exception.Exception404;
import com.join.spring_resume.member.Member;
import com.join.spring_resume.member.MemberRepository;
import com.join.spring_resume.session.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final MemberRepository memberRepository;

    private Member getLoggedInMember(HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) {
            throw new Exception401("로그인이 필요합니다.");
        }
        return memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new Exception404("유저를 찾을 수 없습니다."));
    }

    @PostMapping("/{id}/delete")
    public String deleteComment(@PathVariable Long id, HttpSession session) {
        Member member = getLoggedInMember(session);
        Comment comment = commentService.findById(id);

        if (!comment.getUser().getMemberIdx().equals(member.getMemberIdx())) {
            throw new Exception403("댓글 삭제 권한이 없습니다.");
        }

        Long boardId = comment.getBoard().getBoardIdx();
        commentService.deleteById(id);
        return "redirect:/board/" + boardId;
    }

    @PostMapping("/{id}/edit")
    public String editComment(@PathVariable Long id,
                              @RequestParam String content,
                              HttpSession session) {
        Member member = getLoggedInMember(session);
        Comment comment = commentService.findById(id);


        if (!comment.getUser().getMemberIdx().equals(member.getMemberIdx())) {
            throw new Exception403("댓글 수정 권한이 없습니다.");
        }
        // HTML 태그 제거
        String plainText = Jsoup.parse(content).text();

        commentService.updateContent(id, content);
        return "redirect:/board/" + comment.getBoard().getBoardIdx();
    }

    @PostMapping("/{boardId}/comment")
    public String writeComment(@PathVariable Long boardId,
                               @RequestParam String content,
                               @RequestParam(required = false) Long parentId,
                               @RequestParam(defaultValue = "false") boolean isSecret,
                               HttpSession session) {
        Member member = getLoggedInMember(session);
        // HTML 태그 제거
        String plainText = Jsoup.parse(content).text();
        commentService.writeComment(boardId, content, member.getMemberIdx(), parentId, isSecret);
        return "redirect:/board/" + boardId;
    }


}