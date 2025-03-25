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
public class JanusEditRoomRequest {
    private String request; // 요청 유형 (create, edit, destroy 등)
    private Long room;           // 수정할 방 ID
    private String secret;          // 방 설정 변경/삭제 시 필요한 비밀번호
    private String newDescription;  // 새로운 방 설명 (optional)
    private String newPin;          // 새로운 방 참가 비밀번호 (optional)
    private boolean newIsPrivate = false; // 새롭게 방을 목록에서 숨길지 여부 (기본값: false)
}

