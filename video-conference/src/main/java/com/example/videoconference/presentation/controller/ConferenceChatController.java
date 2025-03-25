package com.example.videoconference.presentation.controller;

import com.example.videoconference.application.ConferenceChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ConferenceChatController {
    private final ConferenceChatService conferenceChatService;

    @MessageMapping("/chat/conference")
    public void broadcastConferenceChat(@Payload ConferenceChatMessage message) {
        conferenceChatService.sendConferenceMessage(message);
    }
}
