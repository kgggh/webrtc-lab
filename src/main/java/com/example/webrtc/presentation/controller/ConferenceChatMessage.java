package com.example.webrtc.presentation.controller;

import lombok.Data;

@Data
public class ConferenceChatMessage {
    private Long conferenceId;
    private String sender;
    private String content;
}
