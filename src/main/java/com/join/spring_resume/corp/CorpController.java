package com.join.spring_resume.corp;

import com.join.spring_resume.session.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class CorpController {

    private final CorpService corpService;
    private final HttpSession session;


    @PostMapping("/corp/login")
    public String login(CorpRequest.LoginDTO loginDTO){

        Corp corp = corpService.login(loginDTO);
        SessionUser sessionUser = SessionUser.fromCorp(corp);
        session.setAttribute("sessionUser",sessionUser);

        return "redirect:/";
    }


    @PostMapping("/corp-sign")
    public String save(CorpRequest.saveDTO saveDTO){
        System.out.println("기업 회원 가입 : " + saveDTO.toEntity());
        Corp corp = corpService.save(saveDTO);
        System.out.println("기업 회원 가입 : " + corp.getCorpId());

        return "redirect:/";
    }

}
