package edu.odu.cs.teamblack.cs411.thecouponapp.helper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;
import java.util.Locale;

public class SpeechToText {
    private SpeechRecognizer speechRecognizer;
    private Intent recognizerIntent;
    private SpeechToTextListener listener;

    public interface SpeechToTextListener {
        void onTranscription(String transcription);
        void onError(int error);
    }

    public SpeechToText(Context context, SpeechToTextListener listener) {
        this.listener = listener;
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) { }

            @Override
            public void onBeginningOfSpeech() { }

            @Override
            public void onRmsChanged(float rmsdB) { }

            @Override
            public void onBufferReceived(byte[] buffer) { }

            @Override
            public void onEndOfSpeech() { }

            @Override
            public void onError(int error) {
                SpeechToText.this.listener.onError(error);
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    SpeechToText.this.listener.onTranscription(matches.get(0));
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) { }

            @Override
            public void onEvent(int eventType, Bundle params) { }
        });
    }

    public void startListening() {
        speechRecognizer.startListening(recognizerIntent);
    }

    public void stopListening() {
        speechRecognizer.stopListening();
    }

    public void destroy() {
        speechRecognizer.destroy();
    }
}