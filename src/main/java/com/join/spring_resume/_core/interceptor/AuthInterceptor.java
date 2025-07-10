package com.join.spring_resume._core.interceptor;

import com.join.spring_resume._core.errors.exception.Exception403;
import com.join.spring_resume.session.SessionUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 1.요청이 컨트롤러 메소드가 맞는지 확인
        // CSS, JS 같은 정적 리소스 요청은 그냥 통과시킴
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 2.@Auth 어노테이션 확인
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Auth auth = handlerMethod.getMethodAnnotation(Auth.class);

        // 3.@Auth 없으면, 권한 검사 없이 통과
        if (auth == null) {
            return true;
        }

        // 4.세션에서 사용자 정보 가져오기
        HttpSession session = request.getSession();
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");

        // LoginInterceptor가 이미 로그인 확인을 했지만, 한번 더 방어
        if (sessionUser == null) {
            throw new Exception403("인증 정보가 없습니다. 다시 로그인해주세요.");
        }

        // 5.역할(Role) 검사
        String requiredRole = auth.role();
        if (!requiredRole.equals(sessionUser.getRole())) {
            throw new Exception403("해당 페이지에 접근할 권한이 없습니다");
        }

        // 6.모든 검사 통과
        return true;
    }
}