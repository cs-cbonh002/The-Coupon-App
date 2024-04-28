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

import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.EmergencyContact;

public class CommunicationsManager {
    private Context context;
    private NotificationManager notificationManager;
    public static final int COMMUNICATION_CALL = 1;  // 0b001
    public static final int COMMUNICATION_TEXT = 2;  // 0b010
    public static final int COMMUNICATION_EMAIL = 4; // 0b100

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

    public void sendCommunication(EmergencyContact contact) {
        int preferences = contact.getCommunicationPreferences();
        if (preferences != 0) {
            if ((preferences & COMMUNICATION_CALL) != 0) {
                makeCall(contact.getPhoneNumber());
            }
            if ((preferences & COMMUNICATION_TEXT) != 0) {
                sendSMS(contact.getPhoneNumber());
            }
            if ((preferences & COMMUNICATION_EMAIL) != 0) {
                sendEmail(contact.getEmail(), "Emergency", "Please check your messages for details.");
            }
            // if ((preferences & COMMUNICATION_EMAIL) != 0) {
            //     sendLocation(contact.getLocation());
            // }
        }
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

    // public void sendLocation(String location) {
    //     Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse ("coordinates" + location));
    //     sendNotification("Emergency Detected", intent);
    // }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(channel);
    }
}
