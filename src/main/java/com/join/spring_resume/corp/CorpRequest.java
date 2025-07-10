package com.join.spring_resume.corp;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

public class CorpRequest {

    @Data
    public static class saveDTO {
        
        @NotBlank(message = "회사명을 입력해주세요")
        private String corpName;

        @NotBlank(message = "아이디를 입력해주세요")
        private String corpId;

        @NotBlank(message = "올바른 이메일 형식이 아닙니다")
        @Pattern(
                regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.(com|co\\.kr|org|ac\\.kr)$",
                message = "이메일 형식이 올바르지 않거나 .com 또는 .co.kr 도메인만 허용됩니다"
        )
        private String email;

        @NotBlank(message = "비밀번호를 입력해주세요")
        @Size(min = 4, max = 20, message = "비밀번호는 4자 이상 20자 이하로 입력해주세요")
        private String password;

        @NotBlank(message = "비밀번호 확인을 입력해주세요")
        @Size(min = 4, max = 20, message = "비밀번호는 4자 이상 20자 이하로 입력해주세요")
        private String rePassword;

        private String corpImage = "basic.png";

        public Corp toEntity(){
            return Corp.builder()
                    .corpName(this.corpName)
                    .corpId(this.corpId)
                    .email(this.email)
                    .password(this.password)
                    .corpImage(corpImage)
                    .build();
        }

    }

    @Data
    // 로그인 용 DTO
    public static class LoginDTO{
        
        @NotBlank(message = "아이디를 입력해주시기 바랍니다")
        private String corpId;
        @NotBlank(message = "비밀번호를 입력해주시기 바랍니다")
        private String password;
    }

    @Data
    // 로그인 용 DTO
    public static class UpdateDTO{

        private String corpName;
        private MultipartFile corpImage; // 이미지 파일 multipartFile로 받음

    }
}
