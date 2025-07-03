package com.join.spring_resume._core.errors;


import com.join.spring_resume._core.errors.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

// 모든 컨트롤러에서 발생하는 예외 처리를 이 클래스에 서 처리한다
// RuntimeException 발생하면 해당 파일로 예외 처리가 집중 됨
@ControllerAdvice 
// @RestControllerAdvice // 데이터를 반환해서 내려 줄 떄 사용
public class MyExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(MyExceptionHandler.class);


    @ExceptionHandler(Exception400.class)
    public String ex400(Exception400 e, HttpServletRequest request) {
        log.warn(" === 400 BadRequest 에러 발생 === ");
        log.warn(" === 요청 URL : {} " , request.getRequestURL());
        log.warn("인증 오류 : {}" ,e.getMessage());
        log.warn("User-Agent : {}" , request.getHeader("User-Agent"));
        request.setAttribute("msg",e.getMessage());
        return "err/400";
    }


    @ExceptionHandler(Exception404.class)
    public String ex404(Exception404 e, HttpServletRequest request) {
        log.warn(" === 404 NotFound 에러 발생 === ");
        log.warn(" === 요청 URL : {} " , request.getRequestURL());
        log.warn("인증 오류 : {}" ,e.getMessage());
        log.warn("User-Agent : {}" , request.getHeader("User-Agent"));
        request.setAttribute("msg",e.getMessage());
        return "err/404";
    }

    @ExceptionHandler(Exception500.class)
    public String ex500(Exception500 e, HttpServletRequest request) {
        log.warn(" === 500 ServerError 에러 발생 === ");
        log.warn(" === 요청 URL : {} " , request.getRequestURL());
        log.warn("인증 오류 : {}" ,e.getMessage());
        log.warn("User-Agent : {}" , request.getHeader("User-Agent"));
        request.setAttribute("msg",e.getMessage());
        return "err/500";
    }

//    @ExceptionHandler(RuntimeException.class)
//    public String RuntimeExceptionHandler(RuntimeException e, HttpServletRequest request) {
//        log.warn(" === RuntimeException 에러 발생 === ");
//        log.warn(" === 요청 URL : {} " , request.getRequestURL());
//        log.warn("인증 오류 : {}" ,e.getMessage());
//        log.warn("User-Agent : {}" , request.getHeader("User-Agent"));
//        request.setAttribute("msg","시스템 오류 발생, 관리자에게 문의 하세요0");
//        return "err/500";
//    }

    @ExceptionHandler(Exception401.class)
    @ResponseBody // 데이터를 반환 함
    public ResponseEntity<String> ex401(Exception401 e , HttpServletRequest request) {
        String script = "<script> alert('" + e.getMessage()+ "'); location.href='/login-form'; </script>";
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }

    @ExceptionHandler(Exception403.class)
    @ResponseBody // 데이터를 반환 함
    public ResponseEntity<String> ex403(Exception403 e , HttpServletRequest request) {
        String script = "<script> alert('" + e.getMessage()+ "'); location.href='/'; </script>";
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }
}
