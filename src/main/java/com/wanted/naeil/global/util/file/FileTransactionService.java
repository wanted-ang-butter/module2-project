package com.wanted.naeil.global.util.file;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import static org.springframework.transaction.support.TransactionSynchronization.STATUS_ROLLED_BACK;

@Component
@RequiredArgsConstructor
public class FileTransactionService {

    private final LocalFileService localFileService;

    // 파일 수정
    public void registerReplace(String oldFileUrl, String newFileUrl) {
        if (!StringUtils.hasText(newFileUrl)) {
            return;
        }

        // 파일 수정 실패 / 성공시 기존 트랜잭션과 별개로 수행시켜주는 역할
        // 실패시 newFile 삭제 + 기존 file 유지 성공시 newFile DB 등록, oldFile 삭제
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            // 성공할 경우
            @Override
            public void afterCommit() {
                if (StringUtils.hasText(oldFileUrl) && !oldFileUrl.equals(newFileUrl)) {
                    localFileService.deleteFile(oldFileUrl);
                }
            }

            // RollBack 상태일 경우
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    localFileService.deleteFile(newFileUrl);
                }
            }
        });
    }

    // 파일 삭제
    public void registerDelete(String videoUrl) {
        if (!StringUtils.hasText(videoUrl)) {
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                localFileService.deleteFile(videoUrl);
            }
        });
    }
}
