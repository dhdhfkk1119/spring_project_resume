package com.join.spring_resume._core.config;


import com.join.spring_resume._core.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // Ioc 처리 (싱글톤 패턴 관리)
@RequiredArgsConstructor
public class WebMcvConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/board/**", "/recruit/**")
                .excludePathPatterns(
                        "/recruit/{id:\\d+}",
                        "/member/**",
                        "/sign-form",
                        "/member/login",
                        "/member/sign",
                        "/corp/corp-sign",
                        "/corp/login",
                        "/corp/login-form",
                        "/login-form",
                        "/logout",
                        "/board/comments"
                );
    }
}
