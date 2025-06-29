package com.join.spring_resume.recruit;

import com.join.spring_resume.corp.Corp;
import com.join.spring_resume.corp.CorpService;
import com.join.spring_resume.session.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/recruit")
public class RecruitController {

    private final RecruitService recruitService;
    private final CorpService corpService;

    @GetMapping("/recruit-form")
    public String recruitForm(){
        return "recruit/recruit-form";
    }

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

}
