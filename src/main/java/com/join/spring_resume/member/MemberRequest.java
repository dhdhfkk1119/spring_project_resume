package com.join.spring_resume.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MemberRequest {

    @Data
    public static class SaveDTO{
        private String username;
        private String password;
        private String repassword;
        private String memberId;
        private String email;
        private String sex;
        private int age;

        private String address; // 지번
        private String addressDefault; // 기본 주소
        private String addressDetail; // 상세 주소

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
                    .build();
        }

        public void isPassCheck(){
            if(!password.equals(repassword)){
                throw new IllegalArgumentException("두개의 비밀번호가 다릅니다");
            }
        }
    }
}
