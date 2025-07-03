package com.join.spring_resume.member;

import com.join.spring_resume._core.errors.exception.Exception400;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sound.midi.MetaMessage;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member login(MemberRequest.LoginDTO loginDTO){

        Member member = memberRepository.findByMemberId(loginDTO.getMemberId())
                .orElseThrow(() -> new Exception400("아이디 또는 비밀번호가 틀렸습니다"));

        if (!member.getPassword().equals(loginDTO.getPassword())) {
            throw new Exception400("아이디 또는 비밀번호가 틀립니다");
        }

        return member;

    }


    @Transactional
    public Member saveMember(MemberRequest.SaveDTO saveDTO){

        return memberRepository.save(saveDTO.toEntity());
    }

    public Member findById(String memberId){
        return memberRepository.findByMemberId(memberId).orElseThrow(() -> new Exception400("해당 유저를 찾을 수 없습니다"));
    }


    // 아이디 체크
    public boolean existsByMemberId(String Id) {
        return memberRepository.existsByMemberId(Id);
    }

}
