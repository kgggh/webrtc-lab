package com.example.videoconference.application;

import com.example.videoconference.presentation.handler.SignalingMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketSessionManager {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final Map<Long, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    private static final String ROOM_PREFIX = "conference:signaling:room:";

    /** 사용자가 방에 참가할 때 WebSocket 세션을 저장 */
    public void addSession(Long roomId, Long userId, WebSocketSession session) {
        sessionMap.put(userId, session);
        redisTemplate.opsForSet().add(ROOM_PREFIX + roomId, String.valueOf(userId));
    }

    /** 사용자가 방을 나갈 때 WebSocket 세션을 제거 */
    public void removeSession(Long userId) {
        sessionMap.remove(userId);

        Set<String> rooms = redisTemplate.keys(ROOM_PREFIX + "*");
        if (rooms == null || rooms.isEmpty()) {
            log.warn("Redis에 존재하는 방이 없음. userId={}", userId);
            return;
        }

        /** 사용자 ID가 포함된 방을 찾아 삭제 **/
        rooms.forEach(roomKey -> {
            if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(roomKey, String.valueOf(userId)))) {
                redisTemplate.opsForSet().remove(roomKey, String.valueOf(userId));
                log.info("Redis에서 사용자 제거됨: userId={}, room={}", userId, roomKey);
            }
        });
    }

    /** 비정상 종료 시 WebSocket 세션 제거 */
    public void removeSession(WebSocketSession session) {
        sessionMap.entrySet().stream()
            .filter(entry -> entry.getValue().getId().equals(session.getId()))
            .map(Map.Entry::getKey)
            .findFirst()
            .ifPresent(this::removeSession);
    }

    /** 특정 사용자의 WebSocket 세션 가져오기 */
    public WebSocketSession getSession(Long userId) {
        return sessionMap.get(userId);
    }

    /** 특정 방의 모든 사용자에게 메시지 전송 */
    public void sendToRoom(Long roomId, Long senderId, String message) {
        Set<String> userIds = redisTemplate.opsForSet().members(ROOM_PREFIX + roomId);
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        for (String userId : userIds) {
            if (!String.valueOf(senderId).equals(userId)) {
                WebSocketSession session = getSession(Long.valueOf(userId));
                if (session != null && session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(message));
                    } catch (IOException e) {
                        log.warn("WebSocket 메시지 전송 실패: userId={}, roomId={}", userId, roomId, e);
                    }
                }
            }
        }
    }

    /** 모든 WebSocket 서버에 메시지를 전파 */
    public void broadcastMessage(Long roomId, String message) {
        redisTemplate.convertAndSend(ROOM_PREFIX + roomId, message);
    }

    public void broadcastParticipants(Long roomId) {
        Set<String> participants = redisTemplate.opsForSet().members(ROOM_PREFIX + roomId);
        if (participants != null && !participants.isEmpty()) {
            SignalingMessage message = new SignalingMessage();
            message.setType(SignalingMessage.CommandType.PARTICIPANTS_SYNC);
            message.setParticipants(participants);

            try {
                String json = objectMapper.writeValueAsString(message);
                sendToRoom(roomId, null, json);
            } catch (JsonProcessingException e) {
                log.error("WebSocket 메시지 전송 실패: roomId={}", roomId, e);
            }
        }
    }
}
