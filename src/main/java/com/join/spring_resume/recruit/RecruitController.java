package com.join.spring_resume.recruit;

import com.join.spring_resume._core.common.PageResponseDTO;
import com.join.spring_resume._core.errors.exception.Exception401;
import com.join.spring_resume._core.errors.exception.Exception403;
import com.join.spring_resume.apply.*;
import com.join.spring_resume.corp.Corp;
import com.join.spring_resume.corp.CorpService;
import com.join.spring_resume.recruit_like.RecruitLikeService;
import com.join.spring_resume.session.SessionUser;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/recruit")
public class RecruitController {

    private final RecruitService recruitService;
    private final CorpService corpService;
    private final RecruitLikeService recruitLikeService;
    private final ApplyService applyService;
    private final ApplyRepository applyRepository;

    // 공고 연결하기
    @GetMapping("/recruit-form")
    public String recruitForm(HttpSession httpSession) {

        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");

        if (sessionUser == null || !"CORP".equals(sessionUser.getRole())) {
            throw new Exception403("기업 로그인 상태가 아닙니다.");
        }

        return "recruit/recruit-form";
    }


    // 공고 등록하기
    @PostMapping("/corp-recruit")
    public String recruit(@Valid RecruitRequest.RecruitDTO recruitDTO,
                          BindingResult bindingResult,
                          HttpSession session,
                          Model model) {

        SessionUser sessionUser = (SessionUser) session.getAttribute("session");

        if (sessionUser == null || !"CORP".equals(sessionUser.getRole())) {
            throw new Exception403("기업 로그인 상태가 아닙니다.");
        }

        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage()); // DTO 에서 받은 에러메세지를 담음
            }
            model.addAttribute("errors", errorMap);
            return "recruit/recruit-form";
        }

        LocalDateTime start = recruitDTO.getParsedStartAt();
        LocalDateTime end = recruitDTO.getParsedEndAt();

        if (start != null && end != null && end.isBefore(start)) {
            model.addAttribute("dateError", "모집 마감일은 모집 시작일보다 이후여야 합니다.");
            return "recruit/recruit-form";

        }

        Corp corp = corpService.findById(sessionUser.getId());

        recruitService.recruit(recruitDTO, corp);

        return "redirect:/";
    }


    // 현재 로그인한 유저의 공고 목록 확인 하기
    @GetMapping("/list")
    public String recruitList(Model model, HttpSession httpSession) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");

        if (sessionUser == null || !"CORP".equals(sessionUser.getRole())) {
            throw new Exception403("기업 로그인 상태가 아닙니다.");
        }

        Corp corp = corpService.findById(sessionUser.getId());
        Pageable pageable = PageRequest.of(0, 5);
        Page<Recruit> recruits = recruitService.findByAllList(corp.getCorpIdx(), pageable);

        PageResponseDTO<RecruitResponse.RecruitListDTO> pageDTO =
                PageResponseDTO.from(recruits,RecruitResponse.RecruitListDTO::fromEntity);
        model.addAttribute("recruitList", pageDTO);
        return "recruit/recruit-list";
    }

    // ajax 실시간으로 데이터 베이스 데이터 뿌리기 (공고 목록)
    @GetMapping("/api/my-recruit")
    @ResponseBody
    public PageResponseDTO<RecruitResponse.RecruitListDTO> getMyRecruits(
            HttpSession httpSession,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) {

        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");

        if (sessionUser == null || !"CORP".equals(sessionUser.getRole())) {
            throw new Exception403("기업 로그인 상태가 아닙니다.");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Recruit> recruitPage = recruitService.findByAllList(sessionUser.getId(), pageable); // 본인 공고만
        return PageResponseDTO.from(recruitPage,RecruitResponse.RecruitListDTO::fromEntity);
    }

    // 등록한 공고 삭제하기
    @PostMapping("/{recruitIdx}/delete")
    public String delete(@PathVariable(name = "recruitIdx") Long idx, HttpSession httpSession) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");
        Recruit recruit = recruitService.findById(idx); // 해당 공고 의 정보를 가져옴

        if (!recruit.isOwner(sessionUser.getId())) {
            throw new Exception403("삭제에대한 권한이없습니다");
        }

        recruitService.deleteById(idx);
        return "redirect:/";
    }

    // 등록한 공고 수정하기
    @GetMapping("/{recruitIdx}/update-form")
    public String updateForm(@PathVariable(name = "recruitIdx") Long idx, Model model, HttpSession httpSession) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");

        if (sessionUser == null || !"CORP".equals(sessionUser.getRole())) {
            throw new Exception403("기업 로그인 상태가 아닙니다.");
        }

        Recruit recruit = recruitService.findById(idx); // 해당 공고 의 정보를 가져옴

        if (!recruit.isOwner(sessionUser.getId())) {
            throw new Exception403("수정권한이없습니다");
        }


        model.addAttribute("recruit", recruit); // 현재 수정 공고의 업데이트
        model.addAttribute("sessionUser", sessionUser); //
        return "recruit/recruit-update-form";
    }

    // 공고 업데이트
    @PostMapping("/{recruitIdx}/recruit-update")
    public String update(@PathVariable(name = "recruitIdx") Long idx, RecruitRequest.RecruitUpdateDTO recruitDTO, HttpSession session) {

        SessionUser sessionUser = (SessionUser) session.getAttribute("session");

        if (sessionUser == null) {
            throw new Exception401("로그인 해주시기 바립니다");
        }
        if (!"CORP".equals(sessionUser.getRole())) {
            throw new Exception403("기업 회원만 수정할 수 있습니다.");
        }
        Recruit recruit = recruitService.findById(idx);
        if (!recruit.isOwner(sessionUser.getId())) {
            throw new Exception403("본인의 공고만 수정할 수 있습니다");
        }

        recruitService.recruitUpdate(idx, recruitDTO);

        return "redirect:/";
    }

    // 공고 상세페이지
    @GetMapping("/{recruitIdx}")
    public String detail(@PathVariable(name = "recruitIdx") Long idx, Model model, HttpSession httpSession) {
        Recruit recruit = recruitService.findById(idx);
        model.addAttribute("recruit", recruit);

        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");

        if (sessionUser != null && "MEMBER".equals(sessionUser.getRole())) {
            boolean isApplied = applyService.isAppliedCheck(sessionUser.getId(), idx);
            boolean isLiked = recruitLikeService.isCheckLike(sessionUser.getId(), idx);
            model.addAttribute("isApplied", isApplied);
            model.addAttribute("isLiked", isLiked);
        }

        return "recruit/recruit-detail";
    }

    // 현재 공고에 지원한 유저의 정보
    @GetMapping("/{recruitIdx}/applied")
    public String viewApplied(@PathVariable(name = "recruitIdx") Long recruitIdx,
                              Model model,
                              HttpSession httpSession) {

        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");

        if (sessionUser == null) {
            throw new Exception401("로그인 해주시기 바랍니다");
        }
        if (!"CORP".equals(sessionUser.getRole())) {
            throw new Exception403("기업 회원만 접근 할수있습니다");
        }

        Recruit recruit = recruitService.findById(recruitIdx);

        if (!recruit.isOwner(sessionUser.getId())) {
            throw new Exception403("기업이 등록한 공고에 대해서만 조회 가능합니다");
        }

        Pageable pageable = PageRequest.of(0, 3);
        Page<Apply> appMemberList = applyService.getApplicantsForRecruit(recruit.getRecruitIdx(), pageable);

        model.addAttribute("recruit", recruit);
        model.addAttribute("recruitList", appMemberList);
        model.addAttribute("appliedCount", appMemberList.getTotalElements());

        return "recruit/recruit-applied";
    }

    // 지원자 더보기 Ajax용 API
    @GetMapping("/api/{recruitIdx}/applied")
    @ResponseBody
    public PageResponseDTO<ApplyResponse.ApplicantDTO> getApplicantsJson(
            @PathVariable Long recruitIdx,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            HttpSession session) {

        SessionUser sessionUser = (SessionUser) session.getAttribute("session");

        if (sessionUser == null || !"CORP".equals(sessionUser.getRole())) {
            throw new Exception403("기업 회원만 접근 가능합니다");
        }

        Recruit recruit = recruitService.findById(recruitIdx);
        if (!recruit.isOwner(sessionUser.getId())) {
            throw new Exception403("공고 소유자가 아닙니다");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Apply> applyPage = applyService.getApplicantsForRecruit(recruitIdx, pageable);

        return PageResponseDTO.from(applyPage, ApplyResponse.ApplicantDTO::fromEntity);
    }

    // 현재 로그인한 유저의 공고 목록 확인 하기 ( 클릭시 지원자 정보를 확인 가능 )
    @GetMapping("/applied/list")
    public String appliedList(Model model,
                              HttpSession httpSession) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");

        if (sessionUser == null || !"CORP".equals(sessionUser.getRole())) {
            throw new Exception403("기업 로그인 상태가 아닙니다.");
        }

        Corp corp = corpService.findById(sessionUser.getId());
        Pageable pageable = PageRequest.of(0, 5);
        Page<Recruit> recruits = recruitService.findByAllList(corp.getCorpIdx(), pageable);

        Page<ApplyRequest.RecruitWithApplyCountDTO> recruitListWithCounts = recruits.map(recruit -> {
                    long applyCount = applyRepository.countByRecruitId(recruit.getRecruitIdx());
                    return new ApplyRequest.RecruitWithApplyCountDTO(recruit, applyCount);
                });

        model.addAttribute("recruitList", recruitListWithCounts);
        return "recruit/recruit-applied-list";
    }

    // 공고 + 지원자 수 목록을 JSON으로 반환 (Ajax 전용)
    @GetMapping("/api/applied/status")
    @ResponseBody
    public PageResponseDTO<ApplyRequest.RecruitWithApplyCountDTO> getAppliedStatus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            HttpSession httpSession) {

        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");

        if (sessionUser == null || !"CORP".equals(sessionUser.getRole())) {
            throw new Exception403("기업 로그인 상태가 아닙니다.");
        }

        Corp corp = corpService.findById(sessionUser.getId());
        Pageable pageable = PageRequest.of(page, size);
        Page<Recruit> recruits = recruitService.findByAllList(corp.getCorpIdx(), pageable);

        // DTO 변환
        Page<ApplyRequest.RecruitWithApplyCountDTO> result = recruits.map(recruit -> {
            long applyCount = applyRepository.countByRecruitId(recruit.getRecruitIdx());
            return new ApplyRequest.RecruitWithApplyCountDTO(recruit, applyCount);
        });

        return PageResponseDTO.from(result, dto -> dto);
    }


}
