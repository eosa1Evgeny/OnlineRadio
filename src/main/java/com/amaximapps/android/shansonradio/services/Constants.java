package com.amaximapps.android.shansonradio.services;

/**
 * Константы
 */

public class Constants {

    public interface ACTION {

        public static String ALARM_FROM_BROADCAST = "com.amaximapps.foregroundservice.action.alarm";
        public static String STARTFOREGROUND_ACTION = "com.amaximapps.foregroundservice.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.amaximapps.foregroundservice.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }


}
