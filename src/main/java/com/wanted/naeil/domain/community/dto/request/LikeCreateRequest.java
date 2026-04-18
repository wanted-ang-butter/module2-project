package com.wanted.naeil.domain.community.dto.request;

import com.wanted.naeil.domain.community.entity.enums.LikeTargetType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class LikeCreateRequest {

    private LikeTargetType targetType;
    private Long targetId;
}
