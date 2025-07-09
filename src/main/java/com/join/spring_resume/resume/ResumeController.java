package com.join.spring_resume.resume;

import com.join.spring_resume._core.errors.exception.Exception401;
import com.join.spring_resume._core.errors.exception.Exception403;
import com.join.spring_resume._core.errors.exception.Exception404;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class ResumeController {

    private final ResumeService resumeService;
    private final HttpSession session;
    private final MemberRepository memberRepository;

    //이력서 목록 보기
    @GetMapping("/resumes")
    public String list(Model model,
                       @PageableDefault(
                               size = 3,
                               sort = "resumeIdx",
                               direction = Sort.Direction.DESC) Pageable pageable) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) {
            throw new Exception401("로그인 해주시기 바랍니다");
        }
        System.out.println("로그인된 사용자 ID: " + sessionUser.getId());
        if (sessionUser.getRole() != "MEMBER") {
            throw new Exception403("일반 회원만 작성 가능합니다");
        }
        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new Exception404("해당 회원을 찾을수 없습니다"));

        //pageable 객체 전달
        ResumeResponse.ListDTO listDTO = resumeService.findResumesForList(member.getMemberIdx(), pageable);

        //뷰에 데이터 전달
        model.addAttribute("repResume", listDTO.getRepResume());
        model.addAttribute("resumePage", listDTO.getResumePage());
        model.addAttribute("member", member);

        //❗todo 나중에 PageDTO에 들어갈 내용
        int currentPage = listDTO.getResumePage().getNumber();
        int totalPages = listDTO.getResumePage().getTotalPages();
        int startPage = Math.max(0, currentPage - 2);
        int endPage = Math.min(totalPages - 1, currentPage + 2);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("prevPage", listDTO.getResumePage().getNumber() - 1);
        model.addAttribute("nextPage", listDTO.getResumePage().getNumber() + 1);
        //❗todo 여기까지

        return "resume/list";
    }


//    @GetMapping("/resumes")
//    public String list(Model model) {
//        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
//        if (sessionUser == null) {
//            throw  new Exception401("로그인 해주시기 바랍니다");
//        }
//
//        System.out.println("로그인된 사용자 ID: " + sessionUser.getId());
//        if (sessionUser.getRole() != "MEMBER") {
//            throw new Exception403("일반 회원만 작성 가능합니다");
//        }
//        Member member = memberRepository.findById(sessionUser.getId())
//                .orElseThrow(() -> new Exception404("해당 회원을 찾을수 없습니다"));
//
//        //모든 이력서 리스트 호출
//        List<Resume> resumeList = resumeService.findMyResumes(member.getMemberIdx());
//
//        //대표 이력서와 나머지를 분리
//        Resume repResume = resumeList.stream()
//                .filter(r -> Boolean.TRUE.equals(r.getIsRep()))
//                .findFirst()
//                .orElse(null);
//
//        List<Resume> otherResumes = resumeList.stream()
//                .filter(r -> !Boolean.TRUE.equals(r.getIsRep()))
//                .collect(Collectors.toList());
//
//        model.addAttribute("repResume", repResume);
//        model.addAttribute("otherResumes", otherResumes);
//        model.addAttribute("member", member);
//
//        return "resume/list";
//    }

    // 일반 회원을 위한 이력서 상세보기
    @GetMapping("/resume/{id}")
    public String detail(@PathVariable(name = "id") Long resumeIdx, Model model) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) {
            throw new Exception401("로그인 해주시기 바랍니다");
        }
        if (sessionUser.getRole() != "MEMBER") {
            throw new Exception403("일반 회원만 작성 가능합니다");
        }

        Resume resume = resumeService.findByIdWithCareers(resumeIdx);

        if (!resume.isOwner(sessionUser.getId())) {
            throw new Exception403("이력서를 조회할 권한이 없습니다");
        }

        ResumeResponse.CorpDetailDTO responseDTO = new ResumeResponse.CorpDetailDTO(resume);

        model.addAttribute("resume", responseDTO);
        model.addAttribute("isOwner", true);
        return "resume/detail";
    }

    //👨‍💻 기업 채용담당관을 위한 이력서 상세보기
    @GetMapping("/corp/resume/{resumeIdx}")
    public String corpResumeDetail(@PathVariable Long resumeIdx, Model model, HttpSession session) {

        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) {
            throw new Exception401("로그인 해주시기 바랍니다");
        }
        if (sessionUser.getRole() != "CORP") {
            throw new Exception403("기업 회원만 작성 가능합니다");
        }

        ResumeResponse.CorpDetailDTO responseDTO = resumeService.findCorpResumeDetail(resumeIdx);
        model.addAttribute("resume", responseDTO);
        model.addAttribute("isOwner", false);
        return "resume/detail";
    }

    /**
     * 이력서 수정 화면 요청
     */
    @GetMapping("/resume/{id}/update-form")
    public String updateForm(@PathVariable(name = "id") Long resumeIdx, Model model) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) {
            throw new Exception401("로그인 해주시기 바랍니다");
        }
        if (sessionUser.getRole() != "MEMBER") {
            throw new Exception403("일반 회원만 작성 가능합니다");
        }
        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new Exception404("해당 회원을 찾을수 없습니다"));

        Resume resume = resumeService.findByIdWithCareers(resumeIdx);
        model.addAttribute("resume", resume);
        model.addAttribute("member", member);
        return "resume/update-form";
    }


    /**
     * 수정 액션
     */
    @PostMapping("/resume/{id}/update")
    public String update(@PathVariable(name = "id") Long resumeIdx,
                         @Valid ResumeRequest.UpdateDTO updateDTO,
                         BindingResult bindingResult,
                         Model model) {

        SessionUser sessionUser = (SessionUser) session.getAttribute("session");

        //유효성
        Map<String, String> errorMap = new HashMap<>();//에러 바인딩
        if (bindingResult.hasErrors()) {

            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            model.addAttribute("errors", errorMap);
            model.addAttribute("dto", updateDTO);
            return "resume/save-form";
        }


        if (sessionUser == null) {
            throw new Exception401("로그인 해주시기 바랍니다");
        }
        if (sessionUser.getRole() != "MEMBER") {
            throw new Exception403("일반 회원만 작성 가능합니다");
        }
        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new Exception404("해당 회원을 찾을수 없습니다"));

        resumeService.updateById(resumeIdx, updateDTO, member);
        return "redirect:/resume/" + resumeIdx;
    }

    /**
     * 이력서 삭제
     */
    @PostMapping("/resume/{id}/delete")
    public String delete(@PathVariable(name = "id") Long resumeIdx) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) {
            throw new Exception401("로그인 해주시기 바랍니다");
        }
        if (sessionUser.getRole() != "MEMBER") {
            throw new Exception403("일반 회원만 작성 가능합니다");
        }
        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new Exception404("해당 회원을 찾을수 없습니다"));

        resumeService.deleteById(resumeIdx, member);
        return "redirect:/resumes";
    }

    /**
     * 이력서 작성창 요청
     */
    @GetMapping("/resume/save-form")
    public String saveForm(Model model) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) {
            throw new Exception401("로그인 해주시기 바랍니다");
        }
        if (sessionUser.getRole() != "MEMBER") {
            throw new Exception403("일반 회원만 작성 가능합니다");
        }
        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new Exception404("해당 회원을 찾을수 없습니다"));
        model.addAttribute("member", member);

        return "resume/save-form";
    }

    /**
     * 이력서 저장 액션
     * 1.인증검사
     * 2.유효성검사
     * 3.저장 = 서비스위임
     */

    @PostMapping("/resume/save")
    public String save(@Valid ResumeRequest.SaveDTO saveDTO,
                       BindingResult bindingResult,
                       Model model) {

        SessionUser sessionUser = (SessionUser) session.getAttribute("session");

        //유효성
        Map<String, String> errorMap = new HashMap<>();//에러 바인딩
        if (bindingResult.hasErrors()) {

            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            model.addAttribute("errors", errorMap);
            //model.addAttribute("dto", saveDTO);
            return "resume/save-form";
        }

        if (sessionUser == null) {
            throw new Exception401("로그인 해주시기 바랍니다");
        }
        if (sessionUser.getRole() != "MEMBER") {
            throw new Exception403("일반 회원만 작성 가능합니다");
        }
        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new Exception404("해당 회원을 찾을수 없습니다"));

        Resume resume = resumeService.save(saveDTO, member);

        return "redirect:/resume/" + resume.getResumeIdx();
    }

    /**
     * 대표 이력서를 변경하시겠습니까?
     */
    @PostMapping("/resume/{id}/set-rep")
    public String setRepresentative(@PathVariable(name = "id") Long resumeIdx) {

        // 인증검사
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) {
            throw new Exception401("로그인 해주시기 바랍니다");
        }
        // 권한확인
        if (sessionUser.getRole() != "MEMBER") {
            throw new Exception403("일반 회원만 작성 가능합니다");
        }

        // 2. 서비스에 대표 이력서 변경 로직 위임
        resumeService.setRep(sessionUser.getId(), resumeIdx);

        // 3. 다시 이력서 목록 페이지로 리다이렉트
        return "redirect:/resumes";
    }

}//
