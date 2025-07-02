package com.join.spring_resume.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

        @NotBlank(message = "이름을 입력해주세요")
        private String username;

        @NotBlank(message = "비밀번호 입력해주세요")
        private String password;

        @NotBlank(message = "아이디를 입력해주세요")
        private String memberId;

        // @Email(message = "올바른 이메일 형식이 아닙니다") 기본적이 유효성 검사만 해주(세부적인거는 pattern 사용하면좋음)
        @NotBlank(message = "올바른 이메일 형식이 아닙니다")
        @Pattern(
                regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.(com|co\\.kr|org|ac\\.kr)$",
                message = "이메일 형식이 올바르지 않거나 .com 또는 .co.kr 도메인만 허용됩니다"
        )
        private String email;

        @NotBlank(message = "성별 선택해주세요")
        private String sex;

        @NotBlank(message = "나이를 입력해주세요")
        private int age;

        private String address; // 지번
        private String addressDefault; // 기본 주소
        @NotBlank(message = "상세주소를 입력해주세요")
        private String addressDetail; // 상세 주소

        @NotBlank(message = "비밀번호를 재 입력해주세요")
        private String rePassword; // 비밀번호 재 입력

        @NotBlank(message = "시작하는 번호를 입력해주세요")
        private String phone1;
        @NotBlank(message = "전화번호 앞 4자리를 입력해주세요")
        private String phone2;
        @NotBlank(message = "전화번호 뒷 4자리를 입력해주세요")
        private String phone3;

        public Member toEntity(){
            String phoneNumber = phone1 + phone2 + phone3; // --> 따로 받아서 넘겨주기
            
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
                    .phoneNumber(phoneNumber)
                    .build();
        }


        public void isPassCheck(){
            if(!password.equals(rePassword)){
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
