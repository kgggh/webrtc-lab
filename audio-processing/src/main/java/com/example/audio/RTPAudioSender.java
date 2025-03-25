package com.example.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;


/**
 * RTPAudioSender TEST
 *
 * **확인해야 할 사항**
 *  1. RTP 프로토콜 기본 개념
 *     - RFC 3550 (RTP: A Transport Protocol for Real-Time Applications) 참고하기.
 *     - RTP 패킷 형식: [Version, Padding, Extension, CC, Marker, Payload Type, Sequence Number, Timestamp, SSRC]
 *
 *  2. 입력 파일 (WMA) 확인
 *     - TEST_AUDIO_FILE의 경로에 있는 WMA 파일을 올바르게 읽을 수 있는지 확인
 *     - WMA를 PCM(8kHz, 16-bit, 모노)으로 변환하는 과정 확인
 *
 *  3.PCM 변환 관련 사항
 *     - WMA를 8kHz, 16-bit, 모노(1채널) PCM으로 변환해야 함
 *     - AudioSystem.getAudioInputStream()을 사용하여 변환됨
 *
 *  4. G.711 μ-law (PCMU) 인코딩 확인
 *     - PCM 데이터를 G.711 μ-law(PCMU)로 변환해야 함
 *     - linearToULaw() 함수가 올바르게 동작하는지 확인
 *
 *  5. RTP 패킷 전송 관련 사항
 *     - RTP 패킷 헤더 설정이 RFC 3550 규격에 맞는지 확인
 *     - BUFFER_SIZE(160 bytes, 20ms 프레임) 설정이 적절한지 확인
 *     - Timestamp 계산이 8kHz 기준으로 올바르게 증가하는지 확인
 *
 *  6. FFmpeg을 사용한 RTP 수신 테스트
 *     - RTP 패킷이 정상적으로 전송되는지 확인하려면 다음 명령어를 사용
 *       cli: ffplay -nodisp -protocol_whitelist "file,udp,rtp" -i rtp://127.0.0.1:6000
 *     - 깨지는 소리가 나면 샘플 레이트, G.711 변환 과정, RTP 패킷 헤더를 점검
 */
public class RTPAudioSender {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 6000;
    private static final int RTP_HEADER_SIZE = 12;
    private static final int BUFFER_SIZE = 160;
    private static final String TEST_AUDIO_FILE = "audio-processing/src/main/resources/sample.wav";

    public  void start() {
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);

            System.out.println("RTP 패킷 전송 시작...");

            byte[] audioData;

            audioData = convertWMAtoPCM(TEST_AUDIO_FILE);

            if (audioData == null) {
                System.out.println("오디오 데이터를 읽을 수 없습니다.");
                return;
            }
            sendRTP(socket, serverAddress, audioData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** RTP 패킷을 생성하여 UDP 전송 */
    private void sendRTP(DatagramSocket socket, InetAddress serverAddress, byte[] audioData) throws IOException {
        int sequenceNumber = 1;
        int timestamp = 0;

        for (int offset = 0; offset < audioData.length; offset += BUFFER_SIZE) {
            System.out.println("RTP 패킷 전송 중...");
            int packetSize = Math.min(BUFFER_SIZE, audioData.length - offset);
            byte[] rtpPacket = new byte[RTP_HEADER_SIZE + packetSize];

            rtpPacket[0] = (byte) 0x80;
            rtpPacket[1] = (byte) 0x00;


            rtpPacket[2] = (byte) (sequenceNumber >> 8);
            rtpPacket[3] = (byte) (sequenceNumber & 0xFF);

            rtpPacket[4] = (byte) ((timestamp >> 24) & 0xFF);
            rtpPacket[5] = (byte) ((timestamp >> 16) & 0xFF);
            rtpPacket[6] = (byte) ((timestamp >> 8) & 0xFF);
            rtpPacket[7] = (byte) (timestamp & 0xFF);

            int ssrc = new Random().nextInt();

            rtpPacket[8] = (byte) ((ssrc >> 24) & 0xFF);
            rtpPacket[9] = (byte) ((ssrc >> 16) & 0xFF);
            rtpPacket[10] = (byte) ((ssrc >> 8) & 0xFF);
            rtpPacket[11] = (byte) (ssrc & 0xFF);

            System.arraycopy(audioData, offset, rtpPacket, RTP_HEADER_SIZE, packetSize);

            socket.send(new DatagramPacket(rtpPacket, rtpPacket.length, serverAddress, SERVER_PORT));

            sequenceNumber++;
            timestamp += BUFFER_SIZE * 1000 / 8000;
        }

        System.out.println("RTP 패킷 전송 끝~!");
    }

    private byte[] convertWMAtoPCM(String path) throws UnsupportedAudioFileException, IOException {
        File wmaFile = new File(path);
        AudioInputStream wmaStream = AudioSystem.getAudioInputStream(wmaFile);

        AudioFormat pcmFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            44100,
            16,
            1,
            2,
            44100,
            false
        );

        AudioInputStream pcmStream = AudioSystem.getAudioInputStream(pcmFormat, wmaStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = pcmStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }

        return byteArrayOutputStream.toByteArray();
    }

    private byte[] convertPCMToG711(byte[] pcmData) {
        byte[] g711Data = new byte[pcmData.length / 2];

        for (int i = 0, j = 0; i < pcmData.length; i += 2, j++) {
            short sample = (short) ((pcmData[i + 1] << 8) | (pcmData[i] & 0xFF));
            g711Data[j] = linearToULaw(sample);
        }

        return g711Data;
    }

    private static byte linearToULaw(short sample) {
        final int BIAS = 0x84;
        final int CLIP = 32635;

        int sign = (sample >> 8) & 0x80;
        if (sign != 0) sample = (short) -sample;
        if (sample > CLIP) sample = CLIP;

        sample += BIAS;
        int exponent = 7;
        for (int expMask = 0x4000; (sample & expMask) == 0; expMask >>= 1, exponent--) ;

        int mantissa = (sample >> (exponent + 3)) & 0x0F;
        byte ulawByte = (byte) ~(sign | (exponent << 4) | mantissa);

        return ulawByte;
    }
}
