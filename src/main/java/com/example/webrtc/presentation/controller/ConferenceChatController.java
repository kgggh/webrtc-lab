package com.example.webrtc.presentation.controller;

import com.example.webrtc.application.ChatMessagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ConferenceChatController {
    private final ChatMessagingService chatMessagingService;

    @MessageMapping("/chat/conference")
    public void broadcastMeetingChat(@Payload ConferenceChatMessage message) {
        chatMessagingService.sendMeetingMessage(message);
    }
}
