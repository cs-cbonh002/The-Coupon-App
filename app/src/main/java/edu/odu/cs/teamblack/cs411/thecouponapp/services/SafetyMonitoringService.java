package edu.odu.cs.teamblack.cs411.thecouponapp.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;
import org.tensorflow.lite.task.audio.classifier.Classifications;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.helper.CommunicationsHelper;

public class SafetyMonitoringService extends Service {
    final String TAG = "Audio Classifier Service";
    //notifications
    private static final String CHANNEL_ID = "SafetyMonitoringChannel";

    // TensorFlow Lite
    private AudioClassifier audioClassifier;
    private TensorAudio tensorAudio;
    private AudioRecord audioRecord;
    private ServiceHandler serviceHandler;

    private ScheduledThreadPoolExecutor executor;

    //call to action
    int count = 0;
    List<Classifications> output;

    //communications
    CommunicationsHelper communicationsHelper = new CommunicationsHelper();
    PendingIntent pendingPhoneIntent;

    PendingIntent pendingEmailIntent;

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
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            executor = new ScheduledThreadPoolExecutor(2);
            long lengthInMilliSeconds = (long) (((audioClassifier.getRequiredInputBufferSize() * 1.0f) /
                                audioClassifier.getRequiredTensorAudioFormat().getSampleRate()) * 1000);

            long interval = (long) (lengthInMilliSeconds * (1 - 0.5f));

            executor.scheduleAtFixedRate(
                    () -> {
                        tensorAudio.load(audioRecord);
                        //long inferenceTime = SystemClock.uptimeMillis();
                        output = audioClassifier.classify(tensorAudio);
                        //inferenceTime = SystemClock.uptimeMillis() - inferenceTime;
                        //Log.d(TAG, String.format("Inference time %d", inferenceTime));
                        Notification notification;
                        for (int i = 0; i < 2; i++) {
                            Log.d(TAG,output.get(0).getCategories().get(i).getLabel()+" " + output.get(0).getCategories().get(i).getScore() + " " + count);

                            switch (output.get(0).getCategories().get(i).getLabel()) {
                                case "Whistling":
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
                                    //notification = getNotification("Safety Monitoring", "you're " + category.getLabel() + "\nCount: " + count);
                                    //notificationManager.notify(4321, notification);
                                    break;
                                case "Silence":
                                case "Television":
                                    if (count > 0) {
                                        count--;
                                    }
                                    break;
                            }
                        }
                        if (count == 0) {
                            //stop documenting
                        } else if (count == 5/*&& pendingIntent == null*/) {
                            //start documenting
                            notification = getNotification("Safety Monitoring", "Start Documenting");
                            notificationManager.notify(4921, notification);
                        } else if (count == 7 && pendingPhoneIntent == null) {
                            //call 911
                            try {
                                communicationsHelper.sendSMS("540-214-0551");
                                pendingEmailIntent = communicationsHelper.sendEmail("marksilasgabriel@gmial.com","Sending from Porcupine Service", getApplicationContext());
                                notification = getNotification("Safety Monitoring", "Send Email", pendingEmailIntent);
                                notificationManager.notify(9322, notification);
                                pendingPhoneIntent = communicationsHelper.dialPhoneNumber("540-241-0551",getApplicationContext());
                                notification = getNotification("Safety Monitoring", "Calling 911", pendingPhoneIntent);
                                notificationManager.notify(9321, notification);
                            } catch (PendingIntent.CanceledException e) {
                                Log.e(CHANNEL_ID, "can't call", e);
                            }

                        }
                    },
                1,
                interval,
                TimeUnit.MILLISECONDS
            );
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

        try {
            //tensorflow
            final String model = "audioClassifier.tflite";
            audioClassifier = AudioClassifier.createFromFile(getApplicationContext(), model);
            tensorAudio = audioClassifier.createInputTensorAudio();
            audioRecord = audioClassifier.createAudioRecord();
            notification = getNotification("Safety Monitoring Service","Now listing");
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
        stopSelf();
        executor.shutdown();
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
    private Notification getNotification(String title, String message, PendingIntent pendingIntent) {

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent)
                .build();
    }

    private Notification getNotification(String title, String message) {

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .build();
    }

    @Override
    public IBinder onBind(Intent intent) {return null; }
}