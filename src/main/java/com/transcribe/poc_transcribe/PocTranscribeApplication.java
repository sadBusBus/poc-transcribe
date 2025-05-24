package com.transcribe.poc_transcribe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
@RestController
public class PocTranscribeApplication {

	private static final Logger logger = LoggerFactory.getLogger(PocTranscribeApplication.class);

	// ANSI color codes
	private static final String YELLOW = "\u001B[33m";
	private static final String RED = "\u001B[31m";
	private static final String RESET = "\u001B[0m";

	@Autowired
	private final OpenAiAudioTranscriptionModel transcriptionModel;

    public PocTranscribeApplication(OpenAiAudioTranscriptionModel transcriptionModel) {
        this.transcriptionModel = transcriptionModel;
    }

    public static void main(String[] args) {
		SpringApplication.run(PocTranscribeApplication.class, args);
	}

	@PostMapping("/transcribe")
	public String transcribe(@RequestParam("file") MultipartFile audioFile) throws IOException {
		TranscriptionResult whisperResult = transcribeAudio(audioFile, "whisper-1");
		logger.info(YELLOW + "Whisper Transcription Time: {}ms" + RESET, whisperResult.getDuration());
		logger.info(YELLOW + "Whisper Transcription: {}" + RESET, whisperResult.getTranscription());

		// Transcribe with GPT-4o-Transcribe
		TranscriptionResult gpt4oResult = transcribeAudio(audioFile, "gpt-4o-transcribe");
		logger.info(RED + "GPT-4o-Transcribe Time: {}ms" + RESET, gpt4oResult.getDuration());
		logger.info(RED + "GPT-4o-Transcribe Transcription: {}" + RESET, gpt4oResult.getTranscription());

		return "Transcription completed. Check console for results.";
	}

	private TranscriptionResult transcribeAudio(MultipartFile audioFile, String modelName) throws IOException {

		OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
				.model(modelName)
				.build();

		long startTime = System.currentTimeMillis();
		AudioTranscriptionResponse transcription = transcriptionModel.call(new AudioTranscriptionPrompt(audioFile.getResource(), options));
		long endTime = System.currentTimeMillis();

		long duration = endTime - startTime;
		return new TranscriptionResult(transcription.getResult().getOutput(), duration, modelName);
	}

	public static class TranscriptionResult {
		private String transcription;
		private long duration;
		private String modelName;

		public TranscriptionResult(String transcription, long duration, String modelName) {
			this.transcription = transcription;
			this.duration = duration;
			this.modelName = modelName;
		}

		public String getTranscription() {
			return transcription;
		}

		public long getDuration() {
			return duration;
		}

		public String getModelName() {
			return modelName;
		}
	}
}