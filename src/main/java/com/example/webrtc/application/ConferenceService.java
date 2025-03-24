package com.example.webrtc.application;

import com.example.webrtc.domain.Conference;
import com.example.webrtc.domain.JpaConferenceRepository;
import com.example.webrtc.infra.gateway.JanusHttpGateway;
import com.example.webrtc.infra.gateway.JanusConnectionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ConferenceService {
    private final JpaConferenceRepository jpaConferenceRepository;
    private final JanusHttpGateway janusHttpGateway;
    private final JanusConnectionManager janusConnectionManager;

    @Transactional
    public Long create(Long userId) {
        Conference conference = new Conference(userId);
        Long conferenceId = jpaConferenceRepository.save(conference).getId();

        String sessionId = janusConnectionManager.getSessionId();
        String handleId = janusConnectionManager.getHandleId();

        boolean createdVideoRoom = janusHttpGateway.createVideoRoom(sessionId, handleId, conferenceId);
        if(!createdVideoRoom) {
            throw new IllegalArgumentException("비디오 룸 생성에 실패했습니다.");
        }

        return conferenceId;
    }
}
