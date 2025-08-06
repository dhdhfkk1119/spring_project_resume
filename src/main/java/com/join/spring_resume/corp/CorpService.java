package com.join.spring_resume.corp;

import com.join.spring_resume._core.config.UploadProperties;
import com.join.spring_resume._core.errors.exception.Exception400;
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
public class CorpService {

    private final CorpRepository corpRepository;
    private final FileUploadUtil fileUploadUtil;
    private final UploadProperties uploadProperties;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public Corp save(CorpRequest.saveDTO saveDTO){
        String encoderPassword = bCryptPasswordEncoder.encode(saveDTO.getPassword());
        Corp corp = saveDTO.toEntity();
        corp.setPassword(encoderPassword);
        return corpRepository.save(corp);
    }


    public Corp login(CorpRequest.LoginDTO loginDTO){
        Corp corp = corpRepository.findByCorpId(loginDTO.getCorpId())
                .orElseThrow(() -> new Exception400("아이디 또는 비밀번호가 틀립니다"));

        if (!bCryptPasswordEncoder.matches(loginDTO.getPassword(),corp.getPassword())) {
            throw new Exception400("아이디 또는 비밀번호가 틀립니다");
        }

        return corp;
    }

    public Corp findById(Long id) {
        return corpRepository.findByCorpIdx(id)
                .orElseThrow(() -> new Exception400("기업을 찾을 수 없습니다."));
    }

    public boolean existsByCorpId(String corpId) {
        return corpRepository.existsByCorpId(corpId);
    }

    // 업데이트 하기
    @Transactional
    public void update(Long idx, CorpRequest.UpdateDTO updateDTO) {
        Corp corp = findById(idx);

        // 기업 이름 수정
        corp.setCorpName(updateDTO.getCorpName());

        // 이미지 저장
         String oldImagePath = corp.getCorpImage();
        try {
            // 새 이미지로 서버 컴퓨터에 생성 완료
            String savedFileName = fileUploadUtil.uploadProfileImage(updateDTO.getCorpImage(),uploadProperties.getCorpDir());

            // 기존에 파일이 있으면 삭제
            if(oldImagePath != null){
                fileUploadUtil.deleteProfileImage(oldImagePath,uploadProperties.getCorpDir());
            }
            
            // DB에 저장 하기
            corp.setCorpImage(savedFileName);
        } catch (IOException e) {
            throw new Exception400("프로필 이미지 업로드에 실패했습니다");
        }

    }

}
