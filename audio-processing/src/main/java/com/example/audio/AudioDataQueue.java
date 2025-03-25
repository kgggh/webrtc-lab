package com.example.audio;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AudioDataQueue {
    private static final Queue<byte[]> queue = new ConcurrentLinkedQueue<>();

    public static void add(byte[] data) {
        queue.add(data);
    }

    public static byte[] poll() {
        return queue.poll();
    }

    public static boolean isEmpty() {
        return queue.isEmpty();
    }
}
