package com.example.webrtc.infra.gateway.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JanusListParticipantsRequest {
    private String request; // 요청 유형 (create, edit, destroy 등)
}

