package edu.odu.cs.teamblack.cs411.thecouponapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

public class SecurePreferences {

    private static final String PREFERENCES_FILE_KEY = "secure_preferences";
    private static final String PIN_KEY = "pin_key";
    private SharedPreferences sharedPreferences;

    public SecurePreferences(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREFERENCES_FILE_KEY,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            // Handle the error.
        }
    }

    public void saveUserPin(String pin) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PIN_KEY, pin);
        editor.apply();
    }

    public String getUserPin() {
        return sharedPreferences.getString(PIN_KEY, null);
    }
}
