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
                .addPathPatterns("/board/**","/recruit/**","/member/**") // 현재 경로로 들어오는 맵핑을 가로챔(여부체크)
                .excludePathPatterns("/recruit/{id:\\d+}","/member/login");
        // \\d+ 는 정규표현식으로 1개 이사으이 숫자를 의미
        // /board/1 , /board/22

    }
}
