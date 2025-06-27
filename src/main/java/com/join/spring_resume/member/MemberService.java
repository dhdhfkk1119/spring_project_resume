package com.join.spring_resume.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member login(MemberRequest.LoginDTO loginDTO){
        return memberRepository.findByMemberIdAndPassword(loginDTO.getMemberId(),loginDTO.getPassword())
                .orElseThrow(() -> new RuntimeException("해당 아이디 및 유저를 찾을수 업습니다"));

    }


    @Transactional
    public Member saveMember(MemberRequest.SaveDTO saveDTO){
        System.out.println("Service 데이터 확인" + saveDTO.toEntity()); // 엔티티 확인
        Member member = memberRepository.save(saveDTO.toEntity());
        System.out.println("Service 데이터 저장: " + member.getMemberIdx());
        System.out.println("Service 데이터 저장: " + member.getCreatedAt());
        return member;
    }

}
