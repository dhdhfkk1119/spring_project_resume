package com.join.spring_resume.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class MemberRequest {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaveDTO{
        private String username;
        private String password;
        private String memberId;
        private String email;
        private String sex;
        private int age;

        private String address; // 지번
        private String addressDefault; // 기본 주소
        private String addressDetail; // 상세 주소

        private String repassword;
        private LocalDateTime createdAt = LocalDateTime.now();

        public Member toEntity(){
            return Member.builder()
                    .username(this.username)
                    .password(this.password)
                    .memberId(this.memberId)
                    .email(this.email)
                    .sex(this.sex)
                    .age(this.age)
                    .address(address)
                    .addressDefault(addressDefault)
                    .addressDetail(addressDetail)
                    .createdAt(createdAt)
                    .build();
        }

        public void isPassCheck(){
            if(!password.equals(repassword)){
                throw new IllegalArgumentException("두개의 비밀번호가 다릅니다");
            }
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    // 로그인 용 DTO
    public static class LoginDTO{
        private String memberId;
        private String password;

        // 유혀성 검사
        public void validate() {
            if(memberId == null || memberId.trim().isEmpty()){
                throw new IllegalArgumentException("사용자명 입력해");
            }
            if(password == null || password.trim().isEmpty()){
                throw new IllegalArgumentException("비밀번호 입력해");
            }
        }

    }
}
