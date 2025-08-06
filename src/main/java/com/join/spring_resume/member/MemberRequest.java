package com.join.spring_resume.member;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public class MemberRequest {


    @Data
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

        @NotNull(message = "나이를 입력해주세요")
        @Min(value = 0, message = "나이는 0세 이상이어야 합니다")
        private Integer age;

        @NotBlank(message = "지번을 입력해주세요")
        private String address; // 지번

        @NotBlank(message = "기본주소 입력해주세요")
        private String addressDefault; // 기본 주소

        @NotBlank(message = "상세주소를 입력해주세요")
        private String addressDetail; // 상세 주소

        @NotBlank(message = "비밀번호를 재 입력해주세요")
        private String rePassword; // 비밀번호 재 입력

        @NotBlank(message = "시작하는 번호를 입력해주세요")
        @Pattern(regexp = "010", message = "전화번호는 010만 입력 가능합니다.")
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

    }

    @Data
    // 로그인 용 DTO
    public static class LoginDTO{

        @NotBlank(message = "아이디를 입력해주세요")
        @Size(message = "")
        private String memberId;

        @NotBlank(message = "비밀번호를 입력해 주시기 바랍니다")
        private String password;
        
    }

    @Data
    // 업데이트 용 DTO
    public static class UpdateDTO{

        private String username;
        private MultipartFile memberImage;

    }
}
