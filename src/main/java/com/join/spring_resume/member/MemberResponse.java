package com.join.spring_resume.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class MemberResponse {

    @Data
    @AllArgsConstructor
    public static class MemberDTO {
        private Long memberIdx;
        private String username;
        private String memberId;
        private String email;
        private String sex;
        private int age;
        private String address; // 지번
        private String addressDefault; // 기본 주소
        private String addressDetail; // 상세 주소
        private String phoneNumber; // 전화번호
        private String createdAtFormatted;

        // 💡 엔티티 → DTO 변환 정적 메서드
        public static MemberDTO fromEntity(Member member) {
            return new MemberDTO(
                    member.getMemberIdx(),
                    member.getUsername(),
                    member.getMemberId(),
                    member.getEmail(),
                    member.getSex(),
                    member.getAge(),
                    member.getAddress(),
                    member.getAddressDefault(),
                    member.getAddressDetail(),
                    member.getPhoneNumber(),
                    member.getCreatedAtFormatted() // 💡 포맷된 String 값 사용
            );
        }
    }
}
