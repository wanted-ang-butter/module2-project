package com.wanted.naeil.domain.admin.service;

import com.wanted.naeil.domain.admin.dto.response.BlacklistResponse;
import com.wanted.naeil.domain.admin.entity.BlacklistHistory;
import com.wanted.naeil.domain.admin.repository.BlacklistRepository;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.entity.enums.UserStatus;
import com.wanted.naeil.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
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
                .orElseThrow(() -> new NoSuchElementException("User not found."));

        if (user.getStatus() == UserStatus.BANNED
                || blacklistRepository.existsByUserIdAndReleaseReasonIsNull(userId)) {
            throw new IllegalStateException("User is already blacklisted.");
        }

        user.ban();

        BlacklistHistory history = BlacklistHistory.builder()
                .user(user)
                .admin(null)
                .reason(reason)
                .build();
        blacklistRepository.save(history);
    }

    @Transactional
    public void unban(Long blacklistId, String releaseReason) {
        BlacklistHistory history = blacklistRepository.findById(blacklistId)
                .orElseThrow(() -> new NoSuchElementException("Blacklist history not found."));

        User user = history.getUser();
        user.unban();

        blacklistRepository.findAllByUserIdAndReleaseReasonIsNull(user.getId())
                .forEach(activeHistory -> activeHistory.release(releaseReason));
    }

    @Transactional(readOnly = true)
    public List<BlacklistResponse> getBlacklist() {
        List<BlacklistHistory> histories =
                blacklistRepository.findAllByReleaseReasonIsNullOrderByCreatedAtDesc();

        return histories.stream()
                .collect(Collectors.toMap(
                        history -> history.getUser().getId(),
                        history -> history,
                        (existing, ignored) -> existing,
                        LinkedHashMap::new
                ))
                .values().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private BlacklistResponse toResponse(BlacklistHistory history) {
        return BlacklistResponse.builder()
                .blacklistID(history.getBlacklistId())
                .userId(history.getUser().getId())
                .userName(history.getUser().getName())
                .phone(history.getUser().getPhone())
                .userCreatedAt(history.getUser().getCreatedAt())
                .reason(history.getReason())
                .releaseReason(history.getReleaseReason())
                .createdAt(history.getCreatedAt())
                .build();
    }
}
