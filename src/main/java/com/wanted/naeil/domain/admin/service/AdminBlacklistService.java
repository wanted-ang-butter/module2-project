package com.wanted.naeil.domain.admin.service;

import com.wanted.naeil.domain.admin.dto.response.BlacklistResponse;
import com.wanted.naeil.domain.user.entity.UserStatus;
import com.wanted.naeil.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.wanted.naeil.domain.admin.entity.BlacklistHistory;
import com.wanted.naeil.domain.admin.repository.BlacklistRepository;
import com.wanted.naeil.domain.user.entity.User;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminBlacklistService {
    private final BlacklistRepository blacklistRepository;
    private final UserRepository userRepository;

    @Transactional
    public void ban(Long userId, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("유저를 찾을수 없습니다"));
        if (user.getStatus() == UserStatus.BANNED) {
            throw new IllegalStateException("이미 블랙리스트에 등록된 유저입니다");
        }
        user.ban();

        BlacklistHistory history =
                BlacklistHistory.builder()
                        .user(user)
                        .admin(null) //TODO: 세션 구현 후 로그인한 관리자 넣기
                        .reason(reason)
                        .build();
        blacklistRepository.save(history);
    }
    @Transactional
    public void unban(Long blacklistId, String releaseReason) {
        BlacklistHistory history = blacklistRepository.findById(blacklistId)
                .orElseThrow(() -> new NoSuchElementException("블랙리스트 내역을 찾을 수 없습니다"));
            history.getUser().unban();
            history.release(releaseReason);
        //이력 안에 User가 연결돼 있으니까 history.getUser()로 꺼내서 해제
        //해제할 때는 "어떤 블랙리스트 이력을 해제할 건지"가 기준이라 blacklistId로 찾음
    }
    public List<BlacklistResponse> getBlacklist() {
        List<BlacklistHistory> histories =
                blacklistRepository.findAllByReleaseReasonIsNull();
        return histories.stream()// stream은 리스트를 하나씩 꺼내서 가공할 수 있게 해주는 도구
                .map(h -> BlacklistResponse.builder()
                        .blacklistID(h.getBlacklistId())
                        .userId(h.getUser().getId())
                        .userName(h.getUser().getName())
                        .reason(h.getReason())
                        .releaseReason(h.getReleaseReason())
                        .createdAt(h.getCreatedAt())
                        .build())
                .collect(Collectors.toList());


    }

}
