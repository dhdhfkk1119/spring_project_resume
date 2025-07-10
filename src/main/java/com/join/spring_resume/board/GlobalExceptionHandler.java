// src/main/java/com/join/spring_resume/exception/GlobalExceptionHandler.java
package com.join.spring_resume.board;

import com.join.spring_resume._core.errors.exception.Exception401;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 예외 처리기
 * 로그인 정보가 없으면 Exception401 예외 발생
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception401.class)
    @ResponseBody
    public String handle401(Exception401 e, HttpServletRequest request) {
        // alert 후 로그인 페이지로 이동
        return "<script>alert('" + e.getMessage() + "'); location.href='/login-form';</script>";
    }
}
