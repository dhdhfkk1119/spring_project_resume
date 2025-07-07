package com.join.spring_resume.apply;

import com.join.spring_resume._core.errors.exception.Exception401;
import com.join.spring_resume._core.errors.exception.Exception403;
import com.join.spring_resume.corp.Corp;
import com.join.spring_resume.member.Member;
import com.join.spring_resume.member.MemberService;
import com.join.spring_resume.recruit.Recruit;
import com.join.spring_resume.recruit.RecruitService;
import com.join.spring_resume.resume.Resume;
import com.join.spring_resume.resume.ResumeService;
import com.join.spring_resume.session.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ApplyController {

    private final ApplyService applyService;
    private final RecruitService recruitService;
    private final ResumeService resumeService;
    private final MemberService memberService;

    // 이력서 지원하기
    @PostMapping("/{recruitIdx}/apply")
    @ResponseBody
    public String apply(@PathVariable(name = "recruitIdx") Long idx,
                        ApplyRequest.SaveDTO saveDTO,
                        HttpSession httpSession) {

        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");
        if (sessionUser == null) {
            // 로그인 안 된 경우
            throw new Exception401("로그인해주시기 바랍니다");
        }

        if (!"MEMBER".equals(sessionUser.getRole())) {
            throw new Exception403("일반 회원만 접근 가능합니다");
        }

        Member member = memberService.findById(sessionUser.getUserId()); // 현재 로그인 한 유저 정보 찾기
        Recruit recruit = recruitService.findById(idx); // 현재 공고 번호 가져오기 
        Resume resume = resumeService.findIdMyResumes(member); // 이력서에서 대표이력서 있는지 체크
        applyService.applySave(saveDTO, recruit, resume);

        // 지원 완료 alert 후 메인 페이지 이동
        return "<script>alert('지원이 완료되었습니다.'); window.location.href='/';</script>";


    }

    @GetMapping("/apply/status")
    public String applyList(Model model,HttpSession httpSession){
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");

        if (sessionUser == null) {
            // 로그인 안 된 경우
            throw new Exception401("로그인해주시기 바랍니다");
        }

        if (!"MEMBER".equals(sessionUser.getRole())) {
            throw new Exception403("일반 회원만 접근 가능합니다");
        }

        List<Apply> applyRecruitList = applyService.MyApplyList(sessionUser.getId());

        model.addAttribute("recruitList",applyRecruitList);
        return "apply/apply-list";
    }



}
