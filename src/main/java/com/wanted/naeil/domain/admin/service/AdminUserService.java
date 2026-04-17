package com.wanted.naeil.domain.admin.service;

import com.wanted.naeil.domain.admin.dto.response.UserResponse;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.entity.enums.Role;
import com.wanted.naeil.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserService {
    private final UserRepository userRepository;

    // 학생 목록 조회
    @Transactional(readOnly = true)
    public List<UserResponse> getUser() {
        List<User> users =
                userRepository.findAllByRoleIn(List.of(Role.USER, Role.INSTRUCTOR));
        return users.stream()
                .map(u ->UserResponse.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .name(u.getName())
                        .nickname(u.getNickname())
                        .email(u.getEmail())
                        .phone(u.getPhone())
                        .role(u.getRole())
                        .status(u.getStatus())
                        .warningCount(u.getWarningCount())
                        .birthDate(u.getBirthDate())
                        .build())
                .collect(Collectors.toList());
    }
    // 학생 삭제
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("해당 유저가 없습니다"));
                userRepository.delete(user);
    }
    // 학생 활성화
    @Transactional
    public void activateUser(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("해당 유저가 없습니다"));
                 user.activate();
    }
    // 학생 비활성화
    @Transactional
    public  void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("해당 유저가 없습니다"));
                user.deactivate();

    }
    // 경고 초기화
    @Transactional
    public void resetWarnigUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("해당 유조가 없습니다"));
                user.resetWarning();
    }
}

