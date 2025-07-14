package com.join.spring_resume.board;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileUploadService {

    private static final String UPLOAD_DIR = "src/main/resources/static/upload/";

    public String saveImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("빈 파일입니다.");
        }

        // 원본 파일명과 확장자
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 고유한 파일명 생성
        String uniqueFileName = UUID.randomUUID() + extension;

        // 저장 경로
        File targetFile = new File(UPLOAD_DIR + uniqueFileName);

        // 디렉토리 없으면 생성
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }

        // 실제 파일 저장
        file.transferTo(targetFile);

        // 클라이언트가 접근할 수 있는 URL 반환
        return "/upload/" + uniqueFileName;
    }
}
