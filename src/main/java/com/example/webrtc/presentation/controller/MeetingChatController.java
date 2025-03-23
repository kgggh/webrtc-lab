package com.example.webrtc.presentation.controller;

import com.example.webrtc.application.ChatMessagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class MeetingChatController {
    private final ChatMessagingService chatMessagingService;

    @MessageMapping("/chat/meeting")
    public void broadcastMeetingChat(@Payload MeetingChatMessage message) {
        chatMessagingService.sendMeetingMessage(message);
    }
}
