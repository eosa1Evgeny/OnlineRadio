package com.amaximapps.android.shansonradio.Alarm;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Проигрываание сигнала будильника
 */

public class AlarmPlayer implements MediaPlayer.OnCompletionListener {
    private static final String TAG = "mediaplayerAlarm";
    private MediaPlayer mMediaPlayer;
    private static AlarmPlayer sAlarmPlayer;

    /**
     * Синглтон
     *
     * @return
     */

    public static AlarmPlayer getInstance() {
        if (sAlarmPlayer == null) sAlarmPlayer = new AlarmPlayer();
        return sAlarmPlayer;
    }

    public void startAlarmRingtone(final Context context) {

        try {
            AssetFileDescriptor afd = context.getAssets().openFd("NokiaSMS.mp3");
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void stopPlay() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mMediaPlayer.stop();
    }
}
