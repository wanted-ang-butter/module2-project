package com.wanted.naeil.domain.user.entity;

import com.wanted.naeil.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
// 1. DELETE 쿼리가 발생할 때, 가로채서 UPDATE 쿼리로 바꿔버리는 코드
@SQLDelete(sql = "UPDATE users SET deleted_at = CURRENT_TIMESTAMP WHERE user_id = ?")
// 2. SELECT 쿼리가 발생할 때, 무조건 뒤에 이 조건을 자동으로 붙여버리는 코드
@Where(clause = "deleted_at IS NULL")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username; // 로그인 아이디

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name; // 실명

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "profile_img")
    private String profileImg;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @Column(name = "warning_count", nullable = false)
    private int warningCount;

    @Column(name = "birth_date")
    private LocalDate birthDate;


    // 비밀번호 세팅용 체이닝 메서드
    public User password(String password) {
        this.password = password;
        return this;
    }

    // 권한 세팅용 체이닝 메서드
    public User role(Role role) {
        this.role = role;
        return this;
    }

    // 상태 세팅용 체이닝 메서드
    public User status(UserStatus status) {
        this.status = status;
        return this;
    }

    @Builder
    public User(String username, String password, String name, String nickname, String email, String phone, LocalDate birthDate, String profileImg) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.phone = phone;
        this.birthDate = birthDate;
        this.role = Role.USER;          // 기본 권한
        this.status = UserStatus.ACTIVE; // 기본 상태
        this.warningCount = 0;          // 경고 횟수 초기화
        this.profileImg = profileImg;
    }

    // 비즈니스 로직
    // 경고 올리기
    public void addWarning() {
        this.warningCount++;
        if (this.warningCount >= 3) {
            this.status = UserStatus.BANNED; // 경고 3회 누적 시 정지 (예시)
        }
    }

    // role 값 바꾸기
    public void changeRole(Role newRole) {
        this.role = newRole;
    }


    // 블랙리스트 등록 성민 수정
    public void ban() {
        this.status = UserStatus.BANNED;
    }

    // 블랙리스트 해제 성민 수정
    public void unban() {
        this.status = UserStatus.ACTIVE;
    }
}

