package com.join.spring_resume.member;

import com.join.spring_resume.session.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
// @RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final HttpSession session; // 로그인시 세션 등록

    // 로그인 화면
    @GetMapping("/login-form")
    public String loginForm(){

        return "member/login-form";
    }

    // 회원 가입 화면
    @GetMapping("/sign-form")
    public String signForm(){

        return "member/sign-form";
    }

    // 로그인 기능
    @PostMapping("/member/login")
    public String login(MemberRequest.LoginDTO loginDTO){
        Member member = memberService.login(loginDTO);
        SessionUser sessionUser = SessionUser.fromMember(member); // 해당 유저가 있으면 세션에 정보를 담음
        session.setAttribute("session",sessionUser); // 세선 저장
        return "redirect:/";
    }

    
    // 회원 가입 기능
    @PostMapping("/member/sign")
    public String sign(MemberRequest.SaveDTO saveDTO){
        System.out.println("Controller 데이터 확인" + saveDTO.toEntity()); // 엔티티 확인
        // saveDTO.isPassCheck(); // 회원 비밀번호 체크


        Member saved = memberService.saveMember(saveDTO);
        System.out.println("Controller 데이터 확인" + saved.getMemberIdx()); // 엔티티 확인
        System.out.println("Controller 데이터 확인" + saved.getCreatedAt()); // 엔티티 확인

        return "redirect:/";
    }

    // 로그아웃 기능
    @GetMapping("/logout")
    public String logout() {
        session.invalidate();
        return "redirect:/";
    }


    
}
