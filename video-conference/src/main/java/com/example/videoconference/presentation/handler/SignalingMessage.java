package com.example.videoconference.presentation.handler;

import lombok.Data;

import java.util.Set;

@Data
public class SignalingMessage {
    public enum CommandType {
        JOIN, LEAVE, OFFER, ANSWER, CANDIDATE, PARTICIPANTS_SYNC
    }

    private CommandType type;
    private Long roomId;
    private Long senderId;
    private String sdp;
    private String candidate;
    private Set<String> participants;
}
