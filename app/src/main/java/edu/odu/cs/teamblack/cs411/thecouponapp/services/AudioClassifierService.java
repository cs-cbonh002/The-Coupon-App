package edu.odu.cs.teamblack.cs411.thecouponapp.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioRecord;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;
import org.tensorflow.lite.task.audio.classifier.Classifications;
import org.tensorflow.lite.task.core.BaseOptions;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.managers.SafetyResponseManager;
import edu.odu.cs.teamblack.cs411.thecouponapp.managers.SafetyResponseManager.TriggerType;

public class AudioClassifierService extends Service {
    private static final String TAG = "AudioClassifierService";
    private final IBinder binder = new LocalBinder();
    private static final String CHANNEL_ID = "SafetyMonitoringChannel";
    private static final int NOTIFICATION_ID = 4321;
    private AudioClassifier audioClassifier;
    private TensorAudio tensorAudio;
    private AudioRecord audioRecord;
    private Handler serviceHandler;
    private ScheduledThreadPoolExecutor executor;
    private boolean isInitialized = false;
    private boolean isMonitoring = false;
    private SafetyResponseManager safetyResponseManager;
    private int count = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() called");

        // Initialize the thread for background work
        HandlerThread thread = new HandlerThread("AudioClassifierThread", Thread.MAX_PRIORITY);
        thread.start();
        Looper serviceLooper = thread.getLooper();
        serviceHandler = new Handler(serviceLooper);

        // Create a notification channel for foreground service notification
        createNotificationChannel();

        // Initialize components related to audio classification
        initializeComponents();
        safetyResponseManager = SafetyResponseManager.getInstance(this);
    }


    private void initializeComponents() {
        Log.d(TAG, "Initializing components");
        BaseOptions baseOptions = BaseOptions.builder().setNumThreads(1).build(); // Don't use NNAPI
        AudioClassifier.AudioClassifierOptions options = AudioClassifier.AudioClassifierOptions.builder()
                .setScoreThreshold(0.7f)
                .setBaseOptions(baseOptions)
                .setMaxResults(1)
                .build();
        try {
            audioClassifier = AudioClassifier.createFromFileAndOptions(this, "audioClassifier.tflite", options);
            tensorAudio = audioClassifier.createInputTensorAudio();
            audioRecord = audioClassifier.createAudioRecord();
            audioRecord.startRecording();
            startForeground(NOTIFICATION_ID, getNotification("Monitoring", "Service is active"));
            startAudioClassification();
        } catch (IOException e) {
            Log.e(TAG, "Failed to initialize TensorFlow Lite components", e);
            stopSelf();
        }
    }

    private void dispatchNotification(String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Monitoring Status")
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }


    private void startAudioClassification() {
        executor = new ScheduledThreadPoolExecutor(1);
        long interval = calculateInterval();
        executor.scheduleAtFixedRate(this::processAudio, 0, interval, TimeUnit.MILLISECONDS);
    }

    private void handleResults(List<Classifications> results) {
        Log.d("AUDIO_CLASSIFIER", "Listening...");
        int localCount = 0; // Introduce a local count for this function

        for (Classifications classification : results) {
            for (Category category : classification.getCategories()) {
                Log.d("AudioEventDetection", "Category: " + category.getLabel() + ", Score: " + category.getScore());
                if ("Hands".equals(category.getLabel())) // CHECKING IF LOGS WORK
                    safetyResponseManager.handleTrigger(TriggerType.AUDIO_EVENT, "START_LOGGING");

                switch (category.getLabel()) {
                    case "Whistling":
                    case "Scream":
                    case "Whack":
                    case "Thwack":
                    case "Crying":
                    case "Sobbing":
                    case "Slap":
                    case "Whimper":
                    case "Screaming":
                    case "Wail, moan":
                    case "Whipping":
                    case "Shout":
                    case "Yell":
                    case "Grunt":
                    case "Groan":
                    case "Slap, smack":
                    case "Hands":
                    case "Crying, sobbing":
                    case "Drum":
                    case "Baby cry, infant cry":
                        count++;
                        break;
                    case "Silence":
                    case "Television":
                        if (count > 0) {
                            count--;
                        }
                        break;
                }
            }
        }
        // TODO: Fix this logic?
        if (count == 5) {
            safetyResponseManager.handleTrigger(TriggerType.AUDIO_EVENT, "START_LOGGING");
        } else if (count == 15) {
            safetyResponseManager.handleTrigger(TriggerType.AUDIO_EVENT, "COMMUNICATE");
            count = 0;
        }
    }

    private long calculateInterval() {
        int sampleRate = audioRecord.getSampleRate();
        int bufferSizeInBytes = audioRecord.getBufferSizeInFrames();
        float audioLengthInSeconds = bufferSizeInBytes / (float) sampleRate;
        return (long) (1000 * audioLengthInSeconds / 2); // 50% overlap
    }

    private void processAudio() {
        serviceHandler.post(() -> {
            if (audioRecord != null && audioClassifier != null) {
                tensorAudio.load(audioRecord);
                List<Classifications> results = audioClassifier.classify(tensorAudio);
                handleResults(results);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAudioClassification();
    }

    private void stopAudioClassification() {
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        if (audioClassifier != null) {
            audioClassifier.close();
            audioClassifier = null;
        }
    }

    private Notification getNotification(String title, String message) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .build();
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Audio Monitoring", NotificationManager.IMPORTANCE_HIGH);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
    }

    public class LocalBinder extends Binder {
        public AudioClassifierService getService() {
            return AudioClassifierService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    // Method for clients (activities)
    public void startMonitoring() {
        if (!isMonitoring) {
            if (!isInitialized) {
                initializeComponents();
            }
            isMonitoring = true;
            SafetyResponseManager.getInstance(this).setAudioClassificationRunning(true);
            startAudioClassification();
            Log.d(TAG, "Monitoring started.");
            dispatchNotification("Monitoring has been started.");
        }
    }

    public void stopMonitoring() {
        if (isMonitoring) {
            stopAudioClassification();
            isMonitoring = false;
            SafetyResponseManager.getInstance(this).setAudioClassificationRunning(false);
            Log.d(TAG, "Monitoring stopped.");
            dispatchNotification("Monitoring has been stopped.");
        }
    }

    public boolean isMonitoring() {
        return isMonitoring;
    }
}
