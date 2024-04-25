package edu.odu.cs.teamblack.cs411.thecouponapp.helper;

import android.content.Context;
import android.util.Log;
import android.media.MediaRecorder;
import java.io.IOException;

public class AudioRecorder {
    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private boolean isRecording = false;
    private Context context;

    public AudioRecorder(Context context, String audioFilePath) {
        this.context = context;
        this.audioFilePath = audioFilePath;
    }

    public void startRecording() {
        mediaRecorder = new MediaRecorder(context);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(audioFilePath);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
        } catch (IOException e) {
            Log.e("AudioRecorder", "Error starting recording", e);
        }
    }

    public void stopRecording() {
        Log.d("AudioRecorder", "Stopping recording");
        Log.d("AudioRecorder", "isRecording: " + isRecording);
        Log.d("AudioRecorder", "Stored in: " + audioFilePath);
        if (mediaRecorder!= null) {
            isRecording = false;
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }
}