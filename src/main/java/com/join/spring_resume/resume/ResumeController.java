package com.join.spring_resume.resume;

import com.join.spring_resume.member.Member;
import com.join.spring_resume.member.MemberRepository;
import com.join.spring_resume.session.SessionUser;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class ResumeController {

    private final ResumeService resumeService;
    private final HttpSession session;
    private final MemberRepository memberRepository;

    /**
     * 이력서 목록 보기
     */
    @GetMapping("/resumes")
    public String list(Model model) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) return "redirect:/login-form";

        System.out.println("로그인된 사용자 ID: " + sessionUser.getId());
        if (sessionUser.getRole() != "MEMBER") {
            System.out.println("일반 회원만 작성 가능합니다");
            return "redirect:/";
        }
        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new RuntimeException("해당 회원을 찾을수 없습니다"));

        //세션에서 가져온 memberIdx 회원이력서 전체조회
        List<Resume> resumeList = resumeService.findMyResumes(member.getMemberIdx());
        model.addAttribute("resumes", resumeList);
        return "resume/list";
    }

    /**
     * 이력서 상세보기 화면 요청
     */
    @GetMapping("/resume/{id}")
    public String detail(@PathVariable(name = "id") Long resumeIdx, Model model) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) return "redirect:/login-form";
        if (sessionUser.getRole() != "MEMBER") {
            System.out.println("일반 회원만 작성 가능합니다");
            return "redirect:/";
        }
        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new RuntimeException("해당 회원을 찾을수 없습니다"));

        Resume resume = resumeService.findByIdWithCareers(resumeIdx);
        model.addAttribute("resume", resume);
        return "resume/detail";
    }

    /**
     * 이력서 수정 화면 요청
     */
    @GetMapping("/resume/{id}/update-form")
    public String updateForm(@PathVariable(name = "id") Long resumeIdx, Model model) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) return "redirect:/login-form";
        if (sessionUser.getRole() != "MEMBER") {
            System.out.println("일반 회원만 작성 가능합니다");
            return "redirect:/";
        }
        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new RuntimeException("해당 회원을 찾을수 없습니다"));

        Resume resume = resumeService.findByIdWithCareers(resumeIdx);
        model.addAttribute("resume", resume);
        return "resume/update-form";
    }


    /**
     * 수정 액션
     */
    @PostMapping("/resume/{id}/update")
    public String update(@PathVariable(name = "id") Long resumeIdx, @Valid ResumeRequest.UpdateDTO updateDTO) {

        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) return "redirect:/login-form";
        if (sessionUser.getRole() != "MEMBER") {
            System.out.println("일반 회원만 작성 가능합니다");
            return "redirect:/";
        }
        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new RuntimeException("해당 회원을 찾을수 없습니다"));

        resumeService.updateById(resumeIdx, updateDTO, member);
        return "redirect:/resume/" + resumeIdx;
    }

    /**
     * 이력서 삭제
     */
    @PostMapping("/resume/{id}/delete")
    public String delete(@PathVariable(name = "id") Long resumeIdx) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) return "redirect:/login-form";
        if (sessionUser.getRole() != "MEMBER") {
            System.out.println("일반 회원만 작성 가능합니다");
            return "redirect:/";
        }
        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new RuntimeException("해당 회원을 찾을수 없습니다"));

        resumeService.deleteById(resumeIdx, member);
        return "redirect:/resumes";
    }

    /**
     * 이력서 작성창 요청
     */
    @GetMapping("/resume/save-form")
    public String saveForm() {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) return "redirect:/login-form";
        if (sessionUser.getRole() != "MEMBER") {
            System.out.println("일반 회원만 작성 가능합니다");
            return "redirect:/";
        }
        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new RuntimeException("해당 회원을 찾을수 없습니다"));

        return "resume/save-form";
    }

    /**
     * 이력서 저장 액션
     * 1.인증검사
     * 2.유효성검사
     * 3.저장 = 서비스위임
     */

    @PostMapping("/resume/save")
    public String save(@Valid ResumeRequest.SaveDTO saveDTO, BindingResult bindingResult) {

        SessionUser sessionUser = (SessionUser) session.getAttribute("session");

        if(bindingResult.hasErrors()){
            HashMap<String,String> errors = new HashMap<>();
            return "resume/save-form";
        }
        if (sessionUser == null) return "redirect:/login-form";
        if (sessionUser.getRole() != "MEMBER") {
            System.out.println("일반 회원만 작성 가능합니다");
            return "redirect:/";
        }
        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new RuntimeException("해당 회원을 찾을수 없습니다"));

        Resume resume = resumeService.save(saveDTO, member);
        return "redirect:/resume/" + resume.getResumeIdx();
    }

}//
