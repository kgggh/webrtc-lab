package com.example.audio;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        RTPUdpServer RTPUdpServer = new RTPUdpServer();
        Thread serverThread = new Thread(() -> {
            try {
                RTPUdpServer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        RTPAudioSender rtpAudioSender = new RTPAudioSender();
        Thread senderThread = new Thread(() -> {
            try {
                rtpAudioSender.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        senderThread.start();

        RTPAudioProcessor rtpAudioProcessor = new RTPAudioProcessor();

        Thread.sleep(10000);
        rtpAudioProcessor.process();
    }
}
