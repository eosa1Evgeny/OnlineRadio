package com.amaximapps.android.shansonradio.services;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.amaximapps.android.shansonradio.BuildConfig;
import com.amaximapps.android.shansonradio.R;
import com.amaximapps.android.shansonradio.network.NetworkHelper;
import com.amaximapps.android.shansonradio.ui.*;
import com.amaximapps.android.shansonradio.ui.OnRadioChannelChangedListener;
import com.amaximapps.android.shansonradio.utility.SettingsStorage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

import xmlwise.Plist;
import xmlwise.XmlParseException;


/**
 * Фрагмент, в котором загружается и обрабатывается Plist
 */

public class DownloadPlistFragment extends Fragment implements OnRadioChannelChangedListener {

    public String mKeyPressed;
    private static final String TAG = "DownloadPlistFragment";
    private String mUrlForPlay = null;  //искомый URL
    private static final String GET_PLIST = "com.amaximapps.android.shansonradio.action.GET_PLIST";
    private Activity mActivity;

    public DownloadPlistFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();

        //запускаем ресивер
        IntentFilter f = new IntentFilter(RadioChannelIntentService.ACTION_COMPLETE_DOWNLOAD_PLIST);
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(onEvent, f);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStop() {
        super.onStop();
        //останавливаем ресивер
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(onEvent);
    }

    /**
     * Загружаем PList с сайта
     */
    public void DownloadFromUrl(Context context) {
        if (NetworkHelper.isNetworkConnected(context)) {
            //интент для IntentService загрузки Plist с сайта
            Intent i = new Intent(context, RadioChannelIntentService.class);
            i.putExtra(GET_PLIST, "com.amaximapps.android.shansonradio.action.GET_PLIST");
            // Url сервера
            i.putExtra(RadioChannelIntentService.RADIO_CHANNEL_SERVER_URL, Uri.parse(getString(R.string.radio_shanson_server)).toString());
            // вызываем сервис с параметрами
            context.startService(i);
        }
    }

    /**
     * Широковещательный ресивер
     */

    private BroadcastReceiver onEvent = new BroadcastReceiver() {
        public void onReceive(Context ctx, Intent i) {

            String completed = i.getStringExtra(RadioChannelIntentService.ACTION_COMPLETE_DOWNLOAD_PLIST);

            if (completed.equals("Completed")) {
                try {
                    Map<String, Object> result = getConfig(mActivity);
                    getMap(result);
                    setOnRadioChanged(mUrlForPlay);
                } catch (XmlParseException xmlException) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, xmlException.getMessage());
                    }
                } catch (IOException ioException) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, ioException.getMessage());
                    }
                }
            }

        }
    };

    /**
     * Парсим в Map
     *
     * @param context
     * @return
     * @throws XmlParseException
     * @throws IOException
     */
    public Map<String, Object> getConfig(Context context) throws XmlParseException, IOException {
        Map<String, Object> properties = null;

        try {
            String fileName = "Config.plist";

            String path = context.getFilesDir() + "/" + fileName;
            InputStream inputStream = null;
            FileInputStream fileInputStream = new FileInputStream(path);

            BufferedReader br = null;

            try {
                inputStream = fileInputStream;
                br = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                properties = Plist.fromXml(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                br.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return properties;
    }

    /**
     * разбираем Map на Key и Value
     * mKeyPressed - rb, который нажал пользователь
     * mUrlForPlay - URL, привязанный к этой кнопке
     *
     * @param mp
     */

    private void getMap(Map<String, Object> mp) {

        Object key;
        Object value;

        if (mp == null) return;

        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            key = pair.getKey().toString();
            value = pair.getValue().toString().replace("'", ":").replace("~", "/");

            //ловим нашу кнопку
            if ((key).equals(mKeyPressed)) {
                mUrlForPlay = value.toString();
            }
            if ((key).equals("opt")) {
                //количество позволенных нажатий
                SettingsStorage.storeInPreferences("COUNT_FROM_PLIST", value.toString());
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
    }


    /**
     * Возврат найденного урл в активити, выход
     *
     * @param serviceUrl = mUrlForPlay
     */

    @Override
    public void setOnRadioChanged(String serviceUrl) {

        //устанавливаем найденный урл в главную активность
        if (serviceUrl != null) {
            ((MainActivity) mActivity).setRadioUrl(serviceUrl);
        }
    }

    /**
     * Интефейс обмена, точка входа
     *
     * @param command
     */
    @Override
    public void setCommand(String[] command) {

        mKeyPressed = command[1];
        DownloadFromUrl(mActivity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}

