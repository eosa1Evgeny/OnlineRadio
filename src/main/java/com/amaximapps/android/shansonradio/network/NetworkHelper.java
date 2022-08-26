package com.amaximapps.android.shansonradio.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by admin on 21.12.2017.
 */


public class NetworkHelper {
    private static boolean isConnectedToWifi;
    private static WifiConnectionChange sListener;


    public interface WifiConnectionChange {
        void wifiConnected(boolean connected);
    }


    /** Используется BroadcastReceiver для отслеживания события подключения к WiFi
     * Only used by Connectivity_Change broadcast receiver
     * @param connected
     */
    public static void setWifiConnected(boolean connected) {
        isConnectedToWifi = connected;
        if (sListener!=null)
        {
            sListener.wifiConnected(connected);
            sListener = null;
        }
    }

    public static void setWifiListener(WifiConnectionChange listener) {
        sListener = listener;
    }


    public static NetworkInfo getNetworkInfo(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }



    /**
     * Возвращает подключено ли устройство к WiFi
     * @return
     */
    public static Boolean isWiFiConnected(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled()) {      // WiFi адаптер включен
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo.getNetworkId() == -1) {
                return false;       // нет соединения с Access Point
            }
            return true;            // есть соединение с Access Point
        } else {
            return false;           // WiFi адаптер выключен
        }
    }

    /**
     * Возвращает подключено ли устройство к Сотовой сети
     * @return
     */
    public static Boolean isMobileConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    /**
     * Возвращает подключено ли устройство к любой сети
     * @return
     */
    public static Boolean isNetworkConnected(Context context){
        return (isWiFiConnected(context) || isMobileConnected(context));
    }

}

