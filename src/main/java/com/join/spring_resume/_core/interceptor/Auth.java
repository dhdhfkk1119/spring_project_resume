package com.join.spring_resume._core.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 역할(Role) 기반으로 접근 권한을 제어하기 위한 커스텀 어노테이션 @Auth
 * 이 어노테이션이 붙은 컨트롤러 메서드는 {@link AuthInterceptor}에 의해 권한 검사를 받는다
 * 로그인 여부는 {@link LoginInterceptor}가 먼저 확인
 * <ul>
 *     <li>개인 회원 전용: {@code @Auth} 또는 {@code @Auth(role = "MEMBER")}</li>
 *     <li>기업 회원 전용: {@code @Auth(role = "CORP")}</li>
 * </ul>
 */
// 이 어노테이션을 '메소드'에만 붙일 수 있도록 제한
@Target(ElementType.METHOD)
// 이 어노테이션 정보가 프로그램 실행 중(런타임)에도 유지되도록 설정
@Retention(RetentionPolicy.RUNTIME)
// 내가 새로운 어노테이션 @Auth 를 만들어 쓰겠다는 표시
public @interface Auth {

    /**
     * 이 메소드에 접근하기 위해 필요한 사용자 역할(Role)을 지정
     * @return 필요한 역할 문자열. 기본값은 "MEMBER"
     */
    String role() default "MEMBER";
}