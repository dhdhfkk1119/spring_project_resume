package com.join.spring_resume.member;

import com.join.spring_resume._core.errors.exception.Exception400;
import com.join.spring_resume._core.errors.exception.Exception404;
import com.join.spring_resume._core.errors.exception.Exception500;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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

        // 회원 이름 수정
        member.setUsername(updateDTO.getUsername());
        
        // 이미지 저장
        MultipartFile imageFile = updateDTO.getMemberImage();
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // 1. 저장 경로 설정
                String uploadDir = "C:/join-uploads/member-images/";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                String originalFilename = imageFile.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

                // 2. UUID 파일명 생성
                String uuidFilename = UUID.randomUUID().toString() + extension;

                // 3. 파일 객체로 저장
                File saveFile = new File(uploadDir + uuidFilename);
                imageFile.transferTo(saveFile);

                // 4. DB에 저장할 파일명 (또는 상대 경로)
                member.setMemberImage(uuidFilename); // 또는 "/corp-images/" + uuidFilename

            } catch (Exception e) {
                e.printStackTrace(); // 상세 스택 로그 출력
                throw new Exception500("이미지 저장 실패");
            }
        }
    }
}
