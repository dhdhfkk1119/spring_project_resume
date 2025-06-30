package com.join.spring_resume.session;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Component
// 스프링 컨테이너에 빈으로 등록 GlobalModelAttributeAdvice 모든 컨트롤러의 요청에 대해 공통 작업 가능
@ControllerAdvice
// 모든 컨트롤러에 적용되는 예외 처리, 바인딩, 모델 데이터 주입 등 전역 설정을 정의
//GlobalModelAttributeAdvice 같은 클래스에 @ControllerAdvice만 붙이면
//스프링 컨테이너가 애플리케이션 구동 시 자동으로 이 클래스를 빈으로 등록하고
//모든 컨트롤러 요청에 대해 동작하게 됩니다.
// @RequiredArgsConstructor는 final 필드나 @NonNull 필드에 대한 생성자를 자동 생성해주는 롬복 어노테이션입니다.
public class GlobalModelAttributeAdvice {

    @Autowired
    private HttpSession session;
    // HttpSession을 생성자 주입 방식으로 받을 수 있는데,
    //스프링 컨테이너가 HttpSession 빈을 생성자에 자동 주입해 줘야 합니다.


    @ModelAttribute
    public void addCommonAttributes(Model model) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");

        if (sessionUser != null) {
            model.addAttribute("sessionUser", sessionUser);
            model.addAttribute("isUser", "MEMBER".equals(sessionUser.getRole()));
            model.addAttribute("isCorp", "CORP".equals(sessionUser.getRole()));
        }
    }
}
