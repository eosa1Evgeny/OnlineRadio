package com.amaximapps.android.shansonradio.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.amaximapps.android.shansonradio.R;
import com.amaximapps.android.shansonradio.utility.SettingsStorage;

import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Фрагмент управления кнопками таймера
 */

public class TimerButtonsFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    private final static String TIMER_BUTTON = "TIMER_BUTTON";
    private final static String TAG = "TimerButtonsFragment";

    SegmentedGroup timerSegmentedGroup;
    RadioButton rbTimerOff;
    String currentKey;


    public TimerButtonsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.timer_buttons_fragment, container, false);
        return (rootView);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MainActivity mainActivity = (MainActivity) getActivity();
        timerSegmentedGroup = mainActivity.findViewById(R.id.timer_segmented_group);
        timerSegmentedGroup.setOnCheckedChangeListener(this);
        rbTimerOff = mainActivity.findViewById(R.id.buttonOff);


        try {

            currentKey = SettingsStorage.getInPreferences(TIMER_BUTTON);

            int resBtn = Integer.valueOf(currentKey);
            RadioButton rbStart = mainActivity.findViewById(resBtn);
            if (rbStart != null) {
                rbStart.setChecked(true);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());

        }

        //кнопка по умолчанию (выкл)
        if (currentKey == null) {

            currentKey = String.valueOf(R.id.buttonOff);
            SettingsStorage.storeInPreferences(TIMER_BUTTON, String.valueOf(R.id.buttonOff));
        }


    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {


        switch (checkedId) {
            case R.id.button30:
                SettingsStorage.storeInPreferences(TIMER_BUTTON, String.valueOf(R.id.button30));
                startTimer(1800000);
                break;
            case R.id.button60:
                SettingsStorage.storeInPreferences(TIMER_BUTTON, String.valueOf(R.id.button60));
                startTimer(3600000);
                break;
            case R.id.button90:
                SettingsStorage.storeInPreferences(TIMER_BUTTON, String.valueOf(R.id.button90));
                startTimer(5400000);
                break;
            case R.id.button120:
                SettingsStorage.storeInPreferences(TIMER_BUTTON, String.valueOf(R.id.button120));
                startTimer(7200000);
                break;
            case R.id.buttonOff:
                SettingsStorage.storeInPreferences(TIMER_BUTTON, String.valueOf(R.id.buttonOff));
                cancelTimer();
                break;
            default:

        }
    }


    CountDownTimer countTimer = null;

    /**
     * Таймер
     *
     * @param ms время
     */
    void startTimer(long ms) {

        cancelTimer();
        countTimer = new CountDownTimer(ms, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {

                setDisabledButtonsInGroups();
                ((MainActivity) getActivity()).stopRadio();
            }
        };
        countTimer.start();
    }

    /**
     * сброс таймера
     */
    void cancelTimer() {
        if (countTimer != null)
            countTimer.cancel();
    }

    /**
     * сброс кнопок, после того как таймер отработал
     * установка кнопки таймера Выкл в активное состояние
     */


    private void setDisabledButtonsInGroups() {

        timerSegmentedGroup.setOnCheckedChangeListener(null);
        timerSegmentedGroup.clearCheck();
        timerSegmentedGroup.setOnCheckedChangeListener(this);

        rbTimerOff.setChecked(true);

    }


}
