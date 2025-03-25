package com.example.videoconference.infra.redis.listener;

import com.example.videoconference.presentation.controller.ConferenceChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisConferenceChatMessageSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    private static final String DESTINATION_PREFIX = "/queue";
    private static final String CONFERENCE_CHAT_DESTINATION_TEMPLATE = DESTINATION_PREFIX + "/conference/chat/%s";

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            ConferenceChatMessage chatMessage = objectMapper.readValue(message.getBody(), ConferenceChatMessage.class);
            String destination = CONFERENCE_CHAT_DESTINATION_TEMPLATE.formatted(chatMessage.getConferenceId());
            messagingTemplate.convertAndSend(destination, chatMessage);
        } catch (Exception e) {
            throw new RuntimeException("Message send failed.......", e);
        }
    }
}
