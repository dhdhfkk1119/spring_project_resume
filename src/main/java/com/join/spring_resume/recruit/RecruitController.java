package com.join.spring_resume.recruit;

import com.join.spring_resume._core.errors.exception.Exception403;
import com.join.spring_resume.corp.Corp;
import com.join.spring_resume.corp.CorpService;
import com.join.spring_resume.session.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/recruit")
public class RecruitController {

    private final RecruitService recruitService;
    private final CorpService corpService;

    // 공고 연결하기
    @GetMapping("/recruit-form")
    public String recruitForm(){
        return "recruit/recruit-form";
    }

    // 공고 등록하기
    @PostMapping("/corp-recruit")
    public String recruit(RecruitRequest.RecruitDTO recruitDTO,HttpSession session){

        SessionUser sessionUser = (SessionUser) session.getAttribute("session");

        if (sessionUser == null || !"CORP".equals(sessionUser.getRole())) {
            throw new RuntimeException("기업 로그인 상태가 아닙니다.");
        }

        Corp corp = corpService.findById(sessionUser.getId());

        recruitService.recruit(recruitDTO,corp);

        return "redirect:/";
    }


    // 현재 로그인한 유저의 공고 목록 확인 하기
    @GetMapping("/list")
    public String recruitList(Model model, HttpSession httpSession){
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");
        Corp corp = corpService.findById(sessionUser.getId());
        List<Recruit> recruits = recruitService.findByAllList(corp.getCorpIdx());

        List<RecruitResponseDTO> recruitDTOs = recruits.stream()
                .map(RecruitResponseDTO::new)
                .collect(Collectors.toList());


        model.addAttribute("recruitList",recruitDTOs);
        return "recruit/recruit-list";
    }

    // 등록한 공고 삭제하기
    @PostMapping("/{recruitIdx}/delete")
    public String delete(@PathVariable(name = "recruitIdx")Long idx,HttpSession httpSession){
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");
        Recruit recruit = recruitService.findById(idx); // 해당 공고 의 정보를 가져옴

        if(!recruit.isOwner(sessionUser.getId())){
            throw new RuntimeException("삭제에대한 권한이없습니다");
        }

        recruitService.deleteById(idx);
        return "redirect:/";
    }

    // 등록한 공고 수정하기
    @GetMapping("/{recruitIdx}/update-form")
    public String updateForm(@PathVariable(name = "recruitIdx") Long idx,Model model,HttpSession httpSession){
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");

        if (sessionUser == null || !"CORP".equals(sessionUser.getRole())) {
            throw new Exception403("기업 로그인 상태가 아닙니다.");
        }

        Recruit recruit = recruitService.findById(idx); // 해당 공고 의 정보를 가져옴

        if(!recruit.isOwner(sessionUser.getId())){
            throw new Exception403("수정권한이없습니다");
        }

        RecruitResponseDTO responseDTO = new RecruitResponseDTO(recruit);


        model.addAttribute("recruit",responseDTO); // 현재 수정 공고의 업데이트
        model.addAttribute("sessionUser",sessionUser); //
        return "recruit/recruit-update-form";
    }
    
    // 공고 업데이트
    @PostMapping("/{recruitIdx}/recruit-update")
    public String update(@PathVariable(name = "recruitIdx")Long idx, RecruitRequest.RecruitUpdateDTO recruitDTO,HttpSession session){

        SessionUser sessionUser = (SessionUser) session.getAttribute("session");

        if (sessionUser == null ) {
            throw new RuntimeException("기업 회원만 수정할 수 있습니다");
        }
        if(!"CORP".equals(sessionUser.getRole())){
            throw new Exception403("기업 회원만 수정할 수 있습니다.");
        }
        Recruit recruit = recruitService.findById(idx);
        if(!recruit.isOwner(sessionUser.getId())){
            throw new Exception403("본인의 공고만 수정할 수 있습니다");
        }

        recruitService.recruitUpdate(idx,recruitDTO);

        return "redirect:/";
    }

    // 공고 상세페이지
    @GetMapping("/{recruitIdx}")
    public String detail(@PathVariable(name = "recruitIdx")Long idx,Model model){
        Recruit recruit = recruitService.findById(idx);
        RecruitResponseDTO responseDTO = new RecruitResponseDTO(recruit); // 시간 변화
        model.addAttribute("recruit",responseDTO);
        return "recruit/recruit-detail";
    }
}
