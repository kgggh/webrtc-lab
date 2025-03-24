package com.example.webrtc.infra.gateway.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JanusExistsRoomRequest {
    private String request; // 요청 유형 (create, edit, destroy 등)
    private Long room; // 확인할 방 ID
}

