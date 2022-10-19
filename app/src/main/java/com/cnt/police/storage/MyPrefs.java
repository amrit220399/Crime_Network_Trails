package com.cnt.police.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPrefs {
    private static final String PREF_NAME = "PoliceUser";
    private final SharedPreferences mSharedPreferences;

    public MyPrefs(Context context) {
        this.mSharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getUID() {
        return this.mSharedPreferences.getString("UID", "");
    }

    public void setUID(String uid) {
        this.mSharedPreferences.edit().putString("UID", uid).apply();
    }

    public String getCity() {
        return this.mSharedPreferences.getString("City", "");
    }

    public void setCity(String city) {
        this.mSharedPreferences.edit().putString("City", city).apply();
    }

    public String getPS() {
        return this.mSharedPreferences.getString("PS", "");
    }

    public void setPS(String PS) {
        this.mSharedPreferences.edit().putString("PS", PS).apply();
    }

    public String getName() {
        return this.mSharedPreferences.getString("Name", "");
    }

    public void setName(String name) {
        this.mSharedPreferences.edit().putString("Name", name).apply();
    }

    public String getDesignation() {
        return this.mSharedPreferences.getString("Designation", "");
    }

    public void setDesignation(String city) {
        this.mSharedPreferences.edit().putString("Designation", city).apply();
    }

    public boolean isNightMode() {
        return this.mSharedPreferences.getBoolean("NightMode", false);
    }

    public void setNightMode(boolean isNightMode) {
        this.mSharedPreferences.edit().putBoolean("NightMode", isNightMode).apply();
    }

    public void setNotificationMode(boolean isNotificationON) {
        this.mSharedPreferences.edit().putBoolean("Notification_ON", isNotificationON).apply();
    }

    public boolean isNotificationON() {
        return this.mSharedPreferences.getBoolean("Notification_ON", true);
    }

}
