package edu.odu.cs.teamblack.cs411.thecouponapp.helper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class CommunicationsHelper {
   public Intent call(Context context, String phoneNumber) {
        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
        phoneIntent.setData(Uri.parse("tel:" + phoneNumber));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED) {
            Toast toast = Toast.makeText(context, "Permission not granted to call", Toast.LENGTH_LONG);
            toast.show();
            return null;
        }
        return phoneIntent;
    }

  /*  void requestPermission(){
        ActivityCompat.requestPermissions(this , new String[]{Manifest.permission.CALL_PHONE}, 55);
    }*/
}