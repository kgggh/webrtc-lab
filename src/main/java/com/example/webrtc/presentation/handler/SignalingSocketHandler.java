package com.example.webrtc.presentation.handler;

import com.example.webrtc.application.WebSocketSessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@RequiredArgsConstructor
@Component
public class SignalingSocketHandler extends TextWebSocketHandler {
    private final WebSocketSessionManager webSocketSessionManager;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket 연결 생성: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("WebSocket 메시지 수신: {}", message.getPayload());
        SignalingMessage signalingMessage = objectMapper.readValue(message.getPayload(), SignalingMessage.class);

        switch (signalingMessage.getType()) {
            case JOIN -> {
                log.info("입장: 사용자:{}, 방:{}", signalingMessage.getSenderId(), signalingMessage.getRoomId());
                webSocketSessionManager.addSession(signalingMessage.getRoomId(), signalingMessage.getSenderId(), session);

                webSocketSessionManager.broadcastMessage(signalingMessage.getRoomId(), message.getPayload());

                webSocketSessionManager.broadcastParticipants(signalingMessage.getRoomId());
            }
            case LEAVE -> {
                log.info("퇴장: 사용자:{}, 방:{}", signalingMessage.getSenderId(), signalingMessage.getRoomId());
                webSocketSessionManager.removeSession(signalingMessage.getSenderId());

                webSocketSessionManager.broadcastMessage(signalingMessage.getRoomId(), message.getPayload());
            }
            case OFFER, ANSWER, CANDIDATE -> {
                log.info("시그널링: 사용자:{}, 방:{}", signalingMessage.getSenderId(), signalingMessage.getRoomId());

                webSocketSessionManager.broadcastMessage(signalingMessage.getRoomId(), message.getPayload());

                webSocketSessionManager.broadcastParticipants(signalingMessage.getRoomId());
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        webSocketSessionManager.removeSession(session);
        log.info("WebSocket 연결 종료: {}", session.getId());
    }
}
