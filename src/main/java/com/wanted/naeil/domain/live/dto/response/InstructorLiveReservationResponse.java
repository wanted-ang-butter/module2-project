package com.wanted.naeil.domain.live.dto.response;

import com.wanted.naeil.domain.live.entity.LiveReservation;
import com.wanted.naeil.domain.live.entity.enums.LiveReservationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InstructorLiveReservationResponse {

    private Long reservationId;
    private String studentName;
    private String email;
    private LiveReservationStatus status;
    private LocalDateTime reservedAt;

    public static InstructorLiveReservationResponse of(LiveReservation reservation) {
        return InstructorLiveReservationResponse.builder()
                .reservationId(reservation.getId())
                .studentName(reservation.getUser().getName())
                .email(reservation.getUser().getEmail())
                .status(reservation.getStatus())
                .reservedAt(reservation.getCreatedAt())
                .build();
    }
}