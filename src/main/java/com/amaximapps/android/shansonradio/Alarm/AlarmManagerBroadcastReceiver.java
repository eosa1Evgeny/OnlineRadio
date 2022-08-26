package com.amaximapps.android.shansonradio.Alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import com.amaximapps.android.shansonradio.services.Constants;
import com.amaximapps.android.shansonradio.ui.MainActivity;

/**
 * Широковещательное сообщение на запуск активити и сигнала будильника
 */

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {


    final public static String TAG = "broadcastReceiverSh";
    private final static String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";
    private final static String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private final static String ONE_TIME_ALARM_SHANSON = "ONE_TIME_ALARM_SHANSON";


    @Override
    public void onReceive(Context context, Intent intent) {


        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "mAlarm");
        //Осуществляем блокировку
        wl.acquire();


        try {
            String action = intent.getAction();
            //пропускаем перезагрузку
            if (action.equalsIgnoreCase(BOOT_ACTION)) {
                Log.e(TAG, "Broadcast receiver пропускаем загрузку");
                return;
            }

            //пропускаем изменение сети
            if (action.equalsIgnoreCase(CONNECTIVITY_ACTION)) {
                return;
            }

        } catch (Exception e) {
            e.getMessage();
        }


        //интент на запуск будильника
        if (intent.getBooleanExtra(ONE_TIME_ALARM_SHANSON, false)) {
            Intent scheduledIntent = new Intent(context, MainActivity.class);
            scheduledIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            scheduledIntent.setAction(Constants.ACTION.ALARM_FROM_BROADCAST);
            context.startActivity(scheduledIntent);
        }

        wl.release();
    }

}