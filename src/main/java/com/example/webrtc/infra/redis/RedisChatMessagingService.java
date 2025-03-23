package com.example.webrtc.infra.redis;

import com.example.webrtc.application.ChatMessagingService;
import com.example.webrtc.presentation.controller.MeetingChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisChatMessagingService implements ChatMessagingService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String MEETING_CHAT_CHANNEL = "chat:meeting:";

    @Override
    public void sendMeetingMessage(MeetingChatMessage message) {
        String channel = MEETING_CHAT_CHANNEL + message.getMeetingId();
        redisTemplate.convertAndSend(channel, message);
    }
}
