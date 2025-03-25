package com.example.videoconference.infra.gateway.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JanusKeepAliveResponse {
    private String janus;

    @JsonProperty("session_id")
    private String sessionId;

    private String transaction;
    private KeepAlive error;

    @Data
    public static class KeepAlive {
        private String code;
        private String reason;
    }
}

