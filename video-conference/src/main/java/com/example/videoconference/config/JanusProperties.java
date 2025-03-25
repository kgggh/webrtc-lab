package com.example.videoconference.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "webrtc.janus")
public class JanusProperties {
    private String webSocketUrl;
    private String httpUrl;

}
