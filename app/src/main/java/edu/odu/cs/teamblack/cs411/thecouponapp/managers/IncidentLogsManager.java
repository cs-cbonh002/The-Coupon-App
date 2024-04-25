package edu.odu.cs.teamblack.cs411.thecouponapp.managers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.IncidentLog;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.repository.IncidentLogRepository;
import edu.odu.cs.teamblack.cs411.thecouponapp.helper.AudioRecorder;
import edu.odu.cs.teamblack.cs411.thecouponapp.helper.SpeechToText;
import java.util.Date;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.IncidentLog;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.repository.IncidentLogRepository;
import edu.odu.cs.teamblack.cs411.thecouponapp.helper.AudioRecorder;
import edu.odu.cs.teamblack.cs411.thecouponapp.helper.SpeechToText;

import java.util.Date;

public class IncidentLogsManager {
    private IncidentLogRepository incidentLogRepository;
    private AudioRecorder audioRecorder;
    private SpeechToText speechToText;
    private IncidentLog currentIncidentLog;
    private String audioFilePath;
    private boolean incidentActive = false;

    public IncidentLogsManager(Context context, IncidentLogRepository incidentLogRepository, String audioFileDirectory) {
        this.incidentLogRepository = incidentLogRepository;
        this.audioFilePath = generateUniqueAudioFilePath(audioFileDirectory);
        this.audioRecorder = new AudioRecorder(context, audioFilePath);
        this.speechToText = new SpeechToText(context, new SpeechToText.SpeechToTextListener() {
            @Override
            public void onTranscription(String transcription) {
                if (currentIncidentLog != null && incidentActive) {
                    currentIncidentLog.setTranscription(transcription);
                    Log.i("IncidentLogsManager", "Transcription: " + transcription);
                }
            }

            @Override
            public void onError(int error) {
                if (currentIncidentLog != null && incidentActive) {
                    currentIncidentLog.setError("Speech recognition error: " + error);
                }
            }
        });
    }

    public void startIncidentLogging(boolean isAutomatic) {
        new Handler(Looper.getMainLooper()).post(() -> {
            incidentActive = true;
            currentIncidentLog = new IncidentLog();
            currentIncidentLog.setStartTime(new Date());
            currentIncidentLog.setCreatedByUser(!isAutomatic);
            audioRecorder.startRecording();
            speechToText.startListening();

            if (isAutomatic) {
                // Setup a timeout to automatically stop logging after a set duration
                new Handler(Looper.getMainLooper()).postDelayed(this::stopIncidentLogging, 60000);
            }
        });
    }

    private String generateUniqueAudioFilePath(String audioFilePath) {
        return audioFilePath + "recording_" + System.currentTimeMillis() + ".aac";
    }

    public void stopIncidentLogging() {
        if (incidentActive && currentIncidentLog != null) {
            audioRecorder.stopRecording();
            speechToText.stopListening();
            currentIncidentLog.setEndTime(new Date());
            currentIncidentLog.setAudioFilePath(audioFilePath);
            long durationInMillis = currentIncidentLog.getEndTime().getTime() - currentIncidentLog.getStartTime().getTime();
            currentIncidentLog.setDuration((int) durationInMillis);
            incidentLogRepository.insert(currentIncidentLog); // Only now is the log saved
            incidentActive = false;
        } else {
            Log.d("IncidentLogsManager", "No current incident log to stop.");
        }
    }
}
