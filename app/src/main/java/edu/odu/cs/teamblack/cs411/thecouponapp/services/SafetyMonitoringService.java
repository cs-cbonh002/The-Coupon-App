package edu.odu.cs.teamblack.cs411.thecouponapp.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioRecord;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.tensorflow.lite.InterpreterApi;
import org.tensorflow.lite.task.core.BaseOptions;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tflite.gpu.support.TfLiteGpu;
import com.google.android.gms.tflite.java.TfLite;

import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;
import org.tensorflow.lite.task.audio.classifier.Classifications;
import org.tensorflow.lite.task.core.BaseOptions;

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
    private ServiceHandler serviceHandler;
    Notification notification;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (AudioRecord.RECORDSTATE_RECORDING == audioRecord.getRecordingState()) {
                return;
            }
            audioRecord.startRecording();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    int numOfSamples = tensorAudio.load(audioRecord);
                    List<Classifications> output = audioClassifier.classify(tensorAudio);

                    List<Category> finalOutput = new ArrayList<>(numOfSamples);
                    for (Classifications classifications : output) {
                        for (Category category : classifications.getCategories()) {
                            finalOutput.add(category);
                            switch (category.getLabel()) {
                            case "Whistling":
                            case "Whack":
                            case "Thwack":
                            case "Crying":
                            case "Sobbing":
                            case "Slap":
                            case "Whimper":
                            case "Screaming":
                            case "Wail":
                            case "Moan":
                            case "Whipping":
                            case "Shout":
                            case "Yell":
                            case "Grunt":
                                notification = getNotification("Safety Monitoring","you're " + category.getLabel());
                                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                assert notificationManager != null;
                                notificationManager.notify(4321, notification);
                                break;
                            default:

                                break;
                            }
                            Log.d(TAG,category.getLabel());
                        }
                    }
                }
            };
            new Timer().scheduleAtFixedRate(timerTask, 1, 500);
        }
    }

    @Override
    public void onCreate() {

        HandlerThread thread = new HandlerThread(TAG, Thread.MAX_PRIORITY);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        Looper serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        
        createNotificationChannel();
        Notification notification;

        BaseOptions baseOptions = BaseOptions.builder()
                .setNumThreads(1)
                .useNnapi()
                .build();

        AudioClassifier.AudioClassifierOptions options = AudioClassifier.AudioClassifierOptions.builder()
                .setScoreThreshold(0.5f)
                .setBaseOptions(baseOptions)
                .build();

        try {
            //tensorflow
            final String model = "audioClassifier.tflite";
            audioClassifier = AudioClassifier.createFromFileAndOptions(getApplicationContext(), model, options);
            tensorAudio = audioClassifier.createInputTensorAudio();
            audioRecord = audioClassifier.createAudioRecord();
            notification = getNotification("Safety Monitoring Service","Now listing\nTry Whistling");
        } catch (IOException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            notification =getNotification("Audio Classifier failed","Service will be shut down");
        }

        startForeground(4321, notification);

        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        return Service.START_STICKY;
    }

    /////////////Start Stop Service

    @Override
    public void onDestroy() {
        audioRecord.stop();
        timerTask.cancel();
        stopSelf();
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