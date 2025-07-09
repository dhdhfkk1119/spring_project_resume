package com.join.spring_resume.resume;

import com.join.spring_resume._core.common.PageNumberDto;
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
import java.util.Map;

/**
 * 📄 이력서 관련 요청을 처리하는 컨트롤러
 * (목록, 상세, 작성, 수정, 삭제)
 */
@RequiredArgsConstructor
@Controller
public class ResumeController {

    private final ResumeService resumeService;
    private final HttpSession session;
    private final MemberRepository memberRepository;

    /**
     * 📚 [GET] /resumes : (개인) 이력서 목록 페이지
     * - 로그인한 회원의 이력서 목록을 페이징하여 보여줍니다.
     * - 대표 이력서는 별도로 상단에 표시합니다.
     */
    @GetMapping("/resumes")
    public String list(Model model,
                       @PageableDefault(size = 3, sort = "resumeIdx", direction = Sort.Direction.DESC) Pageable pageable) {
        // 1. 👤 인증 및 권한 검사
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) {
            throw new Exception401("로그인 해주시기 바랍니다");
        }
        if (!"MEMBER".equals(sessionUser.getRole())) {
            throw new Exception403("개인 회원만 접근 가능합니다");
        }
        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new Exception404("해당 회원을 찾을 수 없습니다"));

        // 2. 🔄 서비스 호출 (이력서 목록 조회)
        ResumeResponse.ListDTO listDTO = resumeService.findResumesForList(member.getMemberIdx(), pageable);

        // 3. 🔢 페이지네이션 및 카운트 계산
        PageNumberDto.PageNavigation navigation = PageNumberDto.createNavigation(listDTO.getResumePage());
        long totalCount = listDTO.getResumePage().getTotalElements();
        if (listDTO.getRepResume() != null) {
            totalCount++; // 대표 이력서가 있으면 전체 개수에 1을 더함
        }

        // 4. 🖼️ 뷰에 데이터 전달
        model.addAttribute("repResume", listDTO.getRepResume());
        model.addAttribute("resumePage", listDTO.getResumePage());
        model.addAttribute("member", member);
        model.addAttribute("navigation", navigation);
        model.addAttribute("totalCount", totalCount);

        return "resume/list";
    }

    /**
     * 📄 [GET] /resume/{id} : (개인) 이력서 상세 페이지
     * - 본인이 작성한 이력서의 상세 내용을 보여줍니다.
     */
    @GetMapping("/resume/{id}")
    public String detail(@PathVariable(name = "id") Long resumeIdx, Model model) {
        // 1. 👤 인증 및 권한 검사
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) {
            throw new Exception401("로그인 해주시기 바랍니다");
        }
        if (!"MEMBER".equals(sessionUser.getRole())) {
            throw new Exception403("개인 회원만 접근 가능합니다");
        }

        // 2. 🔄 서비스 호출 (이력서 조회)
        Resume resume = resumeService.findByIdWithCareers(resumeIdx);

        // 3. 🛡️ 소유권 확인
        if (!resume.isOwner(sessionUser.getId())) {
            throw new Exception403("이력서를 조회할 권한이 없습니다");
        }

        // 4. 🖼️ 뷰에 데이터 전달
        ResumeResponse.CorpDetailDTO responseDTO = new ResumeResponse.CorpDetailDTO(resume);
        model.addAttribute("resume", responseDTO);
        model.addAttribute("isOwner", true); // 본인 소유임을 표시
        return "resume/detail";
    }

    /**
     * 👨‍💻 [GET] /corp/resume/{resumeIdx} : (기업) 이력서 상세 페이지
     * - 기업 회원이 개인 회원의 이력서를 조회합니다.
     */
    @GetMapping("/corp/resume/{resumeIdx}")
    public String corpResumeDetail(@PathVariable Long resumeIdx, Model model) {
        // 1. 👤 인증 및 권한 검사
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) {
            throw new Exception401("로그인 해주시기 바랍니다");
        }
        if (!"CORP".equals(sessionUser.getRole())) {
            throw new Exception403("기업 회원만 접근 가능합니다");
        }

        // 2. 🔄 서비스 호출 및 🖼️ 뷰에 데이터 전달
        ResumeResponse.CorpDetailDTO responseDTO = resumeService.findCorpResumeDetail(resumeIdx);
        model.addAttribute("resume", responseDTO);
        model.addAttribute("isOwner", false); // 타인 소유임을 표시
        return "resume/detail";
    }

    /**
     * 📝 [GET] /resume/{id}/update-form : 이력서 수정 페이지
     * - 기존 이력서 내용을 채운 수정 폼을 보여줍니다.
     */
    @GetMapping("/resume/{id}/update-form")
    public String updateForm(@PathVariable(name = "id") Long resumeIdx, Model model) {
        // 1. 👤 인증 및 권한 검사
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) {
            throw new Exception401("로그인 해주시기 바랍니다");
        }
        if (!"MEMBER".equals(sessionUser.getRole())) {
            throw new Exception403("개인 회원만 접근 가능합니다");
        }
        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new Exception404("해당 회원을 찾을 수 없습니다"));

        // 2. 🔄 서비스 호출 및 🖼️ 뷰에 데이터 전달
        Resume resume = resumeService.findByIdWithCareers(resumeIdx);
        model.addAttribute("resume", resume);
        model.addAttribute("member", member);
        return "resume/update-form";
    }


    /**
     * 💾 [POST] /resume/{id}/update : 이력서 수정 처리
     * - 유효성 검사 후, 이력서 정보를 업데이트합니다.
     */
    @PostMapping("/resume/{id}/update")
    public String update(@PathVariable(name = "id") Long resumeIdx,
                         @Valid ResumeRequest.UpdateDTO updateDTO,
                         BindingResult bindingResult,
                         Model model) {
        // 1. 👤 인증 및 권한 검사
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) {
            throw new Exception401("로그인 해주시기 바랍니다");
        }
        if (!"MEMBER".equals(sessionUser.getRole())) {
            throw new Exception403("개인 회원만 접근 가능합니다");
        }

        // 2. ✅ 유효성 검사
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            model.addAttribute("errors", errorMap);
            model.addAttribute("dto", updateDTO);

            // 검사 실패 시, 올바른 수정 폼으로
            Resume resume = resumeService.findByIdWithCareers(resumeIdx);
            model.addAttribute("resume", resume);
            return "resume/update-form";
        }

        // 3. 🔄 서비스 호출 (이력서 수정)
        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new Exception404("해당 회원을 찾을 수 없습니다"));
        resumeService.updateById(resumeIdx, updateDTO, member);

        return "redirect:/resume/" + resumeIdx;
    }

    /**
     * 🗑️ [POST] /resume/{id}/delete : 이력서 삭제 처리
     */
    @PostMapping("/resume/{id}/delete")
    public String delete(@PathVariable(name = "id") Long resumeIdx) {
        // 1. 👤 인증 및 권한 검사
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) {
            throw new Exception401("로그인 해주시기 바랍니다");
        }
        if (!"MEMBER".equals(sessionUser.getRole())) {
            throw new Exception403("개인 회원만 접근 가능합니다");
        }
        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new Exception404("해당 회원을 찾을 수 없습니다"));

        // 2. 🔄 서비스 호출 (이력서 삭제)
        resumeService.deleteById(resumeIdx, member);
        return "redirect:/resumes";
    }

    /**
     * ✨ [GET] /resume/save-form : 이력서 작성 페이지
     */
    @GetMapping("/resume/save-form")
    public String saveForm(Model model) {
        // 1. 👤 인증 및 권한 검사
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) {
            throw new Exception401("로그인 해주시기 바랍니다");
        }
        if (!"MEMBER".equals(sessionUser.getRole())) {
            throw new Exception403("개인 회원만 접근 가능합니다");
        }
        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new Exception404("해당 회원을 찾을 수 없습니다"));

        // 2. 🖼️ 뷰에 데이터 전달
        model.addAttribute("member", member);
        return "resume/save-form";
    }

    /**
     * 💾 [POST] /resume/save : 이력서 저장 처리
     */
    @PostMapping("/resume/save")
    public String save(@Valid ResumeRequest.SaveDTO saveDTO,
                       BindingResult bindingResult,
                       Model model) {
        // 1. 👤 인증 및 권한 검사
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) {
            throw new Exception401("로그인 해주시기 바랍니다");
        }
        if (!"MEMBER".equals(sessionUser.getRole())) {
            throw new Exception403("개인 회원만 접근 가능합니다");
        }

        // 2. ✅ 유효성 검사
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            model.addAttribute("errors", errorMap);
            return "resume/save-form";
        }

        // 3. 🔄 서비스 호출 (이력서 저장)
        Member member = memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new Exception404("해당 회원을 찾을 수 없습니다"));
        Resume resume = resumeService.save(saveDTO, member);

        return "redirect:/resume/" + resume.getResumeIdx();
    }

    /**
     * 👑 [POST] /resume/{id}/set-rep : 대표 이력서 설정
     */
    @PostMapping("/resume/{id}/set-rep")
    public String setRepresentative(@PathVariable(name = "id") Long resumeIdx) {
        // 1. 👤 인증 및 권한 검사
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) {
            throw new Exception401("로그인 해주시기 바랍니다");
        }
        if (!"MEMBER".equals(sessionUser.getRole())) {
            throw new Exception403("개인 회원만 접근 가능합니다");
        }

        // 2. 🔄 서비스 호출 (대표 이력서 설정)
        resumeService.setRep(sessionUser.getId(), resumeIdx);

        // 3. ➡️ 목록 페이지로 리다이렉트
        return "redirect:/resumes";
    }

}