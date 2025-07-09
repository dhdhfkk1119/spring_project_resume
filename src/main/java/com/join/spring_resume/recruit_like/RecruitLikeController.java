package com.join.spring_resume.recruit_like;

import com.join.spring_resume.member.Member;
import com.join.spring_resume.member.MemberResponse;
import com.join.spring_resume.member.MemberService;
import com.join.spring_resume.recruit.Recruit;
import com.join.spring_resume.recruit.RecruitService;
import com.join.spring_resume.session.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/like")
public class RecruitLikeController {

    private final RecruitService recruitService;
    private final MemberService memberService;
    private final RecruitLikeService recruitLikeService;

    @PostMapping("/api/likes/{recruitIdx}")
    public ResponseEntity<Map<String, String>> toggleLike(
            @PathVariable(name = "recruitIdx") Long recruitIdx,
            RecruitLikeRequest.SaveDTO saveDTO,
            HttpSession httpSession) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");

        if (sessionUser == null || !"MEMBER".equals(sessionUser.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "unauthorized"));
        }

        Member member = memberService.findById(sessionUser.getId());
        Recruit recruit = recruitService.findById(recruitIdx);

        boolean isLiked = recruitLikeService.likeSaveToggle(saveDTO ,member, recruit);

        Map<String, String> response = new HashMap<>();
        response.put("status", isLiked ? "liked" : "unliked");
        return ResponseEntity.ok(response);
    }


}
