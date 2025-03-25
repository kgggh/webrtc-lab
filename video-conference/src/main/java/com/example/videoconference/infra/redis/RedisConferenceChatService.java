package com.example.videoconference.infra.redis;

import com.example.videoconference.application.ConferenceChatService;
import com.example.videoconference.presentation.controller.ConferenceChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisConferenceChatService implements ConferenceChatService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CONFERENCE_CHAT_CHANNEL = "conference:chat:";

    @Override
    public void sendConferenceMessage(ConferenceChatMessage message) {
        String channel = CONFERENCE_CHAT_CHANNEL + message.getConferenceId();
        redisTemplate.convertAndSend(channel, message);
    }
}
