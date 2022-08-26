package com.amaximapps.android.shansonradio.ui;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextClock;
import android.widget.TextView;


import com.amaximapps.android.shansonradio.Alarm.AlarmManagerBroadcastReceiver;
import com.amaximapps.android.shansonradio.Alarm.AlarmPlayer;
import com.amaximapps.android.shansonradio.BuildConfig;
import com.amaximapps.android.shansonradio.services.Constants;
import com.amaximapps.android.shansonradio.services.DownloadPlistFragment;
import com.amaximapps.android.shansonradio.R;
import com.amaximapps.android.shansonradio.Alarm.ForegroundService;
import com.amaximapps.android.shansonradio.utility.SettingsStorage;
import com.amaximapps.android.shansonradio.Alarm.CustomDateTimePicker;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import co.mobiwise.library.radio.RadioListener;
import co.mobiwise.library.radio.RadioManager;


public class MainActivity extends AppCompatActivity implements RadioListener, OnRadioChannelChangedListener {
    private final static String GET_ALL_STATION = "GET_ALL_STATION";
    private final static String ONE_TIME_ALARM_SHANSON = "ONE_TIME_ALARM_SHANSON";
    public String mRadioUrl = null;
    public String TAG = "mMainActivity";
    public TextView tvTextdata;
    private WifiManager.WifiLock mWifilock;
    private CustomDateTimePicker mCustomDateTimePicker;
    private ImageButton mBtnInfo;
    private TextClock tvTextClock;
    private TextView tvRadioChannelName;
    private Handler handlerError;
    private Thread.UncaughtExceptionHandler onError;


    int mYear;
    int month;
    int day_of_month;
    int hour_of_day;
    int minute;
    int second;

    String notifyAlarm = "Off";

    private AlarmManagerBroadcastReceiver alarm;
    private AlarmPlayer alarmPlayer;


    //сеттер mRadioUrl
    public void setRadioUrl(String radioUrl) {

        mRadioUrl=null;
        mRadioUrl = radioUrl;

        if (mRadioUrl != null) {
            SettingsStorage.storeInPreferences("mRadioUrl", mRadioUrl);
//
//              Задержка, для предотвращения "скоростного"
//              нажатия кнопок юзером
//
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startRadio();
                }
            }, 10000);
        } else {
            //настройка по умолчанию, если не нажата кнопка
            mRadioUrl = "http://chanson.hostingradio.ru:8041/chanson256.mp3";
        }
    }

    RadioManager mRadioManager;

    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //хранилище настроек
        SettingsStorage.sMainActivity = this;

        //радиоменеджер
        mRadioManager = RadioManager.with(this);
        mRadioManager.registerListener(this);

        //кнопка активити инфо
        mBtnInfo = findViewById(R.id.btnInfo);
        mBtnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //запуск активити инфо
                startInfoActivity();
            }
        });

        //получаем экземпляр будильника
        alarmPlayer = AlarmPlayer.getInstance();
        //дата на экране
        tvTextdata = findViewById(R.id.textDate);
        tvTextClock = findViewById(R.id.textClock);
        tvTextClock.setFormat24Hour("kk:mm:ss");

        //Аварийное сообщение
        tvRadioChannelName = findViewById(R.id.tvRadioChannelName);

        handlerError = new Handler(Looper.getMainLooper()) {
            public void handleMessage(android.os.Message msg) {
                // обновляем TextView
                tvRadioChannelName.setText("Ошибка! Нет данных");

                //радиоменеджер
                mRadioManager.disconnect();
                mRadioManager.connect();

            }
        };

       onError= new Thread.UncaughtExceptionHandler() {
                    public void uncaughtException(Thread thread, Throwable ex) {
                        if(BuildConfig.DEBUG) {
                            Log.e(TAG, "Uncaught exception", ex);
                        }
                    }
                };


        //Блокировка выключения WiFi, без этого приложение завершает проигрывание в фоне
        mWifilock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
        mWifilock.acquire();

        //фрагмент загрузки Plist
        if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
            getFragmentManager().beginTransaction().add(android.R.id.content, new DownloadPlistFragment(), "DOWNLOAD_PLIST").commit();
        }

        initLayout();

        CreateDateTimePicker();


 // Отлавливаем запуск будильника из планировщика

        if (getIntent().getAction().equals(Constants.ACTION.ALARM_FROM_BROADCAST)) {

            PowerManager pm2 = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = pm2.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "mAlarm");
            wakeLock.acquire(40000);

            //звук будильника
            alarmPlayer.startAlarmRingtone(getApplicationContext());

            // будильник прозвенел, сервис останавливаем
            if (ForegroundService.IS_SERVICE_RUNNING) {
                stopForegroundService();
            }
        }


    }//OnCreate

    private void startInfoActivity() {

        Intent i = new Intent(MainActivity.this, InfoActivity.class);
        startActivity(i);
    }

    private void initLayout() {
        // определяем шрифт "Одесса-мама"
        TextView tvRadioChannelName = findViewById(R.id.tvRadioChannelName);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Noteworthy.ttf");
        tvRadioChannelName.setTypeface(custom_font);
    }

    private static String GetToday() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMMM, d, yyyy", Locale.getDefault());
        String retval = dateFormat.format(new Date());
        return retval;
    }

    private void CreateDateTimePicker() {

        ImageButton btnAlarm = findViewById(R.id.btnAlarm);
        alarm = new AlarmManagerBroadcastReceiver();

        // picker даты и времени
        mCustomDateTimePicker = new CustomDateTimePicker(this, new CustomDateTimePicker.ICustomDateTimeListener() {
            @Override
            public void onSet(Dialog dialog, Calendar calendarSelected,
                              Date dateSelected, int year, String monthFullName,
                              String monthShortName, int monthNumber, int date,
                              String weekDayFullName, String weekDayShortName,
                              int hour24, int hour12, int min, int sec,
                              String AM_PM) {


                mYear = year;
                month = monthNumber;
                day_of_month = date;
                hour_of_day = hour24;
                minute = min;
                second = sec;

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.YEAR, mYear);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day_of_month);
                calendar.set(Calendar.HOUR_OF_DAY, hour_of_day);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, second);

                notifyAlarm = String.valueOf(mYear) + "." + String.valueOf(month + 1) + "." + String.valueOf(day_of_month) + " Time " + String.valueOf(hour_of_day) + ":" + String.valueOf(minute);

                setOnetimeTimer(calendar);
            }

            @Override
            public void onCancel() {
                //отмена
                CancelAlarm();
            }
        });

        /**
         * Pass Directly current time format it will return AM and PM if you set
         * false
         */
        mCustomDateTimePicker.set24HourFormat(true);

        /**
         * Pass Directly current data and time to show when it pop up
         */
        mCustomDateTimePicker.setDate(Calendar.getInstance());

        // кнопка будильник
        btnAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomDateTimePicker.showDialog();
            }
        });


    }

    /**
     * Foreground старт/стоп
     */

    public void startForegroundService() {

        Intent service = new Intent(MainActivity.this, ForegroundService.class);

        if (!ForegroundService.IS_SERVICE_RUNNING) {
            service.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            service.putExtra("DATE_TIME_ALARM_NOTIFY", notifyAlarm);
            ForegroundService.IS_SERVICE_RUNNING = true;

            startService(service);
        }

    }

    public void stopForegroundService() {

        Intent service = new Intent(MainActivity.this, ForegroundService.class);

        if (ForegroundService.IS_SERVICE_RUNNING) {
            service.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
            ForegroundService.IS_SERVICE_RUNNING = false;

            startService(service);
        }

    }


    /**
     * Установка будильника
     *
     * @param calendar Дата и время
     */

    public void setOnetimeTimer(Calendar calendar) {

        /**
         *
         * Запускаем Foreground сервис
         *
         */
        if (!ForegroundService.IS_SERVICE_RUNNING) {
            startForegroundService();
        }


        /**
         * Запрашиваем у пользователя андроид 6 и выше
         * разрешение на добавление будильника в менеджер оптимизизации батареи
         *
         */

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            String pkg = getPackageName();
            PowerManager pm = getSystemService(PowerManager.class);
            if (!pm.isIgnoringBatteryOptimizations(pkg)) {
                Intent i =
                        new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                                .setData(Uri.parse("package:" + pkg));
                startActivity(i);
            }

            /**
             *
             * с помощью setExactAndAllowWhileIdle ( андроид 6)
             */

            Intent intent = new Intent(getBaseContext(), AlarmManagerBroadcastReceiver.class);
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            intent.putExtra(ONE_TIME_ALARM_SHANSON, Boolean.TRUE);//Задаем параметр интента

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 97, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            /**
             * Для  андроидов (5 и ниже)
             */
            Intent intent = new Intent(getBaseContext(), AlarmManagerBroadcastReceiver.class);
            intent.putExtra(ONE_TIME_ALARM_SHANSON, Boolean.TRUE);//Задаем параметр интента
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 98, intent, 0);

            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }


    /**
     * отмена будильника
     */

    public void CancelAlarm() {
        Context context = getBaseContext();
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra("ONE_TIME_ALARM", Boolean.TRUE);//Задаем параметр интента
        PendingIntent sender = PendingIntent.getBroadcast(context, 99, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);//Отменяем будильник, связанный с интентом данного класса


        if (alarmPlayer != null) {
            alarmPlayer.stopPlay();
        }

         //Останавливаем Foreground сервис

        if (ForegroundService.IS_SERVICE_RUNNING) {
            stopForegroundService();
        }
    }


    /**
     * интерфейсный метод
     */

    @Override
    public void setOnRadioChanged(String serviceUrl) {

    }


    /**
     * команды, приходящие из RadioChannelFragment
     * интерфейсный метод
     *
     * @param command
     */
    @Override
    public void setCommand(String[] command) {

        if (command[0] == GET_ALL_STATION) {

            DownloadPlistFragment fragment = (DownloadPlistFragment) getFragmentManager().findFragmentByTag("DOWNLOAD_PLIST");
            if (fragment != null) {
                fragment.setCommand(command);
            }
        }
    }

    /**
     * Запуск радио
     */

    public void startRadio() {


        try {

            mRadioManager.startRadio(mRadioUrl);

            //увеличиваем счетчик успешных воспроизведений на +1
            SettingsStorage.storeInPreferencesCounterPressKey();

        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, getLocalClassName() + e.getMessage() + " ***startRadio***");
            }
        }
    }

    /**
     * Остановка радио
     */

    public void stopRadio() {

        if (mRadioManager.isPlaying()) {
            mRadioManager.stopRadio();
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        if (mRadioManager != null) {
            mRadioManager.connect();
        }

        //устанавливаем сегодняшнюю дату на экране
        String today = GetToday();
        tvTextdata.setText(today);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onStop() {
        super.onStop();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mRadioManager.isPlaying()) {
            mRadioManager.stopRadio();
        }
        try {

            mRadioManager.disconnect();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, e.getMessage());
            }
        }

        //не трогать, будет отключаться при воспроизведении через wifi (проверено на андроид 6)
        mWifilock.acquire();
    }


    /**
     * методы радио
     */

    @Override
    public void onRadioLoading() {
    }

    @Override
    public void onRadioConnected() {
    }

    @Override
    public void onRadioStarted() {
    }

    @Override
    public void onRadioStopped() {
    }

    @Override
    public void onMetaDataReceived(String s, String s1) {
    }

    @Override
    public void onError() {
        handlerError.sendEmptyMessage(0);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}