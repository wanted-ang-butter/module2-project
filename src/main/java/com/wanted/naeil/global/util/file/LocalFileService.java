package com.wanted.naeil.global.util.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class LocalFileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private static final String URL_PREFIX = "/uploads/";

    /**
     * 단일 파일 업로드 기능
     * @param file : 업로드하려는 파일
     * @param directory : 호출할 때, 넘겨주는 매개변수 문자열
     *                  목적지 폴더명이 된다. ex) ./uploads/profiles/
     *                  (코스 섬네일 : courses, 섹션 파일 : videos, 프로필이미지 : profiles)
     * @return fileUrl을 리턴한다.
     */
    @Transactional
    public String uploadSingleFile(MultipartFile file, String directory) {

        log.info("[FileUpload] 파일 업로드 시작");
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFileName = file.getOriginalFilename();
        String ext =  originalFileName.substring(originalFileName.lastIndexOf("."));

        String savedName = UUID
                .randomUUID()
                .toString()
                .replace("-", "")
                + ext;

        Path targetLocation = Paths.get(uploadDir, directory, savedName).toAbsolutePath().normalize();

        try {
            // 디렉토리 없으면 생성
            Files.createDirectories(targetLocation.getParent());

            // 파일 저장
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = URL_PREFIX + directory + "/" + savedName;

            log.info("[FileUpload] 파일 업로드 성공! 물리적 위치: {}, 접근 URL: {}", targetLocation.toString(), fileUrl);

            return fileUrl;

        } catch (IOException e) {
            log.error("[FileUpload] 파일 업로드 실패! 파일명: {}, 원인: {}", savedName, e.getMessage());
            throw new RuntimeException("파일 저장에 실패했습니다. 파일명: " + savedName, e);
        }
    }

    // 파일 삭제 기능
    @Transactional
    public void deleteFile(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) return;

        // URL_PREFIX를 기준으로 실제 물리 경로 추출
        String relativePath = fileUrl.replaceFirst("^" + URL_PREFIX, "");
        Path filePath = Paths.get(uploadDir, relativePath).toAbsolutePath().normalize();

        File file = filePath.toFile();
        if (file.exists()) {
            if (file.delete()) {
                log.info("[FileDelete] 성공: {}", filePath);
            } else {
                log.warn("[FileDelete] 실패 (권한 등): {}", filePath);
            }
        }
    }
}
