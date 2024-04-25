package edu.odu.cs.teamblack.cs411.thecouponapp.managers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.telephony.SmsManager;

import androidx.core.app.NotificationCompat;

public class CommunicationsManager {
    private Context context;
    private NotificationManager notificationManager;

    public CommunicationsManager(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    public void sendNotification(String title, String text, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(1, builder.build());
    }

    public void sendSMS(String phoneNumber) {
        String message = "Emergency situation detected.";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);

        Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("smsto:" + phoneNumber));
        smsIntent.putExtra("sms_body", message);
        sendNotification("Emergency Detected", "Tap to send an SMS to the emergency contact.", smsIntent);
    }

    public void makeCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        sendNotification("Emergency Detected", "Tap to call the emergency contact.", intent);
    }

    public void sendEmail(String emailAddress, String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + emailAddress));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        sendNotification("Emergency Detected", "Tap to send an Email to the emergency contact.", intent);
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(channel);
    }
}
