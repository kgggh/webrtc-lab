package com.example.audio;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class GoogleSpeechToText {
    public static void main(String[] args) throws Exception {
        String filePath = "audio-processing/src/main/resources/debug_audio.wav";
        String credentialsPath = "audio-processing/src/main/resources/google_key.json";

        byte[] data = convertToMono(filePath);

        GoogleCredentials credentials = GoogleCredentials.fromStream(Files.newInputStream(Paths.get(credentialsPath)));

        try (SpeechClient speechClient = SpeechClient.create(SpeechSettings.newBuilder().setCredentialsProvider(() -> credentials).build())) {

            // ByteString으로 음성 데이터 변환
            ByteString audioBytes = ByteString.copyFrom(data);

            // 음성 인식 요청 설정
            RecognitionConfig config = RecognitionConfig.newBuilder()
                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16) // 오디오 인코딩 방식
                .setSampleRateHertz(8000) // 샘플링 레이트
                .setLanguageCode("ko-KR") // 언어 설정 (예: 한국어)
                .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                .setContent(audioBytes)
                .build();
            // Performs speech recognition on the audio file
            RecognizeResponse response = speechClient.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();


            for (SpeechRecognitionResult result : results) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcription: %s%n", alternative.getTranscript());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // WAV 파일을 모노로 변환하는 메서드
    private static byte[] convertToMono(String filePath) throws IOException, UnsupportedAudioFileException {
        File inputFile = new File(filePath);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputFile);

        // 원본 오디오 포맷
        AudioFormat originalFormat = audioInputStream.getFormat();

        // 변환할 포맷 설정 (모노 채널)
        AudioFormat monoFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            originalFormat.getSampleRate(),
            16,  // 16비트 샘플
            1,   // 모노 채널
            originalFormat.getFrameSize() / originalFormat.getChannels(), // 프레임 크기
            originalFormat.getFrameRate(),
            originalFormat.isBigEndian()
        );

        // 모노로 변환된 오디오 스트림 생성
        AudioInputStream monoAudioInputStream = AudioSystem.getAudioInputStream(monoFormat, audioInputStream);

        // 변환된 오디오 데이터 바이트 배열로 읽기
        byte[] monoData = new byte[monoAudioInputStream.available()];
        monoAudioInputStream.read(monoData);
        return monoData;
    }
}
