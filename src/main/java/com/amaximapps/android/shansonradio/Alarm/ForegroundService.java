package com.amaximapps.android.shansonradio.Alarm;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amaximapps.android.shansonradio.BuildConfig;
import com.amaximapps.android.shansonradio.R;
import com.amaximapps.android.shansonradio.services.Constants;
import com.amaximapps.android.shansonradio.ui.MainActivity;

/**
 * Foreground сервис, стартует при запуске будильника
 * для работы приложения в Doze_mode
 */

public class ForegroundService extends Service {

    private static final String LOG_TAG = "ForegroundService";
    public static boolean IS_SERVICE_RUNNING = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
                if (BuildConfig.DEBUG) {
                    Log.i(LOG_TAG, "Received Start Foreground Intent ");
                }
                String alarmNotification = intent.getStringExtra("DATE_TIME_ALARM_NOTIFY");
                showNotification(alarmNotification);
            } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
                if (BuildConfig.DEBUG) {
                    Log.i(LOG_TAG, "Received Stop Foreground Intent");
                }
                stopForeground(true);
                stopSelf();
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        return START_STICKY;
    }

    private void showNotification(String showNotification) {

        if (showNotification == null) {
            showNotification = "Off";
        }

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground);
        // Create intent that will bring our app to the front, as if it was tapped in the app
        // launcher
        Intent showTaskIntent = new Intent(getApplicationContext(), MainActivity.class);
        showTaskIntent.setAction(Intent.ACTION_MAIN);
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                showTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle("Радио Шансон")
                .setContentText(showNotification)
                .setSmallIcon(R.mipmap.ic_alarm)
                .setTicker("Радио Шансон")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)  //действие, при нажатии на текст уведомления
                .build();

        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
