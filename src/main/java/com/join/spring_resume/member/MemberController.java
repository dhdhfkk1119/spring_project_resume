package com.join.spring_resume.member;

import com.join.spring_resume.session.SessionUser;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final HttpSession session; // 로그인시 세션 등록

    // 로그인 화면
    @GetMapping("/login-form")
    public String loginForm(HttpSession httpSession){
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");
        if(sessionUser != null){
            return "redirect:/";
        }


        return "member/login-form";
    }

    // 회원 가입 화면
    @GetMapping("/sign-form")
    public String signForm(HttpSession httpSession){
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");
        if(sessionUser != null){
            return "redirect:/";
        }
        return "member/sign-form";
    }

    // 로그인 기능
    @PostMapping("/member/login")
    public String login(@Valid MemberRequest.LoginDTO loginDTO,
                        BindingResult bindingResult,
                        Model model){

        if(bindingResult.hasErrors()){
            Map<String, String> errorMap = new HashMap<>(); // 해당 필드에 오류 메세지를 담아서 내보내기
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            model.addAttribute("errors", errorMap);
            return "member/login-form";
        }
        try {
            Member member = memberService.login(loginDTO);
            SessionUser sessionUser = SessionUser.fromMember(member); // 해당 유저가 있으면 세션에 정보를 담음
            session.setAttribute("session",sessionUser); // 세선 저장
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("isErrors",e.getMessage()); // 오류 메세지를 담음
        }
        return "member/login-form";
    }

    
    // 회원 가입 기능
    @PostMapping("/member/sign")
    public String sign(@Valid MemberRequest.SaveDTO saveDTO,
                       BindingResult bindingResult,
                       Model model){

        Map<String, String> errorMap = new HashMap<>(); // 에러 반딩 해주기 위해서 넘기기

        if(bindingResult.hasErrors()){
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            model.addAttribute("errors", errorMap);
            return "member/sign-form";
        }

        // 비밀번호 체크
        if (!saveDTO.getPassword().equals(saveDTO.getRePassword())) {
            errorMap.put("IsRePassword", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("errors", errorMap);
            return "member/sign-form";
        }

        // 아이디 중복 체크
        if (memberService.existsByMemberId(saveDTO.getMemberId())) {
            errorMap.put("IsMemberId", "이미 사용 중인 아이디입니다.");
            model.addAttribute("errors", errorMap);
            return "member/sign-form";
        }

        memberService.saveMember(saveDTO);  // 유효성 모두 통과하면 회원가입 진행

        return "redirect:/";  // 성공 시 메인 페이지 등으로 리다이렉트
    }


    // 로그아웃 기능
    @GetMapping("/logout")
    public String logout() {
        session.invalidate();
        return "redirect:/";
    }

}
