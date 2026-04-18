package com.wanted.naeil.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.wanted.naeil.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 로그인 관련
    // 사용자 ID가 이미 존재하는지 확인 (중복 ID 체크)
    boolean existsByUsername(String userId);

    // 이메일 중복 체크
    boolean existsByEmail(String email);

    // 닉네임 중복 체크
    boolean existsByNickname(String nickname);

    // 사용자 ID로 사용자 찾기(username 이 id 임)
    Optional<User> findByUsername(String username);

    Optional<User> findByEmailAndPhone(String email, String phone);

    Optional<User> findByUsernameAndPhone(String username, String phone);

}
