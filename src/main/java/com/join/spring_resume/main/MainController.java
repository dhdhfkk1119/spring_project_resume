package com.join.spring_resume.main;

import com.join.spring_resume._core.errors.exception.Exception401;
import com.join.spring_resume._core.errors.exception.Exception403;
import com.join.spring_resume.apply.ApplyService;
import com.join.spring_resume.board.BoardRepository;
import com.join.spring_resume.board.BoardService;
import com.join.spring_resume.recruit.Recruit;
import com.join.spring_resume.recruit.RecruitRepository;
import com.join.spring_resume.recruit.RecruitResponse;
import com.join.spring_resume.recruit.RecruitService;
import com.join.spring_resume.resume.ResumeService;
import com.join.spring_resume.session.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final RecruitService recruitService;
    private final ApplyService applyService;
    private final ResumeService resumeService;
    private final BoardService boardService;


    @GetMapping("/")
    public String index(Model model, HttpSession httpSession) {

        Pageable pageable = PageRequest.of(0, 5);
        Page<Recruit> recruitList = recruitService.findAllList(pageable);
        RecruitResponse.RecruitPageDTO recruitPageDTO = RecruitResponse.RecruitPageDTO.fromPage(recruitList);

        model.addAttribute("recruitList", recruitPageDTO);

        return "index";
    }

    // ajax 실시간으로 데이터 베이스 데이터 뿌리기 (공고 목록)
    @GetMapping("/api/recruits")
    @ResponseBody
    public RecruitResponse.RecruitPageDTO getRecruits(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "3") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Recruit> recruits = recruitService.findAllList(pageable);
        return RecruitResponse.RecruitPageDTO.fromPage(recruits);
    }


    //팀 소개페이지
    @GetMapping("/about")
    public String about(){
        return "page/about";
    }

    // 멤버 마이페이지
    @GetMapping("/member/mypage")
    public String memberMyPage(HttpSession httpSession,
                               Model model){
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");

        if(sessionUser == null){
            throw new Exception401("로그인 해주시기 바랍니다");
        }

        if(sessionUser.getRole() != "MEMBER"){
            throw new Exception403("일반회원만 접근할수있습니다");
        }

        int resumeCount = resumeService.findMyResumes(sessionUser.getId()).size(); // 이력서 등록 갯수
        int recruitCount = applyService.MyApplyList(sessionUser.getId()).size(); // 공고 등록 갯수 가져오기
        int boardCount = boardService.findByMemberIdx(sessionUser.getId()).size();// 게시물 작성 갯수 가져오기

        model.addAttribute("resumeCount",resumeCount);
        model.addAttribute("recruitCount",recruitCount);
        model.addAttribute("boardCount",boardCount);
        return "page/member-page";
    }

    // 기업 마이페이지
    @GetMapping("/recruit/mypage")
    public String corpMyPage(HttpSession httpSession,
                             Model model){

        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");

        if(sessionUser == null){
            throw new Exception401("로그인 해주시기 바랍니다");
        }

        if(sessionUser.getRole() != "CORP"){
            throw new Exception403("기업 회원만 접근할수있습니다");
        }

        Long recruitCount = recruitService.countByRecruit(sessionUser.getId());
        model.addAttribute("recruitCount",recruitCount);

        return "page/corp-page";
    }
}
