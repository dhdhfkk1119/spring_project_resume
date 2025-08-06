package com.join.spring_resume.member;

import com.join.spring_resume._core.config.UploadProperties;
import com.join.spring_resume._core.errors.exception.Exception400;
import com.join.spring_resume._core.errors.exception.Exception404;
import com.join.spring_resume._core.errors.exception.Exception500;
import com.join.spring_resume.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final FileUploadUtil fileUploadUtil;
    private final UploadProperties uploadProperties;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Member login(MemberRequest.LoginDTO loginDTO){

        Member member = memberRepository.findByMemberId(loginDTO.getMemberId())
                .orElseThrow(() -> new Exception400("아이디 또는 비밀번호가 틀렸습니다"));

        if(!bCryptPasswordEncoder.matches(loginDTO.getPassword(),member.getPassword())){
            throw new Exception400("아이디 또는 비밀번호가 틀립니다");
        }

        return member;

    }


    @Transactional
    public Member saveMember(MemberRequest.SaveDTO saveDTO){
        String encoderPassword = bCryptPasswordEncoder.encode(saveDTO.getPassword());
        Member member = saveDTO.toEntity();
        member.setPassword(encoderPassword);
        return memberRepository.save(member);
    }

    public Member findById(Long Idx){
        return memberRepository.findByMemberIdx(Idx)
                .orElseThrow(() -> new Exception404("해당 유저를 찾을 수 없습니다"));
    }


    // 아이디 체크
    public boolean existsByMemberId(String Id) {
        return memberRepository.existsByMemberId(Id);
    }


    @Transactional
    public void updateMember(Long idx,MemberRequest.UpdateDTO updateDTO){
        Member member = findById(idx);
        
        // 이미지 저장
        String oldImagePath = member.getMemberImage();
        try {
            // 새 이미지로 서버 컴퓨터에 생성 완료
            String savedFileName = fileUploadUtil.uploadProfileImage(updateDTO.getMemberImage(),uploadProperties.getMemberDir());
            
            // 기존에 파일이 있으면 삭제
            if(oldImagePath != null){
                fileUploadUtil.deleteProfileImage(oldImagePath,uploadProperties.getMemberDir());
            }
            
            // 이름 및 파일 저장
            member.update(updateDTO.getUsername(),savedFileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
