package com.example.videoconference.infra.gateway.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JanusDestroyRoomRequest {
    private String request;    // 요청 유형 (create, edit, destroy 등)
    private Long room;         // 방 ID
    private String secret;     // 방 설정 변경/삭제 시 필요한 비밀번호
    private boolean permanent; // 영구 삭제 여부 (기본값: false)
}
