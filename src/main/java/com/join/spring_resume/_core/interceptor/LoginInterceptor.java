package com.join.spring_resume._core.interceptor;


import com.join.spring_resume._core.errors.exception.Exception401;
import com.join.spring_resume.session.SessionUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component //Ioc 대상 (싱글촌 패턴으로 관리)
/* 이 기능은 controller로 넘어가는 경로를 가로채서 먼저 체크하고 넘겨준다 URL을 가로챈다고 생각하면된다*/
public class LoginInterceptor implements HandlerInterceptor {

    /*
    * preHandle - 컨트롤러에 들어 가기 전에 동작 하는 메서드이다
    * 리턴타입이 boolean 이라서 true ---> 컨트롤러 안으로 들어간다 
    * false ---> 못 들억 감
    * */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(); // 세션 정보를 가져온다 
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        System.out.println("인터셉터 동작 확인 : " + request.getRequestURL());
        if(sessionUser == null ){
            throw new Exception401("로그인 후 이용이 가능합니다");
        }

        return true;
    }
    
    // 뷰가 렌더링 되기전에 콜백 되는 메서드
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
    
    // 뷰가 완전히 렌더링 된 후에 실행
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
