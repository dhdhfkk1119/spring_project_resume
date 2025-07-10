package com.join.spring_resume.corp;

import com.join.spring_resume.session.SessionUser;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/corp")
public class CorpController {

    private final CorpService corpService;
    private final HttpSession session;

    // 회원기입 화면
    @GetMapping("/sign-form")
    public String signForm(HttpSession httpSession){
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");
        if(sessionUser != null){
            return "redirect:/";
        }
        return "corp/sign-form";
    }

    @GetMapping("/login-form")
    public String loginForm(HttpSession httpSession){
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");
        if(sessionUser != null){
            return "redirect:/";
        }
        return "corp/login-form";
    }

    // 로그인 화면
    @PostMapping("/login")
    public String login(@Valid CorpRequest.LoginDTO loginDTO,
                        BindingResult bindingResult,
                        Model model){

        if(bindingResult.hasErrors()){
            Map<String, String> errorMap = new HashMap<>(); // 해당 필드에 오류 메세지를 담아서 내보내기
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            model.addAttribute("errors", errorMap);
            return "corp/login-form";
        }
        try{
            Corp corp = corpService.login(loginDTO);
            SessionUser sessionUser = SessionUser.fromCorp(corp);
            session.setAttribute("session",sessionUser);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("isErrors",e.getMessage());
        }
        return "corp/login-form";
    }




    @PostMapping("/corp-sign")
    public String save(@Valid CorpRequest.saveDTO saveDTO,
                       BindingResult bindingResult,
                       Model model){

        Map<String, String> errorMap = new HashMap<>(); // 에러 반딩 해주기 위해서 넘기기

        if(bindingResult.hasErrors()){
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            model.addAttribute("errors", errorMap);
            model.addAttribute("dto", saveDTO);
            return "corp/sign-form";
        }

        // 비밀번호 체크
        if (!saveDTO.getPassword().equals(saveDTO.getRePassword())) {
            errorMap.put("IsRePassword", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("errors", errorMap);
            model.addAttribute("dto", saveDTO);
            return "corp/sign-form";
        }

        // 아이디 중복 체크
        if (corpService.existsByCorpId(saveDTO.getCorpId())) {
            errorMap.put("IsCorpId", "이미 사용 중인 아이디입니다.");
            model.addAttribute("errors", errorMap);
            model.addAttribute("dto", saveDTO);
            return "corp/sign-form";
        }

        corpService.save(saveDTO);

        return "redirect:/";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable(name = "id")Long sessionIdx,
                         @ModelAttribute CorpRequest.UpdateDTO updateDTO){
        corpService.update(sessionIdx,updateDTO);
        Corp updatedCorp = corpService.findById(sessionIdx);
        // 세션 동기화
        SessionUser updatedSessionUser = SessionUser.fromCorp(updatedCorp);
        session.setAttribute("session", updatedSessionUser);

        return "redirect:/";
    }

}
