package com.join.spring_resume.corp;

import com.join.spring_resume._core.errors.exception.Exception400;
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
public class CorpService {

    private final CorpRepository corpRepository;

    @Transactional
    public Corp save(CorpRequest.saveDTO saveDTO){

        return corpRepository.save(saveDTO.toEntity());
    }


    public Corp login(CorpRequest.LoginDTO loginDTO){
        Corp corp = corpRepository.findByCorpId(loginDTO.getCorpId())
                .orElseThrow(() -> new Exception400("아이디 또는 비밀번호가 틀립니다"));

        if (!corp.getPassword().equals(loginDTO.getPassword())) {
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
        MultipartFile imageFile = updateDTO.getCorpImage();
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // 1. 저장 경로 설정
                String uploadDir = "C:/join-uploads/corp-images/";
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
                corp.setCorpImage(uuidFilename); // 또는 "/corp-images/" + uuidFilename

            } catch (Exception e) {
                e.printStackTrace(); // 상세 스택 로그 출력
                throw new Exception500("이미지 저장 실패");
            }
        }

    }

}
