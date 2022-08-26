package com.amaximapps.android.shansonradio.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.amaximapps.android.shansonradio.ui.MainActivity;

/**
 * Created by admin on 23.12.2017.
 *
 * Сохраняем и восстанавливаем состояние кнопок при включении и выключении
 */

public class SettingsStorage {

    private static final String TAG = "SettingStorage";
    public static MainActivity sMainActivity;
    private static SharedPreferences mSettings;
    //файл настроек preferences
    private static final String APP_PREFERENCES = "amaximappssettings";

    private static Boolean checkActivity() {

        Boolean result;

        if (sMainActivity == null) {
            result = false;
        }else {
            result = true;
        }

        return result;
    }


    /**
     * Сохраняем настройки в preference
     * Вызывается при каждом изменении
     * ключ, что соханяем
     *
     * @param dataStore
     */
    public static void storeInPreferences(String KeyName, String dataStore) {

        if (checkActivity()) {

            mSettings = sMainActivity.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString(KeyName, dataStore);
            editor.apply();
        }
    }



    /**
     * Сохраняем Булево значение в preference
     * Вызывается при каждом изменении
     * ключ, что соханяем
     *
     * @param dataStore
     */
    public static void storeInPreferencesBool(String KeyName, Boolean dataStore) {


        if (checkActivity()) {

            mSettings = sMainActivity.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean(KeyName, dataStore);
            editor.apply();
        }
    }

    /**
     * Булево значение получаем
     * @param KeyName
     * @return
     */
    public static boolean getInPreferencesBool(String KeyName) {

        Boolean tmp=true;

        if (checkActivity()) {
            try {
                mSettings = sMainActivity.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                //чтение
                if (mSettings != null) {
                    tmp = mSettings.getBoolean(KeyName ,true);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return tmp;
    }


    /**
     * Получаем сохраненные настройки
     *
     * @return адрес последнего URL, или ERROR в случае ошибки
     * KeyName ключ preferences
     */
    public static String getInPreferences(String KeyName) {


        String tmp = null;

        if (checkActivity()) {
            try {
                mSettings = sMainActivity.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                //чтение
                if (mSettings != null) {
                    tmp = mSettings.getString(KeyName, null);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return tmp;

    }

    /**
     * Счетчик нажатия кнопок
     */
    public static void storeInPreferencesCounterPressKey() {

        if (checkActivity()) {
            //получим значение счетчика
            String keyName = "PressKey";
            Integer storedInPlist = 0;
            ///из Plist
            try {
                String tmp = getInPreferences("COUNT_FROM_PLIST");
                if (tmp == null) {
                    tmp = "0";
                }

                storedInPlist = Integer.valueOf(tmp);
            } catch (Exception e) {

                Log.e(TAG, e.getMessage());
            }


            mSettings = sMainActivity.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
            //чтение
            Integer countPlay = mSettings.getInt(keyName, 0);
            //прибавляем значение счетчика
            countPlay++;

            if (countPlay >= storedInPlist) {
                //Toast.makeText(sMainActivity, "Количество нажатий превысило 15", Toast.LENGTH_LONG).show();
                //TODO сделать, что бы не включался плеер
            } else {
                //сохраняем +1 значение счетчика
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putInt(keyName, countPlay);
                editor.apply();
            }
        }
    }
}
