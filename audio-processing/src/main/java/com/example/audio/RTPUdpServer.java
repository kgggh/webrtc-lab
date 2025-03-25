package com.example.audio;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/* https://en.wikipedia.org/wiki/Real-time_Transport_Protocol */
public class RTPUdpServer {
    private static final int PORT = 6000;
    private static final int RTP_HEADER_SIZE = 12;

    public void start() {
        try {
            DatagramSocket socket = new DatagramSocket(PORT);
            byte[] buffer = new byte[2048];

            System.out.println("RTP 수신 서버 시작...");

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                /* rtp header 확인 불가라 판단 하고 무시.. */
                if(packet.getLength() <= RTP_HEADER_SIZE) {
                    continue;
                }
                /* RTP 헤더를 제거하고 오디오 데이터만 추출 */
                int payloadSize = packet.getLength() - RTP_HEADER_SIZE;
                byte[] audioData = new byte[payloadSize];

                System.arraycopy(packet.getData(), RTP_HEADER_SIZE, audioData, 0, payloadSize);

                System.out.println("수신된 오디오 데이터 크기: " + payloadSize + " bytes");

                /* 큐에다가 일단 집어 넣음, */
                AudioDataQueue.add(audioData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
