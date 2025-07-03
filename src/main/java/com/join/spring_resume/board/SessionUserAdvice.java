package com.join.spring_resume.board;

import com.join.spring_resume.session.SessionUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class SessionUserAdvice {

    @ModelAttribute("sessionUser")
    public SessionUser sessionUser(HttpSession session) {
        return (SessionUser) session.getAttribute("session");
    }
}