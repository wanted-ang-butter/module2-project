package com.wanted.naeil.domain.user.entity.enums;

public enum UserStatus {
    ACTIVE("활성화"),
    INACTIVE("비활성화"),
    BANNED("블랙리스트");

    private final String description;


    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
