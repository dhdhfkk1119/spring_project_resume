package com.join.spring_resume.apply;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ApplyController {

    private final ApplyService applyService;
    private final RecruitService recruitService;
    private final ResumeService resumeService;
    private final MemberService memberService;
    // 이력서 지원하기
    @PostMapping("/{recruitIdx}/apply")
    public String apply(@PathVariable(name = "recruitIdx")Long idx,
                        ApplyRequest.SaveDTO saveDTO,
                        HttpSession httpSession){

        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");
        Member member = memberService.findById(sessionUser.getUserId());
        Resume resume = resumeService.findIdMyResumes(member);
        Recruit recruit = recruitService.findById(idx);
        applyService.applySave(saveDTO,recruit,resume);
        return "redirect:/";
    }
}
