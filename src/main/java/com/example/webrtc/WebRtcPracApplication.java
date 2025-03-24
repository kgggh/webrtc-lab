package com.example.webrtc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebRtcPracApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebRtcPracApplication.class, args);
    }
}
