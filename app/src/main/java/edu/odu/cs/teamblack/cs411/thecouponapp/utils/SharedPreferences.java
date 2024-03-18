package edu.odu.cs.teamblack.cs411.thecouponapp.utils;

import android.content.Context;

public class SharedPreferences {

    private static final String PREFERENCES_NAME = "MyAppPreferences";

    public static void saveAccessToken(Context context, String accessToken) {
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("accessToken", accessToken);
        editor.apply();
    }

    public static String getAccessToken(Context context) {
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", null);
    }

    public static void clearAccessToken(Context context) {
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("accessToken");
        editor.apply();
    }
}
