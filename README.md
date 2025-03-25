# WebRTC Backend & Audio Processing Lab

WebRTC 백엔드 아키텍처 및 오디오 처리 플로우 테스트용 실험 레포.  
**Signaling, RTP, STT 등 미디어 레이어 백엔드 구성 요소 검증 및 실습 목적.**

---

## Repository Structure

1. **[`video-conference`](video-conference)**  
   WebRTC 백엔드 (시그널링 + 미디어 처리) 테스트용 모듈

2. **[`audio-processing`](audio-processing)**  
   RTP 오디오 수신 → PCM 변환 → STT 처리 플로우 검증용 모듈

---

## 1. video-conference

### 목표
- WebRTC 백엔드 플로우 구현 (Signaling + Peer 연결 + Media 처리)
- Janus Gateway 연동 및 Redis 기반 Pub/Sub 구조 테스트

### 사용 기술
- WebRTC API
- Janus Gateway
- Redis Pub/Sub
- STUN/TURN (환경에 따라 적용)

---

## 2. audio-processing

### 목표
- RTP 스트림 수신 → 오디오 데이터 디코딩 → 텍스트 변환(STT) 전체 플로우 테스트
- 디버깅 및 변환 결과 검증 목적

### 사용 기술
- Java
- Google Cloud STT API
- FFmpeg (오디오 디코딩 및 저장) / VOSK STT API
- Wireshark (RTP 분석)
- PCM/WAV 변환 로직

---

## 테스트 환경 준비

- Google Cloud STT 인증 키: `google_key.json`
- Janus Gateway 설정 파일들 (janus.jcfg 등)
- FFmpeg (CLI 기반 오디오 디코딩)
- Wireshark (선택, 패킷 분석용)

---

## Janus & Redis 환경 구성
video-conference 테스트를 위한 docker-compose
``` yaml
version: '3.8'
services:
  janus-gateway:
    image: 'canyan/janus-gateway:0.10.7'
    command: ["/usr/local/bin/janus", "-F", "/usr/local/etc/janus"]
    ports:
      - "7088:7088"
      - "8088:8088"
      - "8188:8188"
    volumes:
      - "./janus/conf/janus.jcfg:/usr/local/etc/janus/janus.jcfg"
      - "./janus/conf/janus.transport.http.jcfg:/usr/local/etc/janus/janus.transport.http.jcfg"
      - "./janus/conf/janus.transport.websocket.jcfg:/usr/local/etc/janus/janus.transport.websocket.jcfg"
      - "./janus/conf/janus.plugin.videoroom.jcfg:/usr/local/etc/janus/janus.plugin.videoroom.jcfg"
    restart: always
    networks:
      - janus

  janus_web:
    image: httpd:alpine
    ports:
      - "8999:80"
    volumes:
      - ./janus/html:/usr/local/apache2/htdocs/
    restart: always
    networks:
      - janus

  redis:
    image: redis:latest
    container_name: redis
    restart: always
    ports:
      - "6379:6379"

networks:
  janus:
    driver: bridge
```

---
## 사용 방법

### 1. RTP 수신 & STT 처리
```sh
# RTP 패킷 수신 → PCM 변환 → Google STT 호출
# 결과는 audio-processing/src/main/resources/result/stt_result.txt 에 저장
```

### 2. 디버깅용 오디오 파일 재생
```sh
ffplay audio-processing/src/main/resources/result/debug_audio.wav
```

### 3. 실시간 RTP 수신 테스트 (로컬 6000번 포트 기준)
```sh
ffplay -i rtp://127.0.0.1:6000
```

### 4. 오디오 포맷 확인
```sh
ffmpeg -i audio-processing/src/main/resources/result/debug_audio.wav
```

---

## RTP 패킷 분석 (선택)

Wireshark에서 다음 필터 적용:
```
udp.port == 6000
```

---

## Note

- 이 프로젝트는 WebRTC 백엔드 구성 요소의 처리 흐름을 실험하고, RTP 기반 미디어 수신 및 분석을 위한 실습 환경을 구성하기 위한 목적.
- 고로 실제 서비스 수준의 안정성보다는, 다양한 구조/조합을 빠르게 실험, 검증하는 데 초점.
