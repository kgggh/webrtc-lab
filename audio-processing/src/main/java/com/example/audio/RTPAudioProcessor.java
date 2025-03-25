package com.example.audio;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RTPAudioProcessor {
    private static final String CREDENTIALS_PATH = "audio-processing/src/main/resources/google_key.json";
    private static final String DEBUG_AUDIO_FILE_PATH = "audio-processing/src/main/resources/result/debug_audio.wav";
    private static final String STT_RESULT_FILE_PATH = "audio-processing/src/main/resources/result/stt_result.txt";

    private final ByteArrayOutputStream audioBuffer = new ByteArrayOutputStream();

    public void process() {
        while (true) {
            byte[] audioData = AudioDataQueue.poll();
            if (audioData != null) {
                try {
                    audioBuffer.write(audioData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("패킷 없다~ STT 작업 시작~~~~!");
                processBuffer();
                break;
            }
        }
    }

    private void processBuffer() {
        if (audioBuffer.size() > 0) {
            try {
                System.out.println("변환할 오디오 데이터 크기: " + audioBuffer.size() + " bytes");

                // 🔹 G.711 데이터를 PCM으로 변환 후 저장
                byte[] pcmData = audioBuffer.toByteArray();
                saveAsWav(pcmData, DEBUG_AUDIO_FILE_PATH);

                System.out.println("디버깅용 WAV 파일 저장 완료: debug_audio.wav");

                requestGoogleSTT(pcmData); // 변환된 PCM 데이터를 STT로 전송
                audioBuffer.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveAsWav(byte[] audioData, String filePath) throws IOException {
        AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
        ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
        AudioInputStream audioInputStream = new AudioInputStream(bais, format, audioData.length / format.getFrameSize());

        File file = new File(filePath);
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file);
    }

    private void requestGoogleSTT(byte[] data) throws IOException {
        System.out.println("STT 요청 시작, 데이터 크기: " + data.length + " bytes");

        GoogleCredentials credentials = GoogleCredentials.fromStream(Files.newInputStream(Paths.get(CREDENTIALS_PATH)));
        try (SpeechClient speechClient = SpeechClient.create(SpeechSettings.newBuilder().setCredentialsProvider(() -> credentials).build())) {

            RecognitionConfig config = RecognitionConfig.newBuilder()
                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                .setSampleRateHertz(44100)
                .setEnableAutomaticPunctuation(true)
                .setProfanityFilter(false)
                .setLanguageCode("ko-KR")
                .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(ByteString.copyFrom(data)).build();

            System.out.println("Google STT API 호출 중...");
            RecognizeResponse response = speechClient.recognize(config, audio);

            System.out.println("STT 응답 수신 완료!");
            if (response.getResultsList().isEmpty()) {
                System.out.println("변환된 텍스트가 없습니다. (STT 응답 비어 있음)");
            } else {
                response.getResultsList().forEach(result ->
                {
                    String transcript = result.getAlternativesList().get(0).getTranscript();
                    writeToFile(transcript);
                });
            }
        } catch (Exception e) {
            System.err.println("STT 요청 실패: " + e.getMessage());
        } finally {
            System.exit(0);
        }
    }

    private void writeToFile(String text) {
        System.out.println("WRITE  TEXT: " + text);
        File file = new File(STT_RESULT_FILE_PATH);
        try {
            Files.write(file.toPath(), text.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
