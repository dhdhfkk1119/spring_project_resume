package com.join.spring_resume.member;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final HttpSession session; // 로그인시 세션 등록

    @GetMapping("/user/login-form")
    public String loginForm(){

        return "member/login-form";
    }

    @PostMapping("/user/login")
    public String login(){

        return "redirect:/";
    }

    @GetMapping("/sign-form")
    public String signForm(){

        return "member/sign-form";
    }

    @PostMapping("/sign")
    public String sign(MemberRequest.SaveDTO saveDTO){
        System.out.println("회원가입 비밀번호" + saveDTO.getPassword());
        System.out.println("회원가입 주소" + saveDTO.getAddress());
        System.out.println("회원가입 이메일" + saveDTO.getEmail());
        System.out.println("회원가입 이름" + saveDTO.getUsername());
        System.out.println("회원가입 나이" + saveDTO.getAge());

        saveDTO.isPassCheck(); // 회원 비밀번호 체크

        memberService.saveMember(saveDTO);
        return "redirect:/";
    }
    
}
