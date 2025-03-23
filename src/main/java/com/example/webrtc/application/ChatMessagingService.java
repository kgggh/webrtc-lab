package com.example.webrtc.application;

import com.example.webrtc.presentation.controller.MeetingChatMessage;

public interface ChatMessagingService {
    void sendMeetingMessage(MeetingChatMessage message);
}
