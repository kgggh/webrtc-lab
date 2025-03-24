package com.example.webrtc.infra.gateway;

import com.example.webrtc.config.JanusProperties;
import com.example.webrtc.infra.gateway.model.JanusCreateRoomRequest;
import com.example.webrtc.infra.gateway.model.JanusDestroyRoomRequest;
import com.example.webrtc.infra.gateway.model.JanusRequest;
import com.example.webrtc.infra.gateway.model.response.JanusAttachHandleResponse;
import com.example.webrtc.infra.gateway.model.response.JanusCreateSessionResponse;
import com.example.webrtc.infra.gateway.model.response.JanusCreateVideoRoomResponse;
import com.example.webrtc.infra.gateway.model.response.JanusKeepAliveResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class JanusHttpGateway {
    private final JanusProperties janusProperties;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    /** Janus 세션 생성 **/
    public String createSession() {
        String transactionId = UUID.randomUUID().toString();

        JanusCreateSessionResponse response = webClient.post()
            .uri(janusProperties.getHttpUrl())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(JanusRequest.builder()
                .janus("create")
                .transaction(transactionId)
                .build())
            .retrieve()
            .bodyToMono(JanusCreateSessionResponse.class)
            .block();

        log.info("[Janus] 세션 생성: {}", response);

        return response.getData().getId();
    }

    /** Janus video room 핸들러(Plugin) 추가 **/
    public String attachVideoRoomPlugin(String sessionId) {
        String transactionId = UUID.randomUUID().toString();

        JanusAttachHandleResponse response = webClient.post()
            .uri(janusProperties.getHttpUrl() + "/" + sessionId)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(JanusRequest.builder()
                .janus("attach")
                .transaction(transactionId)
                .plugin("janus.plugin.videoroom")
                .build())
            .retrieve()
            .bodyToMono(JanusAttachHandleResponse.class)
            .block();

        log.info("[Janus] 플러그인 핸들러 연결.: {}", response);
        return response.getData().getId();
    }

    /** KeepAlive 요청 **/
    public boolean keepAlive(String sessionId) {
        String transactionId = UUID.randomUUID().toString();

        JanusKeepAliveResponse response = webClient.post()
            .uri(janusProperties.getHttpUrl() + "/" + sessionId)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(JanusRequest.builder()
                .janus("keepalive")
                .transaction(transactionId)
                .build())
            .retrieve()
            .bodyToMono(JanusKeepAliveResponse.class)
            .block();

        log.info("[Janus] KeepAlive: {}", response);
        return response.getError() == null;
    }

    /** video room 생성 */
    public boolean createVideoRoom(String sessionId, String handleId, Long roomId) {
        String transactionId = UUID.randomUUID().toString();

        JanusRequest<JanusCreateRoomRequest> videoRoomCreateRequest = JanusRequest.<JanusCreateRoomRequest>builder()
            .janus("message")
            .transaction(transactionId)
            .body(JanusCreateRoomRequest.builder()
                .request("create")
                .room(roomId)
                .publishers(30)
                .permanent(false)
                .description("room-" + roomId)
                .isPrivate(false)
                .build())
            .build();

        JanusCreateVideoRoomResponse response = webClient.post()
            .uri(janusProperties.getHttpUrl() + "/" + sessionId + "/" + handleId)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(videoRoomCreateRequest)
            .retrieve()
            .bodyToMono(JanusCreateVideoRoomResponse.class)
            .block();

        log.info("[Janus] video room 생성: {}", response);

        JanusCreateVideoRoomResponse.MetaData metaData = response.getPlugindata().getData();
        if(metaData.getErrorCode() != null) {
            log.info("비디오 룸 생성에 실패했습니다, 이유: " + metaData.getError());
            return false;
        }

        return true;
    }

    public boolean destroyVideoRoom(String sessionId, String handleId, Long roomId) {
        String transactionId = UUID.randomUUID().toString();

        JanusRequest<JanusDestroyRoomRequest> videoRoomDestroyRequest = JanusRequest.<JanusDestroyRoomRequest>builder()
            .janus("message")
            .transaction(transactionId)
            .body(JanusDestroyRoomRequest.builder()
                .request("destroy")
                .room(roomId)
                .permanent(true)
                .build())
            .build();

        String response = webClient.post()
            .uri(janusProperties.getHttpUrl() + "/" + sessionId + "/" + handleId)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(videoRoomDestroyRequest)
            .retrieve()
            .bodyToMono(String.class)
            .block();

        log.info("[Janus] video room 삭제: {}", response);

        return true;
    }
}
