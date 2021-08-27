package com.coocaa.remoteplatform.baseability.abilities.rtc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d(TAG, "onReceive: " + "boot: " + intent.getBooleanExtra("boot", false));
        Log.d(TAG, "onReceive: " + "shutdown: " + intent.getBooleanExtra("shutdown", false));
        if (intent.getBooleanExtra("boot", false)) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("switch", Context.MODE_PRIVATE);
            String switchJson = sharedPreferences.getString("switchJson", "");
            Log.d(TAG, "onReceive: switchJson length:  " + switchJson.length());
            if (switchJson.length() <= 0) {
                return;
            }
            Gson gson = new Gson();
            Alarm alarm = gson.fromJson(switchJson, Alarm.class);
            if (alarm == null) {
                return;
            }
            AlarmImpl.setAlarm(alarm, context);
        } else if (intent.getBooleanExtra("shutdown", false)) {
            AlarmImpl.shutdown();
        }
    }
}