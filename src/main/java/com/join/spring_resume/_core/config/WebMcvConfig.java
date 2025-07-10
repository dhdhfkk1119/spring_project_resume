package com.join.spring_resume._core.config;

import com.join.spring_resume._core.interceptor.AuthInterceptor;
import com.join.spring_resume._core.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // Ioc 처리 (싱글톤 패턴 관리)
@RequiredArgsConstructor
public class WebMcvConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;
    private final AuthInterceptor authInterceptor; // 추가사항

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
                        "/board/comments",
                        "/board/list",
                        "/corp/{id:\\d+}/update"
                );

        registry.addInterceptor(authInterceptor) // 추가사항
                .addPathPatterns("/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/corp-images/**","/member-images/**")
                .addResourceLocations("file:///C:/join-uploads/corp-images/","file:///C:/join-uploads/member-images/");
    }
}
