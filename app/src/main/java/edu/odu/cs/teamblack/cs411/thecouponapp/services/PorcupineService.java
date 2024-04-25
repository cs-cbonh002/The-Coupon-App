package edu.odu.cs.teamblack.cs411.thecouponapp.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;
import ai.picovoice.porcupine.Porcupine;
import ai.picovoice.porcupine.PorcupineException;
import ai.picovoice.porcupine.PorcupineManager;
import ai.picovoice.porcupine.PorcupineManagerCallback;
import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.managers.SafetyResponseManager;
import edu.odu.cs.teamblack.cs411.thecouponapp.managers.SafetyResponseManager.TriggerType;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments.WakeWordsFragment;

public class PorcupineService extends Service {
    private static final String TAG = "PORCUPINE";
    private static final String CHANNEL_ID = "PorcupineServiceChannel";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String ACCESS_KEY = "ir/zJzrvkSCpbURXMlpFz1nL5VEHIsNf2snqMTDwXDiEDzc4Cp4zzQ==";
    private static final int NOTIFICATION_ID = 4321;
    private static final long DEBOUNCE_DELAY = 1000;
    private static final int WAKE_WORD_COMMUNICATION_INDEX = 0;
    private static final int WAKE_WORD_LOGGING_START_INDEX = 1;
    private static final int WAKE_WORD_LOGGING_STOP_INDEX = 2;
    private static final int WAKE_WORD_TOGGLE_AUDIO_CLASSIFIER_INDEX = 3;
    private final IBinder binder = new LocalBinder();
    private PorcupineManager porcupineManager;
    private boolean isWakeWordDetectionActive = false;
    private long lastDetectionTime = 0;
    private WakeWordDetectionListener listener;
    private SafetyResponseManager safetyResponseManager;

    public interface WakeWordDetectionListener {
        void onWakeWordDetected(int keywordIndex);
        void onError(Exception e);
    }

    public void setWakeWordDetectionListener(WakeWordDetectionListener listener) {
        this.listener = listener;
    }

    private final PorcupineManagerCallback porcupineManagerCallback = keywordIndex -> {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDetectionTime > DEBOUNCE_DELAY) {
            lastDetectionTime = currentTime;
            switch (keywordIndex) {
                case WAKE_WORD_COMMUNICATION_INDEX:
                    safetyResponseManager.handleTrigger(TriggerType.WAKE_WORD, "COMMUNICATE");
                    break;
                case WAKE_WORD_LOGGING_START_INDEX:
                    safetyResponseManager.handleTrigger(TriggerType.WAKE_WORD, "START_LOGGING");
                    break;
                case WAKE_WORD_LOGGING_STOP_INDEX:
                    safetyResponseManager.handleTrigger(TriggerType.WAKE_WORD, "STOP_LOGGING");
                    break;
                case WAKE_WORD_TOGGLE_AUDIO_CLASSIFIER_INDEX:
                    safetyResponseManager.handleTrigger(TriggerType.WAKE_WORD, "TOGGLE_AUDIO_CLASSIFIER");
                    break;
            }
        }
    };


    private ArrayList<Porcupine.BuiltInKeyword> loadKeywords(List<String> wakeWords) {
        ArrayList<Porcupine.BuiltInKeyword> loadedKeywords = new ArrayList<>();
        for (String keyword : wakeWords) {
            try {
                Porcupine.BuiltInKeyword keywordEnum = Porcupine.BuiltInKeyword.valueOf(keyword.toUpperCase().replace(" ", "_"));
                loadedKeywords.add(keywordEnum);
            } catch (IllegalArgumentException e) {
                Log.e("PORCUPINE", "Invalid keyword: " + keyword, e);
            }
        }
        return loadedKeywords;
    }

    public void startWakeWordDetection(List<String> wakeWords) {
        Log.d(TAG, "Starting wake word detection");
        if (isWakeWordDetectionActive) {
            stopWakeWordDetection(); // Stop the existing PorcupineManager
        }
        try {
            ArrayList<Porcupine.BuiltInKeyword> keywords = loadKeywords(wakeWords);
            if (!keywords.isEmpty()) {
                porcupineManager = buildPorcupineManager(keywords);
                porcupineManager.start();
                isWakeWordDetectionActive = true;
            } else {
                Log.e(TAG, "No wake words provided, cannot start detection.");
            }
        } catch (PorcupineException e) {
            Log.e(TAG, "Failed to start Porcupine Manager", e);
        }
    }

    public void stopWakeWordDetection() {
        if (porcupineManager != null) {
            try {
                porcupineManager.stop();
                porcupineManager.delete();
                porcupineManager = null;
                isWakeWordDetectionActive = false;
                lastDetectionTime = 0;
                Log.d("PORCUPINE", "Wake word detection stopped.");
            } catch (PorcupineException e) {
                Log.e("PORCUPINE", "Failed to stop Porcupine Manager", e);
                if (listener != null) {
                    listener.onError(e);
                }
            }
        }
    }
    private ArrayList<String> getCustomKeywordPaths(String[] customWakeWords) {
        ArrayList<String> keywordPaths = new ArrayList<>();
        for (String wakeWord : customWakeWords) {
            String path = "assets/" + wakeWord + ".ppn"; // Assuming the .ppn files are named after the wake words
            keywordPaths.add(path);
        }
        return keywordPaths;
    }

    private PorcupineManager buildPorcupineManager(ArrayList<Porcupine.BuiltInKeyword> keywords) throws PorcupineException {
        if (keywords.isEmpty()) {
            Log.e(TAG, "No keywords provided for PorcupineManager.");
            throw new IllegalArgumentException("No keywords provided");
        }
        return new PorcupineManager.Builder()
                .setAccessKey(ACCESS_KEY)
                .setKeywords(keywords.toArray(new Porcupine.BuiltInKeyword[0]))
                .setSensitivities(new float[]{0.5f, 0.5f, 0.5f, 0.5f})
                .build(this, porcupineManagerCallback);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");

        // Create notification channel for the service's foreground notification
        createNotificationChannel();
        safetyResponseManager = SafetyResponseManager.getInstance(this);
    }


    public boolean isWakeWordDetectionActive() {
        return isWakeWordDetectionActive;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("selected_wake_words_bundle")) {
            Bundle wakeWordsBundle = intent.getBundleExtra("selected_wake_words_bundle");
            if (wakeWordsBundle != null) {
                List<String> wakeWords = new ArrayList<>();
                for (String key : wakeWordsBundle.keySet()) {
                    wakeWords.add(wakeWordsBundle.getString(key));
                }
                if (!wakeWords.isEmpty()) {
                    startWakeWordDetection(wakeWords);
                } else {
                    Log.e(TAG, "No wake words received to start detection.");
                }
            }
        }
        Notification notification = createNotification();
        startForeground(NOTIFICATION_ID, notification);
        return START_STICKY;
    }

    private Notification createNotification() {
        // If you are targeting API level 26+, you must implement a user-visible notification channel
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Wake Word Detection", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Notification channel for wake word detection service");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Wake Word Detection Active")
                .setContentText("Tap to return to the app.")
                .setSmallIcon(R.drawable.ic_mic) // Replace with your own drawable icon
                .setContentIntent(createPendingIntent())
                .build();
    }

    private PendingIntent createPendingIntent() {
        Intent notificationIntent = new Intent(this, WakeWordsFragment.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Porcupine Service", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public PorcupineService getService() {
            return PorcupineService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopWakeWordDetection();
    }
}