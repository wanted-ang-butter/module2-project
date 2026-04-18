package com.wanted.naeil.domain.user.entity;

public enum Role {
    GUEST("게스트"),
    USER("유저"),
    SUBSCRIBER("구독자"),
    INSTRUCTOR("강사"),
    ADMIN("관리자");

    private final String description;


    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
