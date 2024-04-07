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
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

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

    //phone
    Intent phoneIntent = new Intent(Intent.ACTION_CALL);
    PendingIntent pendingIntent;

    private final PorcupineManagerCallback porcupineManagerCallback = (keywordIndex) -> {

        final String contentText = numUtterances == 1 ? " time!" : " times!";
        Notification n = getNotification(
                "IDK what you said",
                "Detected " + numUtterances + contentText);
        switch (keywordIndex){
            case 0:
                //calling
                try {
                    dialPhoneNumber("555-555-5555");
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
                break;
            default:

                break;
        }


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(1234, n);
    };

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

        numUtterances = 0;
        createNotificationChannel();

        try {
            porcupineManager = new PorcupineManager.Builder()
                    .setAccessKey(ACCESS_KEY)
                    .setKeywords(new Porcupine.BuiltInKeyword[]{Porcupine.BuiltInKeyword.COMPUTER, Porcupine.BuiltInKeyword.PORCUPINE,Porcupine.BuiltInKeyword.BLUEBERRY})
                    .setSensitivities(new float[]{0.7f,0.6f,0.7f}).build(
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
                .setContentIntent(pendingIntent)
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

        super.onDestroy();
    }
    private void dialPhoneNumber(String phoneNumber) throws PendingIntent.CanceledException {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED) {
            Toast toast = Toast.makeText(this, "Permission not granted to call", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        phoneIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        phoneIntent.setData(Uri.parse("tel:" + phoneNumber));
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(phoneIntent);

        pendingIntent = PendingIntent.getActivity(this,0,phoneIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);

        //AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        //this.startActivity(phoneIntent);
        //pendingIntent.send();
    }
    private void gotoFacade() {
        pendingIntent = PendingIntent.getActivity(
                this,
                0,
                new Intent(this, FacadeActivity.class),
                PendingIntent.FLAG_MUTABLE);
    }
}


