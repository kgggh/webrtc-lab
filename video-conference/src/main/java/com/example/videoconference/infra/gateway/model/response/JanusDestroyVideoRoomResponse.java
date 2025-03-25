package com.example.videoconference.infra.gateway.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JanusDestroyVideoRoomResponse {
    private String janus;

    @JsonProperty("session_id")
    private Long sessionId;
    private String transaction;
    private Long sender;

    private PluginData plugindata;


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PluginData {
        private String plugin;
        private MetaData data;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public  static class MetaData {
        private String videoroom;
        private Long room;

        private String error;

        @JsonProperty("error_code")
        private String errorCode;
    }
}
