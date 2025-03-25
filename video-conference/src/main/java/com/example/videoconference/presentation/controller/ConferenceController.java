package com.example.videoconference.presentation.controller;

import com.example.videoconference.application.ConferenceService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/conferences")
@RestController
@CrossOrigin
public class ConferenceController {
    private final ConferenceService conferenceService;

    // 회의 생성
    @PostMapping
    public Long create(@RequestBody ConferenceCreateRequest request) {
        log.info("회의 생성 요청: {}", request);
        return conferenceService.create(request.getUserId());
    }

    @Data
    static class ConferenceCreateRequest {
        private Long userId;
    }
}
