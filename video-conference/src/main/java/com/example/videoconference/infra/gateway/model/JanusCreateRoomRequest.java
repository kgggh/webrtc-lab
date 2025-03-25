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
public class JanusCreateRoomRequest {
    private String request; // 요청 유형 (create, edit, destroy 등)
    private Long room;       // 방 ID (선택 사항, 미입력 시 자동 생성)
    private int publishers; // 방을 목록에서 숨길지 여부 (기본값: false)
    private boolean permanent; // 영구 저장 여부 (기본값: false)
    private String description; // 방 설명
    private String secret;      // 방 설정 변경/삭제 시 필요한 비밀번호
    private String pin;         // 방 참가 비밀번호 (선택 사항)
    private boolean isPrivate; // 방을 목록에서 숨길지 여부 (기본값: false)
}

