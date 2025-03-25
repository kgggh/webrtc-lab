package com.example.videoconference;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VideoConferenceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideoConferenceApplication.class, args);
	}
}
