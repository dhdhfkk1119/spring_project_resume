package com.join.spring_resume.main;

import com.join.spring_resume._core.errors.exception.Exception401;
import com.join.spring_resume._core.errors.exception.Exception403;
import com.join.spring_resume.recruit.Recruit;
import com.join.spring_resume.recruit.RecruitRepository;
import com.join.spring_resume.session.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final RecruitRepository recruitRepository;

    @GetMapping("/")
    public String index(Model model, HttpSession httpSession) {

        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");

        List<Recruit> recruitList = recruitRepository.findAll();


        model.addAttribute("recruitList",recruitList); // 모든 공고등록 가져오기
        model.addAttribute("sessionUser",sessionUser);

        return "index";
    }

    //팀 소개페이지
    @GetMapping("/about")
    public String about(){
        return "page/about";
    }
    
    // 멤버 마이페이지
    @GetMapping("/member/mypage")
    public String memberMyPage(HttpSession httpSession){
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");

        if(sessionUser == null){
            throw new Exception401("로그인 해주시기 바랍니다");
        }

        if(sessionUser.getRole() != "MEMBER"){
            throw new Exception403("일반회원만 접근할수있습니다");
        }

        return "page/member-page";
    }

    // 기업 마이페이지
    @GetMapping("/recruit/mypage")
    public String corpMyPage(HttpSession httpSession){

        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");

        if(sessionUser == null){
            throw new Exception401("로그인 해주시기 바랍니다");
        }

        if(sessionUser.getRole() != "CORP"){
            throw new Exception403("기업 회원만 접근할수있습니다");
        }

        return "page/corp-page";
    }
}
