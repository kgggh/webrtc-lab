package com.example.webrtc.presentation.handler;

import lombok.Data;

@Data
public class SignalingMessage {
    public enum CommandType {
        JOIN, LEAVE, OFFER, ANSWER, CANDIDATE
    }

    private CommandType type;
    private Long roomId;
    private Long senderId;
    private String sdp;
    private String candidate;
}
