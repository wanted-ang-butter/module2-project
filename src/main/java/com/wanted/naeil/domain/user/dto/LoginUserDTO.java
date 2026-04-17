package com.wanted.naeil.domain.user.dto;

import com.wanted.naeil.domain.user.entity.enums.Role;
import com.wanted.naeil.domain.user.entity.enums.UserStatus;

public class LoginUserDTO {

    private Long userId;
    private String username;
    private String name;
    private String password;
    private Role role;
    private UserStatus status;

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public LoginUserDTO(String profileImg) {
        this.profileImg = profileImg;
    }

    private String nickname;
    private String profileImg;

    public LoginUserDTO() {}

    public LoginUserDTO(Long userId, String username, String name, String password, Role role, UserStatus status, String nickname, String profileImg) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.password = password;
        this.role = role;
        this.status = status;
        this.nickname = nickname;
        this.profileImg = profileImg;


    }

    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public UserStatus getStatus() { return status; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setName(String name) { this.name = name; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(Role role) { this.role = role; }
    public void setStatus(UserStatus status) { this.status = status; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    @Override
    public String toString() {
        return "LoginUserDTO{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", role=" + role +
                ", status=" + status +
                '}';
    }
}