package com.wanted.naeil.global.error;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class ErrorResponse {

    private int status;    // HTTP 상태 코드 (예: 400, 404)
    private String code;   // 에러 분류 코드 (예: BAD_REQUEST, NOT_FOUND)
    private String message; // 사용자에게 보여줄 에러 메시지

    public static ErrorResponse of(int status, String code, String message) {
        return ErrorResponse.builder()
                .status(status)
                .code(code)
                .message(message)
                .build();
    }

}
