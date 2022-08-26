package com.amaximapps.android.shansonradio.ui;

import android.app.Application;

import com.amaximapps.android.shansonradio.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Приложение
 */

public class ShansonRadioApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Verdana.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

    }
}
