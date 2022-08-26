package com.amaximapps.android.shansonradio.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;


import com.amaximapps.android.shansonradio.R;

public class InfoActivity extends AppCompatActivity {

    public final static String TWITTER_URL = "https://www.twitter.com/amaximapps/";
    public final static String FACEBOOK_URL = "https://www.facebook.com/amaximappsinc/";
    public final static String AMAXIM_URL = "https://itunes.apple.com/us/app/chanson-radio-fm-hi-fi/id491999571?ls=1&mt=8/";

    public final static String TAG = "InfoActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        ImageButton iSupport = findViewById(R.id.image_support);
        ImageButton iAmaxim = findViewById(R.id.image_amaxim);
        ImageButton iEmail = findViewById(R.id.image_email);
        ImageButton iFacebook = findViewById(R.id.image_facebook);
        ImageButton iTwitter = findViewById(R.id.image_twitter);


/**
 *  Support
 */
        iSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailSupport();
            }
        });
/**
 *  Ссылка
 */
        iAmaxim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(AMAXIM_URL));
                Intent chooser = Intent.createChooser(i, "Выберете, чем открыть ссылку");

                //Возможна ли хоть одна активность?
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivity(chooser);
                }
            }



        });

/**
 * СМС другу
 */
        iEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendsmsFriend();
            }
        });


/**
 * FaceBook
 */

        iFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(FACEBOOK_URL));
                Intent chooser = Intent.createChooser(i, "Выберете, чем открыть ссылку");

                //Возможна ли хоть одна активность?
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivity(chooser);
                }
            }
        });
/**
 * Twitter
 */

        iTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(TWITTER_URL));
                Intent chooser = Intent.createChooser(i, "Выберете, чем открыть ссылку");

                //Возможна ли хоть одна активность?
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivity(chooser);
                }
            }
        });


    }//onCreate


    private void sendsmsFriend(){

        String msg="Привет! \n\nОбратите внимание на\n приложение Best Chanson\n Radio Hi-Fi - Вам понравится!\n\n https://itunes.apple.com/us/app/chanson-radio-fm-hi-fi/id491999571?ls=1&mt=8";
        Intent sms=new Intent(Intent.ACTION_SENDTO,  Uri.parse("smsto:"));
        sms.putExtra("sms_body", msg);

        try {
            startActivity(Intent.createChooser(sms, "Отправка sms..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Log.e(TAG, "Ошибка отправки sms "+ex.getMessage());
        }

    }


    private void sendEmailSupport() {

        String[] TO = {"support@amaximapps.com"};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getPhoneInfo());
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Напишите Ваш вопрос или пожелания здесь.\n");

        try {
            startActivity(Intent.createChooser(emailIntent, "Отправка mail..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Log.e(TAG, "Ошибка отправки email "+ex.getMessage());
        }
    }


    /**
     * Получить сведения о телефоне
     */

    private String getPhoneInfo() {

        String pinfo="";
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            pinfo = pInfo.versionName;
            pinfo+="."+pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.getMessage();
        }

        String os = android.os.Build.VERSION.RELEASE; // OS version
        int api = Build.VERSION.SDK_INT;      // API Level (на будущее)
        String device = android.os.Build.DEVICE;          // Device
        String model = android.os.Build.MODEL;            // Model
        String product = android.os.Build.PRODUCT;         // Product (на будущее)
        String result = "BCR_v" + pinfo + "_Android_ " + os + "_" + device + "_" + model;

        return result;
    }

        @Override
        public void onBackPressed () {
            super.onBackPressed();

        }

    }
