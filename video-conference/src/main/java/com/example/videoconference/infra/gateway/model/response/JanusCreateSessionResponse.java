package com.example.videoconference.infra.gateway.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JanusCreateSessionResponse {
    private String janus;
    private String transaction;
    private MetaData data;

    @Data
    public static class MetaData {
        private String id;
    }
}
