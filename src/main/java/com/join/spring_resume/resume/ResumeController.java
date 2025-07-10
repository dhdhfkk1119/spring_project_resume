package com.join.spring_resume.resume;

import com.join.spring_resume._core.common.PageNumberDto;
import com.join.spring_resume._core.errors.exception.Exception403;
import com.join.spring_resume._core.errors.exception.Exception404;
import com.join.spring_resume._core.interceptor.Auth; // Auth 어노테이션 import
import com.join.spring_resume.member.Member;
import com.join.spring_resume.member.MemberRepository;
import com.join.spring_resume.session.SessionUser;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class ResumeController {

    private final ResumeService resumeService;
    private final MemberRepository memberRepository;

    @Auth // 개인 회원만 접근 가능
    @GetMapping("/resumes")
    public String list(Model model, HttpSession session,
                       @PageableDefault(size = 3, sort = "resumeIdx", direction = Sort.Direction.DESC) Pageable pageable) {
        // 1. 세션에서 사용자 정보 가져오기 (인증/권한 검사는 @Auth가 처리)
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new Exception404("해당 회원을 찾을 수 없습니다"));

        // 2. 서비스 호출
        ResumeResponse.ListDTO listDTO = resumeService.findResumesForList(member.getMemberIdx(), pageable);

        // 3. 뷰에 데이터 전달
        PageNumberDto.PageNavigation navigation = PageNumberDto.createNavigation(listDTO.getResumePage());

        model.addAttribute("repResume", listDTO.getRepResume());
        model.addAttribute("resumePage", listDTO.getResumePage());
        model.addAttribute("member", member);
        model.addAttribute("navigation", navigation);
        model.addAttribute("totalCount", listDTO.getTotalCount());

        return "resume/list";
    }

    @Auth // 개인 회원만 접근 가능
    @GetMapping("/resume/{id}")
    public String detail(@PathVariable(name = "id") Long resumeIdx, Model model, HttpSession session) {
        // 1. 세션에서 사용자 정보 가져오기
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");

        // 2. 서비스 호출
        Resume resume = resumeService.findByIdWithCareers(resumeIdx);

        // 3. 소유권 확인 (이력서 주인만 볼 수 있도록)
        if (!resume.isOwner(sessionUser.getId())) {
            throw new Exception403("이력서를 조회할 권한이 없습니다");
        }

        // 4. 뷰에 데이터 전달
        ResumeResponse.CorpDetailDTO responseDTO = new ResumeResponse.CorpDetailDTO(resume);
        model.addAttribute("resume", responseDTO);
        model.addAttribute("isOwner", true);
        return "resume/detail";
    }

    @Auth(role = "CORP") // 기업 회원만 접근 가능
    @GetMapping("/corp/resume/{resumeIdx}")
    public String corpResumeDetail(@PathVariable Long resumeIdx, Model model) {

        ResumeResponse.CorpDetailDTO responseDTO = resumeService.findCorpResumeDetail(resumeIdx);

        model.addAttribute("resume", responseDTO);
        model.addAttribute("isOwner", false);
        return "resume/detail";
    }

    @Auth // 개인 회원만 접근 가능
    @GetMapping("/resume/{id}/update-form")
    public String updateForm(@PathVariable(name = "id") Long resumeIdx, Model model, HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new Exception404("해당 회원을 찾을 수 없습니다"));

        ResumeResponse.UpdateFormDTO resumeDTO = resumeService.findResumeForUpdateForm(resumeIdx);

        model.addAttribute("resume", resumeDTO);
        model.addAttribute("member", member);
        return "resume/update-form";
    }

    @Auth // 개인 회원만 접근 가능
    @PostMapping("/resume/{id}/update")
    public String update(@PathVariable(name = "id") Long resumeIdx,
                         @Valid ResumeRequest.UpdateDTO updateDTO,
                         BindingResult bindingResult,
                         Model model, HttpSession session) {

        SessionUser sessionUser = (SessionUser) session.getAttribute("session");

        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            model.addAttribute("errors", errorMap);
            model.addAttribute("dto", updateDTO);

            ResumeResponse.UpdateFormDTO resumeDTO = resumeService.findResumeForUpdateForm(resumeIdx);
            model.addAttribute("resume", resumeDTO);
            return "resume/update-form";
        }

        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new Exception404("해당 회원을 찾을 수 없습니다"));
        resumeService.updateById(resumeIdx, updateDTO, member);

        return "redirect:/resume/" + resumeIdx;
    }

    @Auth // 개인 회원만 접근 가능
    @PostMapping("/resume/{id}/delete")
    public String delete(@PathVariable(name = "id") Long resumeIdx, HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new Exception404("해당 회원을 찾을 수 없습니다"));

        resumeService.deleteById(resumeIdx, member);

        return "redirect:/resumes";
    }

    @Auth // 개인 회원만 접근 가능
    @GetMapping("/resume/save-form")
    public String saveForm(Model model, HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new Exception404("해당 회원을 찾을 수 없습니다"));

        model.addAttribute("member", member);
        return "resume/save-form";
    }

    @Auth // 개인 회원만 접근 가능
    @PostMapping("/resume/save")
    public String save(@Valid ResumeRequest.SaveDTO saveDTO,
                       BindingResult bindingResult,
                       Model model, HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");

        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            model.addAttribute("errors", errorMap);

            Member member = memberRepository.findById(sessionUser.getId())
                    .orElseThrow(() -> new Exception404("해당 회원을 찾을 수 없습니다"));
            model.addAttribute("member", member);

            return "resume/save-form";
        }

        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new Exception404("해당 회원을 찾을 수 없습니다"));
        Resume resume = resumeService.save(saveDTO, member);

        return "redirect:/resume/" + resume.getResumeIdx();
    }

    @Auth // 개인 회원만 접근 가능
    @PostMapping("/resume/{id}/set-rep")
    public String setRepresentative(@PathVariable(name = "id") Long resumeIdx, HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");

        resumeService.setRep(sessionUser.getId(), resumeIdx);
        return "redirect:/resumes";
    }
}