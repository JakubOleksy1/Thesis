// TimerService.java
package com.tetris.tetris;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

public class Timer extends Service {


    public static final String ACTION_TIMER_FINISHED = "com.tetris.tetris.ACTION_TIMER_FINISHED";

    private CountDownTimer countDownTimer;
    private long timeRemaining = Game.TOTAL_COUNTDOWN_TIME; // Initialize with total countdown time

    @Override
    public void onCreate() {
        super.onCreate();
        startTimer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // If the service is killed, it will not be restarted automatically
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cancel the countDownTimer to avoid leaks
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeRemaining, Game.COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                updateTimerText(timeRemaining);
            }

            @Override
            public void onFinish() {
                // Notify that the timer has finished by sending a broadcast
                Intent intent = new Intent(ACTION_TIMER_FINISHED);
                sendBroadcast(intent);
                // Stop the service
                stopSelf();
            }
        };
        countDownTimer.start();
    }

    private void updateTimerText(long millisUntilFinished) {
        // Handle the timer update (if needed) when the service is running in the background
    }
}
