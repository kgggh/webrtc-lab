package com.example.webrtc.application;

import com.example.webrtc.presentation.controller.ConferenceChatMessage;

public interface ChatMessagingService {
    void sendMeetingMessage(ConferenceChatMessage message);
}
