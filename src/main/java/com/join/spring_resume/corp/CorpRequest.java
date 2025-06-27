package com.join.spring_resume.corp;


import com.join.spring_resume.member.MemberRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CorpRequest {

    @Data
    public static class saveDTO {

        private String corpName;
        private String corpId;
        private String email;
        private String password;
        private String rePassword;

        public Corp toEntity(){
            return Corp.builder()
                    .corpName(this.corpName)
                    .corpId(this.corpId)
                    .email(this.email)
                    .password(this.password)
                    .build();
        }

        public void isPassCheck(){
            if(!password.equals(rePassword)){
                throw new IllegalArgumentException("두개의 비밀번호가 다릅니다");
            }
        }
    }

    @Data
    // 로그인 용 DTO
    public static class LoginDTO{
        private String corpId;
        private String password;

        // 유혀성 검사
        public void validate() {
            if(corpId == null || corpId.trim().isEmpty()){
                throw new IllegalArgumentException("사용자명 입력해");
            }
            if(password == null || password.trim().isEmpty()){
                throw new IllegalArgumentException("비밀번호 입력해");
            }
        }
    }
}
