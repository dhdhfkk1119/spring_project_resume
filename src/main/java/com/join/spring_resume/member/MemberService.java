package com.join.spring_resume.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member saveMember(MemberRequest.SaveDTO saveDTO){
        System.out.println("회원가입 아이디" + saveDTO.getPassword());
        System.out.println("회원가입 아이디" + saveDTO.getAddress());
        System.out.println("회원가입 아이디" + saveDTO.getEmail());
        System.out.println("회원가입 아이디" + saveDTO.getUsername());
        System.out.println("회원가입 아이디" + saveDTO.getAge());


        return memberRepository.save(saveDTO.toEntity());
    }

}
