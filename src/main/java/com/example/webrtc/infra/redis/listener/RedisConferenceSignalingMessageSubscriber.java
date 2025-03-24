package com.example.webrtc.infra.redis.listener;

import com.example.webrtc.application.WebSocketSessionManager;
import com.example.webrtc.presentation.handler.SignalingMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisConferenceSignalingMessageSubscriber implements MessageListener {

    private final WebSocketSessionManager webSocketSessionManager;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("Redis message received: {}", message.toString());
        try {
            SignalingMessage chatMessage = objectMapper.readValue(message.getBody(), SignalingMessage.class);
            String jsonMessage = objectMapper.writeValueAsString(chatMessage);
            webSocketSessionManager.sendToRoom(chatMessage.getRoomId(), chatMessage.getSenderId(), jsonMessage);
        } catch (Exception e) {
            throw new RuntimeException("Message send failed.......", e);
        }
    }
}
