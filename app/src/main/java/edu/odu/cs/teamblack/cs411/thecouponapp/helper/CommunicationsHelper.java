package edu.odu.cs.teamblack.cs411.thecouponapp.helper;

import android.Manifest;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import edu.odu.cs.teamblack.cs411.thecouponapp.ui.activities.FacadeActivity;

public class CommunicationsHelper {
    public PendingIntent sendEmail(String email,String message, Context context) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Sending from Porcupine Service");
        emailIntent.putExtra(Intent.EXTRA_TEXT,message);

        emailIntent.setType("message/rfc822");

       return PendingIntent.getActivity(context,0,emailIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);
    }

    public PendingIntent dialPhoneNumber(String phoneNumber, Context context) throws PendingIntent.CanceledException {

        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
        phoneIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        phoneIntent.setData(Uri.parse("tel:" + phoneNumber));
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(phoneIntent);

        return PendingIntent.getActivity(context,0,phoneIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);
    }

    public void sendSMS(String phoneNumber) {
        String message = "Hello from Safety Monitoring Service";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber,null,message,null,null);
    }
}