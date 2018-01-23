package com.example.controlapp;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import java.util.Timer;
import java.util.TimerTask;

/* H klash auth xeirizetai to kommati hardware ths
suskeuhs, pou afora thn hxhtikh eidopoihsh. */

public class Sound {

    private Context context;
    private Ringtone Sound;

    public Sound(Context context) {
        this.context = context;
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Sound = RingtoneManager.getRingtone(this.context,notification);
    }

    public void stopSound() { Sound.stop(); }

    public void playSound(int seconds) {
        Sound.play();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                stopSound();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 1000*seconds);
    }
}
