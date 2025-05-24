package com.transcribe.poc_transcribe;

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

	@Autowired
	private OpenAiAudioTranscriptionModel transcriptionModel;

	public static void main(String[] args) {
		SpringApplication.run(PocTranscribeApplication.class, args);
	}

	@PostMapping("/transcribe")
	public String transcribe(@RequestParam("file") MultipartFile audioFile) throws IOException {
		// Transcribe with Whisper
		TranscriptionResult whisperResult = transcribeAudio(audioFile, "whisper-1");
		System.out.println("Whisper Transcription Time: " + whisperResult.getDuration() + "ms");
		System.out.println("Whisper Transcription: " + whisperResult.getTranscription());

		// Transcribe with GPT-4o-Transcribe
		TranscriptionResult gpt4oResult = transcribeAudio(audioFile, "gpt-4o-transcribe");
		System.out.println("GPT-4o-Transcribe Time: " + gpt4oResult.getDuration() + "ms");
		System.out.println("GPT-4o-Transcribe Transcription: " + gpt4oResult.getTranscription());

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