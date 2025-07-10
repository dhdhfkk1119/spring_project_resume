package com.join.spring_resume.recruit_like;

import com.join.spring_resume._core.common.PageResponseDTO;
import com.join.spring_resume._core.errors.exception.Exception401;
import com.join.spring_resume.member.Member;
import com.join.spring_resume.member.MemberService;
import com.join.spring_resume.recruit.Recruit;
import com.join.spring_resume.recruit.RecruitService;
import com.join.spring_resume.session.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    
    // 내가 좋아요 누른 공고 보기
    @GetMapping("/recruit-list")
    public String recruitLikeList(HttpSession httpSession,
                                  Model model){
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");
        
        if(sessionUser == null){
            throw  new Exception401("로그인 해주시기 바랍니다");
        }
        if(!"MEMBER".equals(sessionUser.getRole())){
            throw new Exception401("일반 회원만 접근 가능합니다");
        }

        Pageable pageable = PageRequest.of(0,5);
        Page<RecruitLike> recruitLikes = recruitLikeService.recruitLikeList(sessionUser.getId(),pageable);
        model.addAttribute("recruitList",recruitLikes);
        return "recruit/recruit-like";
    }

    // 내가 좋아요 누른 공고 보기 늘리기
    @GetMapping("/api/my-recruit")
    @ResponseBody
    public PageResponseDTO<RecruitLikeResponse.LikeDTO> getLikeRecruit(HttpSession httpSession,
                                                  Model model,
                                                  @RequestParam(name = "page", defaultValue = "0") int page,
                                                  @RequestParam(name = "size", defaultValue = "5") int size){
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");

        if(sessionUser == null){
            throw  new Exception401("로그인 해주시기 바랍니다");
        }
        if(!"MEMBER".equals(sessionUser.getRole())){
            throw new Exception401("일반 회원만 접근 가능합니다");
        }

        Pageable pageable = PageRequest.of(page,size);
        Page<RecruitLike> recruitLikes = recruitLikeService.recruitLikeList(sessionUser.getId(),pageable);
        return PageResponseDTO.from(recruitLikes,RecruitLikeResponse.LikeDTO::fromEntity);
    }


}
