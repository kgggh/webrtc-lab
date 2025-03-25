package com.example.videoconference.infra.gateway;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JanusConnectionManager {
    private final JanusHttpGateway janusHttpGateway;
    private volatile String sessionId;
    private volatile String handleId;

    @PostConstruct
    public void init() {
        createSessionAndHandle();
    }

    /** 현재 세션 ID 가져오기 **/
    public String getSessionId() {
        return sessionId;
    }

    /** 현재 핸들 ID 가져오기 **/
    public String getHandleId() {
        return handleId;
    }

    /** 새로운 세션과 핸들 생성 **/
    private void createSessionAndHandle() {
        String newSessionId = janusHttpGateway.createSession();
        if (newSessionId == null) {
            log.error("[Janus] 세션 생성 실패!");
            return;
        }

        String newHandleId = janusHttpGateway.attachVideoRoomPlugin(newSessionId);
        if (newHandleId == null) {
            log.error("[Janus] 핸들 생성 실패!");
            return;
        }

        this.sessionId = newSessionId;
        this.handleId = newHandleId;

        log.info("[Janus] 세션 및 핸들 생성 완료: sessionId={}, handleId={}", sessionId, handleId);
    }

    public void refreshSession() {
        String sessionId = getSessionId();
        String handleId = getHandleId();

        if (sessionId == null || handleId == null) {
            log.warn("[Janus] 세션 또는 핸들이 존재하지 않습니다. 새로 생성합니다.");
            createSessionAndHandle();
            return;
        }

        log.info("[Janus] Keep Alive session: {}, handle: {}", sessionId, handleId);
        boolean success = janusHttpGateway.keepAlive(sessionId);

        if (!success) {
            log.warn("[Janus] 세션이 만료되었습니다. 새로운 세션을 생성합니다.");
            createSessionAndHandle();
        }
    }
}
