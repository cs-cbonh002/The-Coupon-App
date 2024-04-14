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
import java.util.Queue;
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

    //call to action
    int count = 0;

    //phone
    Intent phoneIntent = new Intent(Intent.ACTION_CALL);
    PendingIntent pendingPhoneIntent;

    Intent emailIntent = new Intent(Intent.ACTION_SEND);
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
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    int numOfSamples = tensorAudio.load(audioRecord);
                    List<Classifications> output = audioClassifier.classify(tensorAudio);
                    Notification notification;

                    List<Category> finalOutput = new ArrayList<>(numOfSamples);
                    for (Classifications classifications : output) {
                        for (Category category : classifications.getCategories()) {
                            if (category.getScore() > 0.7f) {
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
                                    case "Slap, smack":
                                    case "Hands":
                                        count++;
                                        notification = getNotification("Safety Monitoring", "you're " + category.getLabel() + "\nCount: " + count);
                                        notificationManager.notify(4321, notification);
                                        break;
                                    default:
                                        if (count > 0) {
                                            count--;
                                        }
                                        break;
                                }
                                if (count == 0) {
                                    //stop documenting
                                } else if (count == 5/*&& pendingIntent == null*/) {
                                    //start documenting
                                    notification = getNotification("Safety Monitoring", "Start Documenting");
                                    notificationManager.notify(4321, notification);
                                } else if (count == 7 && pendingPhoneIntent == null) {
                                    //call 911
                                    try {
                                        sendSMS("540-214-0551");
                                        //sendEmail("marksilasgabriel@gmial.com","Sending from Porcupine Service");
                                        dialPhoneNumber("540-241-0551");
                                    } catch (PendingIntent.CanceledException e) {
                                        Log.e(CHANNEL_ID, "can't call", e);
                                    }
                                    notification = getNotification("Safety Monitoring", "Calling 911");
                                    notificationManager.notify(4321, notification);
                                }
                                Log.d(TAG, category.getLabel());
                            }
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
                .setScoreThreshold(0.7f)
                .setBaseOptions(baseOptions)
                .setMaxResults(1)
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

    ////////////////communications
    private void dialPhoneNumber(String phoneNumber) throws PendingIntent.CanceledException {

        if (getApplicationContext().checkSelfPermission(Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED) {
            Toast toast = Toast.makeText(this, "Permission not granted to call", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        phoneIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        phoneIntent.setData(Uri.parse("tel:" + phoneNumber));
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(phoneIntent);

        pendingPhoneIntent = PendingIntent.getActivity(this,0,phoneIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);
    }
    private void sendEmail(String email,String message) {
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Sending from Porcupine Service");
        emailIntent.putExtra(Intent.EXTRA_TEXT,message);

        emailIntent.setType("message/rfc822");

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(emailIntent);

        pendingEmailIntent = PendingIntent.getActivity(this,0,emailIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);
    }

    private void sendSMS(String phoneNumber) {
        if (getApplicationContext().checkSelfPermission(Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            Toast toast = Toast.makeText(this, "Permission not granted to call", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        String message = "Hello from Porcupine Service";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber,null,message,null,null);
    }
}