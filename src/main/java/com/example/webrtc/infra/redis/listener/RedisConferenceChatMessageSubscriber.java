package com.example.webrtc.infra.redis.listener;

import com.example.webrtc.presentation.controller.ConferenceChatMessage;
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
    private static final String MEETING_QUEUE_DESTINATION_PREFIX = "/queue";
    private static final String MEETING_QUEUE_DESTINATION_TEMPLATE =
        MEETING_QUEUE_DESTINATION_PREFIX + "/conference/chat/%s";

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            ConferenceChatMessage chatMessage = objectMapper.readValue(message.getBody(), ConferenceChatMessage.class);
            String destination = MEETING_QUEUE_DESTINATION_TEMPLATE.formatted(chatMessage.getConferenceId());
            messagingTemplate.convertAndSend(destination, chatMessage);
        } catch (Exception e) {
            throw new RuntimeException("Message send failed.......", e);
        }
    }
}
