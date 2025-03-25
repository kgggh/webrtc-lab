package com.example.videoconference.application;


import com.example.videoconference.presentation.controller.ConferenceChatMessage;

public interface ConferenceChatService {
    void sendConferenceMessage(ConferenceChatMessage message);
}
