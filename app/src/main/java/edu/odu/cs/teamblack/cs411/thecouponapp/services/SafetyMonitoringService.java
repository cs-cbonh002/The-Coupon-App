package edu.odu.cs.teamblack.cs411.thecouponapp.services;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioRecord;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;
import org.tensorflow.lite.task.audio.classifier.Classifications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;

public class SafetyMonitoringService extends Service {
    final String TAG = "Audio Classifier Service";
    //notifications
    private static final String CHANNEL_ID = "SafetyMonitoringChannel";
    PendingIntent pendingIntent;


    // TensorFlow Lite
    private AudioClassifier audioClassifier;
    private TensorAudio tensorAudio;
    private AudioRecord audioRecord;
    private TimerTask timerTask;

    //UI callback
    private int CHANNEL_INFO = R.string.channelInfo;
    private int LABEL = R.string.label;

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createNotificationChannel();
        Notification notification;

        try {
            //tensorflow
            final String model = "audioClassifier.tflite";
            audioClassifier = AudioClassifier.createFromFile(getApplicationContext(), model);
            notification = getNotification("Safety Monitoring Service","Now listing");
        } catch (IOException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            notification =getNotification("Audio Classifier failed","Service will be shut down");
        }
        tensorAudio = audioClassifier.createInputTensorAudio();

        startForeground(4321, notification);

        startService();

        return super.onStartCommand(intent, flags, startId);
    }

    /////////////Start Stop Service
    public void startService() {
        TensorAudio.TensorAudioFormat format = audioClassifier.getRequiredTensorAudioFormat();
        String channels = "Number of channels: " + format.getChannels() + "\n" +
                "Sample rate: " + format.getSampleRate();

        audioRecord = audioClassifier.createAudioRecord();
        audioRecord.startRecording();
       // channelTextView.setText(channels);

        timerTask = new TimerTask() {
            @Override
            public void run() {
                int numOfSamples = tensorAudio.load(audioRecord);
                List<Classifications> output = audioClassifier.classify(tensorAudio);
                Notification notification;

                List<Category> finalOutput = new ArrayList<>(numOfSamples);
                for (Classifications classifications : output) {
                    for (Category category : classifications.getCategories()) {
                        if (category.getScore() > 0.2f) {
                            finalOutput.add(category);
                            switch (category.getLabel()) {
                                case "Whistling":
                                    Log.d(TAG,"Whistling");
                                    notification = getNotification("Safety Monitoring","you're whistling");
                                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    assert notificationManager != null;
                                    notificationManager.notify(4321, notification);
                                    break;
                            }
                        }
                    }
                }

                StringBuilder outStr = new StringBuilder();
                for (Category c :
                        finalOutput) {
                    outStr.append(c.getLabel()).append(": ")
                            .append(c.getScore()).append("\n");
                }

                /*requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Log.d(TAG,outStr.toString());
                        outText.setText(outStr.toString());
                    }
                });*/
            }
        };
        new Timer().scheduleAtFixedRate(timerTask, 1, 500);
    }

    @Override
    public void onDestroy() {
        audioRecord.stop();
        timerTask.cancel();
    }



    //////Notifications
    private void createNotificationChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(
                CHANNEL_ID,
                "Safety Monitoring",
                NotificationManager.IMPORTANCE_HIGH);

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(notificationChannel);
    }
    private Notification getNotification(String title, String message) {

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent)
                .build();
    }


    @Override
    public IBinder onBind(Intent intent) {return null; }
}