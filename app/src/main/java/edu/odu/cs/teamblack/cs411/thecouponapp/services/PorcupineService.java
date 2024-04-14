/*
    Copyright 2021 Picovoice Inc.
    You may not use this file except in compliance with the license. A copy of the license is
    located in the "LICENSE" file accompanying this source.
    Unless required by applicable law or agreed to in writing, software distributed under the
    License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
    express or implied. See the License for the specific language governing permissions and
    limitations under the License.
*/
package edu.odu.cs.teamblack.cs411.thecouponapp.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.video.FileOutputOptions;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoRecordEvent;
import androidx.camera.view.CameraController;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.video.AudioConfig;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import java.io.File;
import java.util.ArrayList;

import ai.picovoice.porcupine.Porcupine;
import ai.picovoice.porcupine.PorcupineActivationException;
import ai.picovoice.porcupine.PorcupineActivationLimitException;
import ai.picovoice.porcupine.PorcupineActivationRefusedException;
import ai.picovoice.porcupine.PorcupineActivationThrottledException;
import ai.picovoice.porcupine.PorcupineException;
import ai.picovoice.porcupine.PorcupineInvalidArgumentException;
import ai.picovoice.porcupine.PorcupineManager;
import ai.picovoice.porcupine.PorcupineManagerCallback;
import edu.odu.cs.teamblack.cs411.thecouponapp.R;
import edu.odu.cs.teamblack.cs411.thecouponapp.ui.activities.FacadeActivity;

public class PorcupineService extends Service {
    private static final String CHANNEL_ID = "PorcupineServiceChannel";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String ACCESS_KEY = "ir/zJzrvkSCpbURXMlpFz1nL5VEHIsNf2snqMTDwXDiEDzc4Cp4zzQ==";
    private PorcupineManager porcupineManager;
    private int numUtterances;
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;

    ArrayList<String> keywords = new ArrayList<String>();

    Porcupine.BuiltInKeyword[] porcupineWords;

    //phone
    Intent phoneIntent = new Intent(Intent.ACTION_CALL);
    PendingIntent pendingPhoneIntent;
    Intent emailIntent = new Intent(Intent.ACTION_SEND);
    PendingIntent pendingEmailIntent;

    //camera

    private Recording recording = null;

    LifecycleCameraController controller;

    //camera
    @SuppressLint("MissingPermission")
    private void recordVideo(LifecycleCameraController controller) {
        if (recording != null) {
            recording.stop();
            recording = null;
            return;
        }

        File outFile = new File(getFilesDir(),"testRecording.mp4");
        recording = controller.startRecording(
            new FileOutputOptions.Builder(outFile).build(),
            AudioConfig.create(false),
            getApplication().getMainExecutor(),
            videoRecordEvent -> {
                if (videoRecordEvent instanceof VideoRecordEvent.Finalize) {
                    VideoRecordEvent.Finalize finalizeEvent = (VideoRecordEvent.Finalize) videoRecordEvent;

                    if (finalizeEvent.hasError()) {
                        recording.close();
                        recording = null;

                        Toast.makeText(
                                getApplicationContext(),
                                "video capture failed",
                                Toast.LENGTH_LONG
                        ).show();
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "video capture succeeded",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
            }
        );
    }
    private final Handler handler = new Handler(Looper.getMainLooper());

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            try {
                porcupineManager = new PorcupineManager.Builder()
                        .setAccessKey(ACCESS_KEY)
                        .setKeywords(porcupineWords)
                        .setSensitivities(new float[]{0.7f,0.6f,0.7f,0.7f}).build(
                                getApplicationContext(),
                                porcupineManagerCallback);
                porcupineManager.start();

            } catch (PorcupineInvalidArgumentException e) {
                onPorcupineInitError(e.getMessage());
            } catch (PorcupineActivationException e) {
                onPorcupineInitError("AccessKey activation error");
            } catch (PorcupineActivationLimitException e) {
                onPorcupineInitError("AccessKey reached its device limit");
            } catch (PorcupineActivationRefusedException e) {
                onPorcupineInitError("AccessKey refused");
            } catch (PorcupineActivationThrottledException e) {
                onPorcupineInitError("AccessKey has been throttled");
            } catch (PorcupineException e) {
                onPorcupineInitError("Failed to initialize Porcupine: " + e.getMessage());
            }

            Notification notification = porcupineManager == null ?
                    getNotification("Porcupine init failed", "Service will be shut down") :
                    getNotification("Wake word service", "Say 'Computer'!");
            startForeground(1234, notification);

        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Thread.MAX_PRIORITY);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }
    private final PorcupineManagerCallback porcupineManagerCallback = (keywordIndex) -> {

        final String contentText = numUtterances == 1 ? " time!" : " times!";
        Notification n = getNotification(
                "IDK what you said",
                "Detected " + numUtterances + contentText);
        switch (keywordIndex){
            case 0:
                //calling
                try {
                    sendSMS("540-214-0551");
                    //sendEmail("marksilasgabriel@gmial.com","Sending from Porcupine Service");
                    dialPhoneNumber("540-241-0551");
                } catch (PendingIntent.CanceledException e) {
                    Log.e(CHANNEL_ID, "can't call",e);
                }
                n = getNotification(
                        "Calling",
                        "Detected " + numUtterances + contentText);
                break;
            case 1:
                //start documenting
                numUtterances++;
                recordVideo(controller);
                n = getNotification(
                        "Start Documenting",
                        "Detected " + numUtterances + contentText);
                break;
            case 2:
                //stop documenting
                numUtterances--;
                n = getNotification(
                        "Stop Documenting",
                        "Detected " + numUtterances + contentText);
                gotoFacade();
                break;
            case 3:
                //pause audio classifier
                if (isMyServiceRunning(SafetyMonitoringService.class)) {
                    stopService();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startService();
                            //pauses for 5 seconds
                        }
                    }, 5000);
                } else {
                    startService();
                }

                n = getNotification(
                        "start stop safety monitoring",
                        "Detected " + numUtterances + contentText);
                break;
            default:

                break;
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(1234, n);
    };

    private boolean isMyServiceRunning(Class serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private void createNotificationChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(
                CHANNEL_ID,
                "Porcupine",
                NotificationManager.IMPORTANCE_HIGH);

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(notificationChannel);
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        keywords = intent.getExtras().getStringArrayList("keywords");
        porcupineWords = new Porcupine.BuiltInKeyword[]{
                Porcupine.BuiltInKeyword.valueOf(keywords.get(0)),
                Porcupine.BuiltInKeyword.valueOf(keywords.get(1)),
                Porcupine.BuiltInKeyword.valueOf(keywords.get(2)),
                Porcupine.BuiltInKeyword.valueOf(keywords.get(3)),
        };

        numUtterances = 0;
        createNotificationChannel();

        controller = new LifecycleCameraController(this);
        //controller.bindToLifecycle(Lifecycle.get);
        controller.setEnabledUseCases(CameraController.VIDEO_CAPTURE);
        controller.setCameraSelector(CameraSelector.DEFAULT_BACK_CAMERA);

        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        return Service.START_STICKY;
    }

    private void onPorcupineInitError(String message) {
        Intent i = new Intent("PorcupineInitError");
        i.putExtra("errorMessage", message);
        sendBroadcast(i);
    }

    private Notification getNotification(String title, String message) {

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingPhoneIntent)
                .build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (porcupineManager != null) {
            try {
                porcupineManager.stop();
                porcupineManager.delete();
            } catch (PorcupineException e) {
                Log.e("PORCUPINE", e.toString());
            }
        }
        stopSelf();
        super.onDestroy();
    }
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

    private void gotoFacade() {
        pendingPhoneIntent = PendingIntent.getActivity(
                this,
                0,
                new Intent(this, FacadeActivity.class),
                PendingIntent.FLAG_MUTABLE);
    }

    ///////start stop safety monitoring
    private void startService() {
        Intent serviceIntent = new Intent(getApplicationContext(), SafetyMonitoringService.class);
        getApplicationContext().startForegroundService(serviceIntent);
    }

    private void stopService() {
        Intent serviceIntent = new Intent(getApplicationContext(), SafetyMonitoringService.class);
        getApplicationContext().stopService(serviceIntent);
    }
}


