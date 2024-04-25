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

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Objects;

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
import edu.odu.cs.teamblack.cs411.thecouponapp.helper.CommunicationsHelper;

public class PorcupineService extends Service {
    private static final String CHANNEL_ID = "PorcupineServiceChannel";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String ACCESS_KEY = "ir/zJzrvkSCpbURXMlpFz1nL5VEHIsNf2snqMTDwXDiEDzc4Cp4zzQ==";
    private PorcupineManager porcupineManager;
    private int numUtterances;
    private ServiceHandler serviceHandler;

    ArrayList<String> keywords = new ArrayList<>();

    Porcupine.BuiltInKeyword[] pWords;

    CommunicationsHelper communicationsHelper = new CommunicationsHelper();
    PendingIntent pendingPhoneIntent;
    PendingIntent pendingEmailIntent;

    private final Handler handler = new Handler(Looper.getMainLooper());

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            try {
                porcupineManager = new PorcupineManager.Builder()
                        .setAccessKey(ACCESS_KEY)
                        .setKeywords(pWords) //built in keywords
                        //.setKeywordPaths(new String[]{"stopHittingMe.ppn","please-please-please_en_android.ppn","please-i-don--t-know_en_android_v3_0_0.ppn","stopHittingMe.ppn"})
                        .setSensitivities(new float[]{0.7f,0.7f,0.7f,0.7f})
                        .build(
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
                    getNotification("Wake word service", "Say "+ pWords[0] +"!");
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
        Looper serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }
    private final PorcupineManagerCallback porcupineManagerCallback = (keywordIndex) -> {

        Notification notification;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        final String contentText = numUtterances == 1 ? " time!" : " times!";
        switch (keywordIndex){
            case 0:
                //calling
                try {
                    communicationsHelper.sendSMS("540-214-0551");
                    pendingEmailIntent = communicationsHelper.sendEmail("marksilasgabriel@gmial.com","Sending from Porcupine Service", getApplicationContext());
                    notification = getNotification("Safety Monitoring", "Send Email", pendingEmailIntent);
                    notificationManager.notify(8322, notification);
                    pendingPhoneIntent = communicationsHelper.dialPhoneNumber("540-241-0551",getApplicationContext());
                    notification = getNotification("Safety Monitoring", "Calling 911", pendingPhoneIntent);
                    notificationManager.notify(8321, notification);
                } catch (Exception e) {
                    Log.e(CHANNEL_ID, "can't call",e);
                }
                break;
            case 1:
                //start documenting
                numUtterances++;
                notification = getNotification(
                        "Start Documenting",
                        "Detected " + numUtterances + contentText);
                notificationManager.notify(8323, notification);
                break;
            case 2:
                //stop documenting
                numUtterances--;
                notification = getNotification(
                        "Stop Documenting",
                        "Detected " + numUtterances + contentText);
                notificationManager.notify(8323, notification);
                break;
            case 3:
                //pause audio classifier
                if (isMyServiceRunning()) {
                    stopService();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startService();
                            //pauses for 5 seconds
                        }
                    }, 5000);
                }

                notification = getNotification(
                        "start stop safety monitoring",
                        "Detected " + numUtterances + contentText);
                notificationManager.notify(8323, notification);
                break;
            default:

                break;
        }
    };

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SafetyMonitoringService.class.getName().equals(service.service.getClassName())) {
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

        keywords = Objects.requireNonNull(intent.getExtras()).getStringArrayList("keywords");
        assert keywords != null;
        pWords = new Porcupine.BuiltInKeyword[]{
                Porcupine.BuiltInKeyword.valueOf(keywords.get(0)),
                Porcupine.BuiltInKeyword.valueOf(keywords.get(1)),
                Porcupine.BuiltInKeyword.valueOf(keywords.get(2)),
                Porcupine.BuiltInKeyword.valueOf(keywords.get(3)),
        };

        numUtterances = 0;
        createNotificationChannel();

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

    ///////start stop safety monitoring
    private void startService() {
        Intent serviceIntent = new Intent(getApplicationContext(), SafetyMonitoringService.class);
        ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);
    }

    private void stopService() {
        Intent serviceIntent = new Intent(getApplicationContext(), SafetyMonitoringService.class);
        getApplicationContext().stopService(serviceIntent);
    }
}


