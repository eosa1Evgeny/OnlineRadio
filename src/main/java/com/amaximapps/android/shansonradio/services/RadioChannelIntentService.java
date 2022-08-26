package com.amaximapps.android.shansonradio.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * Created by admin on 21.12.2017.
 * Загрузка файла plist во внутреннее храннилище (не флешка)
 */

public class RadioChannelIntentService extends IntentService {

    private static final String TAG = "RadioChanIntentService";
    public static final String ACTION_COMPLETE_DOWNLOAD_PLIST = "com.amaximapps.android.shansonradio.action.DOWNLOAD_PLIST_COMPLETE";
    public static String RADIO_CHANNEL_SERVER_URL = "RadioChannelServerUrl";
    private static final String GET_PLIST="com.amaximapps.android.shansonradio.action.GET_PLIST";


    public RadioChannelIntentService() {
        super("RadioChannelIntentService");
    }


    @Override
    public void onHandleIntent(Intent i) {
        Log.d(TAG, "onHandleIntent");

        if (i == null) {
            Log.e(TAG, "requested with NULL intent " + new Date().toString());
            return;
        }


        //отбрасываем залетные интенты
        String intentGetPlist = i.getStringExtra(GET_PLIST);
        if (!intentGetPlist.equals("com.amaximapps.android.shansonradio.action.GET_PLIST")){
            return;
        }


        // получаем Url сервера, без .toString() не работает
        String radioChannelUrl = i.getStringExtra(RADIO_CHANNEL_SERVER_URL).toString();

        try {
            String filename = "Config.plist";
            File output = new File(getApplicationContext().getFilesDir(), filename);

            if (output.exists()) {
                output.delete();
            }

            URL url = new URL(radioChannelUrl);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
            BufferedOutputStream out = new BufferedOutputStream(fos);

            try {
                InputStream in = c.getInputStream();
                byte[] buffer = new byte[8192];
                int len = 0;

                while ((len = in.read(buffer)) >= 0) {
                    out.write(buffer, 0, len);
                }

                out.flush();

            } finally {
                fos.getFD().sync();
                out.close();
                c.disconnect();
            }

            ///Широковещательное сообщение
            Intent intent = new Intent(ACTION_COMPLETE_DOWNLOAD_PLIST);
            intent.putExtra(ACTION_COMPLETE_DOWNLOAD_PLIST, "Completed");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } catch (IOException e2) {
            Log.e(getClass().getName(), "Exception in download", e2);
        }
    }

}
