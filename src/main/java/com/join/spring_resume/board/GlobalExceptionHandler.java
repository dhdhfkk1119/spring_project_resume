package com.join.spring_resume.board;

import com.join.spring_resume.board.BoardController.Exception401;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception401.class)
    @ResponseBody
    public String handle401(Exception401 e) {
        return "<script>alert('" + e.getMessage() + "'); location.href='/member/login';</script>";
    }
}