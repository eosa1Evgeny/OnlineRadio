package com.amaximapps.android.shansonradio.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.amaximapps.android.shansonradio.BuildConfig;
import com.amaximapps.android.shansonradio.R;
import com.amaximapps.android.shansonradio.utility.SettingsStorage;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Фрагмент управления кнопками выбора каналов , громкости,  старт-стоп
 */

public class RadioChannelFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    private final static String TAG = RadioChannelFragment.class.getSimpleName();

    String[] channelNames;

    SegmentedGroup channelSegmentedGroup1;
    SegmentedGroup channelSegmentedGroup2;
    SegmentedGroup channelSegmentedGroup3;


    private static final String GET_ALL_STATION = "GET_ALL_STATION";
    private static final String CURRENT_RADIO_CHANNEL_KEY = "CURRENT_RADIO_CHANNEL_KEY";
    private static final String CURRENT_RADIO_CHANNEL_KEY_ID = "CURRENT_RADIO_CHANNEL_KEY_ID";
    private MainActivity mMainActivity;
    private int mCheckId;
    private Animation mAnimation;
    private String curKey;
    private TextView tvRadioChannelName;
    private Map<String, String> mMap;


    ImageButton imagebutton;
    String currentKeyRes;
    SeekBar music = null;
    AudioManager mgr = null;
    Boolean play = false;
    boolean isFirstRun = true;
    int resBtn;


    public RadioChannelFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMainActivity = (MainActivity) activity;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.radio_channel_buttons_fragment, container, false);
        channelNames = getResources().getStringArray(R.array.channel_names);
        return (rootView);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imagebutton = mMainActivity.findViewById(R.id.imageButtonStart);

        channelSegmentedGroup1 = mMainActivity.findViewById(R.id.radio_segmented_group1);
        channelSegmentedGroup1.setOnCheckedChangeListener(this);

        channelSegmentedGroup2 = mMainActivity.findViewById(R.id.radio_segmented_group2);
        channelSegmentedGroup2.setOnCheckedChangeListener(this);

        channelSegmentedGroup3 = mMainActivity.findViewById(R.id.radio_segmented_group3);
        channelSegmentedGroup3.setOnCheckedChangeListener(this);

        tvRadioChannelName = mMainActivity.findViewById(R.id.tvRadioChannelName);
        tvRadioChannelName.setText("Радио Шансон");

        //парсим названия канала
        XmlParser();


        //может прийти null
        try {
            //радио
            currentKeyRes = SettingsStorage.getInPreferences(CURRENT_RADIO_CHANNEL_KEY_ID);

            if (currentKeyRes != null) {
                // Установка кнопок каналов при первом запуске,
                // ставим сегментом, иначе не сработает листенер

                resBtn = Integer.valueOf(currentKeyRes);
                RadioButton rbStart = mMainActivity.findViewById(resBtn);
                if (rbStart != null) {
                    int parentId = ((View) rbStart.getParent()).getId();
                    SegmentedGroup sg = mMainActivity.findViewById(parentId);
                    sg.check(resBtn);
                }
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Error in onActivityCreated");
            }
        }


        /**
         * Обработка кнопки старт/стоп (круглая)
         */

        imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRadioChannel();
            }
        });


        //громкость
        mgr = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        music = mMainActivity.findViewById(R.id.seekBar);
        initBar(music, AudioManager.STREAM_MUSIC);//for Volume this is necessary


    }   ///onActivityCreated()


    XmlPullParser prepareXpp() {
        return getResources().getXml(R.xml.button);
    }


    private void XmlParser() {

        mMap = new HashMap<String, String>();

        String tmp = "";
        String attrib = "";

        try {


            XmlPullParser xpp = prepareXpp();


            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    // начало документа
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    // начало тэга
                    case XmlPullParser.START_TAG:

                        tmp = "";
                        for (int i = 0; i < xpp.getAttributeCount(); i++) {
                            tmp = xpp.getAttributeValue(i);
                        }
                        if (!TextUtils.isEmpty(tmp)) {
                            attrib = tmp;
                        }

                        break;
                    // конец тэга
                    case XmlPullParser.END_TAG:
                        break;
                    // содержимое тэга
                    case XmlPullParser.TEXT:
                        String tagTmp = xpp.getText();
                        mMap.put(attrib, tagTmp);
                        break;
                    default:
                        break;
                }
                // следующий элемент
                xpp.next();
            }
            //END_DOCUMENT

        } catch (XmlPullParserException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
    }


    public void startAnimation() {

        //запуск радио
        if (!isFirstRun) {
            mMainActivity.stopRadio();
            String[] params = new String[2];
            params[0] = GET_ALL_STATION;
            params[1] = curKey;
            mMainActivity.setCommand(params);
        }

        //анимация
        mAnimation = new RotateAnimation(0.0f, 4 * 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimation.setFillAfter(true);
        imagebutton.setImageResource(R.drawable.ic_start_radio);
        mAnimation.setDuration(10000L);
        imagebutton.setAnimation(mAnimation);
        imagebutton.startAnimation(mAnimation);

        mAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                imagebutton.setImageResource(R.drawable.ic_start_radio);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imagebutton.setImageResource(R.drawable.ic_stop_radio);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                imagebutton.setImageResource(R.drawable.ic_start_radio);
            }
        });

        play = false;
    }


    /**
     * Включаем выбранный радиоканал
     */
    String value;

    private void startRadioChannel() {

        curKey = SettingsStorage.getInPreferences(CURRENT_RADIO_CHANNEL_KEY);


        if (curKey == null) {
            value = "Радио Шансон";
        }

        //устанавливаем название радио
        value = mMap.get(curKey);
        tvRadioChannelName.setText(value);


        if (play) {
            startAnimation();
            isFirstRun = false;
        } else {

            //при последующих запусках и нажатиии на кнопку "стоп", отображается "старт радио"
            if (!isFirstRun) {
                imagebutton.setImageResource(R.drawable.ic_start_radio);
                mMainActivity.stopRadio();
            }
            play = true;
            isFirstRun = false;
        }

    }


    /**
     * Громкость
     *
     * @param bar    Seekbar
     * @param stream поток
     */

    private void initBar(SeekBar bar, final int stream) {
        bar.setMax(mgr.getStreamMaxVolume(stream));
        bar.setProgress(mgr.getStreamVolume(stream));

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {

                mgr.setStreamVolume(stream, progress, AudioManager.FLAG_PLAY_SOUND);
            }

            public void onStartTrackingTouch(SeekBar bar) {
                // no-op
            }

            public void onStopTrackingTouch(SeekBar bar) {
                // no-op
            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onStart() {
        super.onStart();

    }


    private Boolean changeGroup = false;

    /**
     * Листенер нажатия кнопок каналов
     *
     * @param group
     * @param checkedId
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        if (group != null && checkedId > -1 && changeGroup == false) {
            if (group == channelSegmentedGroup1) {
                changeGroup = true;
                channelSegmentedGroup2.clearCheck();
                channelSegmentedGroup3.clearCheck();
                changeGroup = false;
            } else if (group == channelSegmentedGroup2) {
                changeGroup = true;
                channelSegmentedGroup1.clearCheck();
                channelSegmentedGroup3.clearCheck();
                changeGroup = false;
            } else if (group == channelSegmentedGroup3) {
                changeGroup = true;
                channelSegmentedGroup1.clearCheck();
                channelSegmentedGroup2.clearCheck();
                changeGroup = false;
            }
        }


        if (!isFirstRun) {
            if (play == false) play = true;
        }


        mCheckId = checkedId;

        switch (mCheckId) {

            case R.id.btnShanson:
                setNameChannel(R.string.chanson1);
                setChannelButtonId(R.id.btnShanson);
                startRadioChannel();
                break;
            case R.id.btn24:
                setNameChannel(R.string.chanson24);
                setChannelButtonId(R.id.btn24);
                startRadioChannel();
                break;
            case R.id.btnPortal:
                setNameChannel(R.string.portal);
                setChannelButtonId(R.id.btnPortal);
                startRadioChannel();
                break;
            case R.id.btnLyric:
                setNameChannel(R.string.lirika);
                setChannelButtonId(R.id.btnLyric);
                startRadioChannel();
                break;
            case R.id.btnSoul:
                setNameChannel(R.string.dusha);
                setChannelButtonId(R.id.btnSoul);
                startRadioChannel();
                break;
            case R.id.btnMama:
                setNameChannel(R.string.chanson2);
                setChannelButtonId(R.id.btnMama);
                startRadioChannel();
                break;
            case R.id.btnBards:
                setNameChannel(R.string.bardy);
                setChannelButtonId(R.id.btnBards);
                startRadioChannel();
                break;
            case R.id.btnRozenbaum:
                setNameChannel(R.string.rosenbaum);
                setChannelButtonId(R.id.btnRozenbaum);
                startRadioChannel();
                break;
            case R.id.btnVysotsky:
                setNameChannel(R.string.vysotsky);
                setChannelButtonId(R.id.btnVysotsky);
                startRadioChannel();
                break;
            case R.id.btnMichailov:
                setNameChannel(R.string.mihailov);
                setChannelButtonId(R.id.btnMichailov);
                startRadioChannel();
                break;
            case R.id.btnKrug:
                setNameChannel(R.string.krug);
                setChannelButtonId(R.id.btnKrug);
                startRadioChannel();
                break;
            case R.id.btnLeps:
                setNameChannel(R.string.leps);
                setChannelButtonId(R.id.btnLeps);
                startRadioChannel();
                break;

        }


    }


    /**
     * Запоминаем идентификатор ресурса кнопки (для восстановления при включении)
     *
     * @param resourseKeyId
     */

    private void setChannelButtonId(int resourseKeyId) {

        SettingsStorage.storeInPreferences(CURRENT_RADIO_CHANNEL_KEY_ID, String.valueOf(resourseKeyId));
    }


    /**
     * Получаем атрибут name из ресурса, отбрасываем то, что до слеша
     *
     * @param channelKey
     * @return строка с именем (Key)
     */
    private void setNameChannel(int channelKey) {

        String resWithSlash = getResources().getResourceName(channelKey).toString();

        int indexOfslash = resWithSlash.indexOf('/');
        String channelName = resWithSlash.substring(indexOfslash + 1);
        //сохраняем в настройках название кнопки канала
        SettingsStorage.storeInPreferences(CURRENT_RADIO_CHANNEL_KEY, channelName);
    }

}



