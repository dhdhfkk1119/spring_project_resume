package com.join.spring_resume.resume;

import com.join.spring_resume.member.Member;
import com.join.spring_resume.member.MemberRepository;
import com.join.spring_resume.member.MemberService;
import com.join.spring_resume.session.SessionUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class ResumeController {

    private final ResumeService resumeService;
    private final MemberRepository memberRepository;
    /**
     * 이력서 메인페이지
     */
    @GetMapping("/resume")
    public String list(Model model, HttpSession session) {
        SessionUser sessionMember = (SessionUser) session.getAttribute("session");
        Member member = memberRepository.findById(sessionMember.getId())
                .orElseThrow();
        List<Resume> resumeList = resumeService.findMyResumes(member.getMemberIdx());
        model.addAttribute("resumeList", resumeList);
        return "/resume/list";
    }

    /**
     * 이력서 수정 화면 요청
     * 2.인증검사
     * 3.게시물 조회 = 서비스 위임
     */
    @GetMapping("/resume/{id}/update-form")
    public String updateForm(@PathVariable(name = "id") Long resumeIdx,
                             HttpSession session, Model model) {
        Member sessionMember = (Member) session.getAttribute("sessionMember");

        resumeService.checkResumeOwner(resumeIdx, sessionMember.getMemberIdx());
        model.addAttribute("resume", resumeService.findById(resumeIdx));
        return "/resume/update-form";
    }


    /**
     * 수정 액션
     * 1.인증검사
     * 2.유효성검사
     * 3.수정 = 서비스 위임
     * 4.리다이렉트
     */
    @PostMapping("/resume/{id}/update-form")
    public String update(@PathVariable(name = "id") Long resumeIdx,
                         HttpSession session, ResumeRequest.UpdateDTO reqDTO) {
        reqDTO.validate();
        Member sessionMember = (Member) session.getAttribute("sessionMember");

        resumeService.updateById(resumeIdx, reqDTO, sessionMember);
        return "redirect:/resume/"+resumeIdx;
    }

    /**
     * 이력서 삭제
     * 1.인증검사
     * 2.세션 로그인 정보 확인
     * 3.삭제 = 서비스위임
     * 4.리다이렉트
     */
    @PostMapping("/resume/{id}/delete")
    public String delete(@PathVariable(name = "id") Long resumeIdx,HttpSession session ,HttpServletRequest request) {

        Member sessionMember = (Member) session.getAttribute("sessionMember");

        resumeService.deleteById(resumeIdx, sessionMember);
        return "redirect:/resume";
    }

    /**
     * 이력서 작성창 요청
     * 1.세션 로그인 정보 확인
     */

    @GetMapping("/resume/save-form")
    public String saveForm(HttpSession session) {
        Member sessionMember = (Member) session.getAttribute("sessionMember");

        return "resume/save-form";
    }

    /**
     * 이력서 저장 액션
     * 1.인증검사
     * 2.유효성검사
     * 3.저장 = 서비스위임
     */

    @PostMapping("/resume/save")
    public String save(ResumeRequest.saveDTO reqDTO, HttpSession session) {
        reqDTO.validate();
        Member sessionMember = (Member) session.getAttribute("sessionMember");

        Resume resume = resumeService.save(reqDTO, sessionMember);
        return "redirect:/resume/"+ resume.getResumeIdx();
    }

    /**
     * 이력서 상세보기 화면 요청
     * 1.인증검사
     * 2.유효성검사
     * 3.저장 = 서비스위임
     */
    @GetMapping("/resume/{id}")
    public String detail(@PathVariable(name = "id") Long resumeIdx,
                         Model model, HttpSession session) {

        Member sessionMember = (Member) session.getAttribute("sessionMember");

        model.addAttribute("resume",resumeService.findById(resumeIdx));
        return "resume/detail";
    }


}//
