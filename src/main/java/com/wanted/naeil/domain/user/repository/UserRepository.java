package com.wanted.naeil.domain.user.repository;

import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.entity.enums.Role;
import com.wanted.naeil.domain.user.entity.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String userId);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByPhone(String phone);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmailAndPhone(String email, String phone);

    Optional<User> findByUsernameAndPhone(String username, String phone);

    List<User> findAllByRoleIn(List<Role> roles);

    long countByRole(Role role);

    long countByRoleIn(List<Role> roles);

    long countByRoleInAndStatus(List<Role> roles, UserStatus status);

    long countByRoleInAndCreatedAtAfter(List<Role> roles, LocalDateTime createdAt);
}
