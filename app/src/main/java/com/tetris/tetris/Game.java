package com.tetris.tetris;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

import java.io.Serializable;


public class Game extends AppCompatActivity implements View.OnClickListener, SensorEventListener, Serializable {

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private View rootView;
    int newRotation = 0;
    private boolean show_message_once = false;
    public static boolean give_prize = false;
    private long remainingPauseTime = 0;
    private static final long MAX_PAUSE_DURATION = 5 * 60 * 1000;

    int i = 1;
    private boolean isCalibrated = false; // Flag to indicate if calibration is done
    private float calibrationOffset = 0.5f;

    public boolean info_pause = false;

    private static final String TIMER_PREFS = "TimerPrefs";
    private static final String REMAINING_TIME_KEY = "RemainingTime";
    public static final long TOTAL_COUNTDOWN_TIME = 300000; // 60 seconds in milliseconds
    public static final long COUNTDOWN_INTERVAL = 1000; // 1 second interval

    private boolean isTimerRunning = false;
    private TextView timerTextView;
    private CountDownTimer countDownTimer;
    private long timeRemaining;
    DrawView drawView;
    GameState gameState;

    int points;
    RelativeLayout gameButtons;
    Button rotateAc;
    FrameLayout game;
    Button pause;
    TextView score;
    Handler handler;
    Runnable loop;
    int delayFactor;
    int delay;
    int delayLowerLimit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        give_prize = false;
        show_message_once = false;
        gameState = new GameState(20, 14, TetraminoType.getRandomTetramino());
        rootView = findViewById(android.R.id.content);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        drawView = new DrawView(this, gameState);
        drawView.setBackgroundColor(Color.DKGRAY);

        game = new FrameLayout(this);
        gameButtons = new RelativeLayout(this);

        delay = 500;
        delayLowerLimit = 200;
        delayFactor = 2;

        rotateAc = new Button(this);
        rotateAc.setText(R.string.rotate_ac);
        rotateAc.setId(R.id.rotate_ac);

        pause = new Button(this);
        pause.setText(R.string.pause);
        pause.setId(R.id.pause);

        score = new TextView(this);
        score.setText(R.string.score);
        score.setId(R.id.score);
        score.setTextSize(30);
        score.setTextColor(Color.WHITE);

        timerTextView = new TextView(this);
        timerTextView.setText(R.string.time);
        timerTextView.setId(R.id.time);


        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams downButton = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams pausebutton = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams scoretext = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams timetext = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        gameButtons.setLayoutParams(rl);
        gameButtons.addView(rotateAc);
        gameButtons.addView(pause);
        gameButtons.addView(score);
        gameButtons.addView(timerTextView);

        downButton.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        downButton.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        downButton.width = 700;
        downButton.height = 350;

        pausebutton.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        pausebutton.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        pausebutton.width = 300;
        pausebutton.height = 150;

        scoretext.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        scoretext.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        timetext.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        timetext.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        rotateAc.setLayoutParams(downButton);
        pause.setLayoutParams(pausebutton);
        score.setLayoutParams(scoretext);
        timerTextView.setLayoutParams(timetext);

        game.addView(drawView);
        game.addView(gameButtons);
        setContentView(game);

        View rotateACButtonListener = findViewById(R.id.rotate_ac);
        rotateACButtonListener.setOnClickListener(this);

        View pauseButtonListener = findViewById(R.id.pause);
        pauseButtonListener.setOnClickListener(this);

        if (savedInstanceState != null) {
            timeRemaining = savedInstanceState.getLong(REMAINING_TIME_KEY);
        } else {
            SharedPreferences prefs = getSharedPreferences(TIMER_PREFS, MODE_PRIVATE);
            timeRemaining = prefs.getLong(REMAINING_TIME_KEY, TOTAL_COUNTDOWN_TIME);
        }

        updateTimerText(timeRemaining);
        startTimer();
        give_prize = false;

        handler = new Handler(Looper.getMainLooper());
        loop = new Runnable() {
            public void run() {
                if (gameState.status) {
                    if (!gameState.pause) {
                        boolean success = gameState.moveFallingTetraminoDown();
                        if (!success) {
                            gameState.paintTetramino(gameState.falling);
                            gameState.lineRemove();

                            gameState.pushNewTetramino(TetraminoType.getRandomTetramino());

                            if (gameState.score % 10 == 9 && delay >= delayLowerLimit) {
                                delay = delay / delayFactor + 1;
                            }
                            gameState.incrementScore();
                            give_prize = gameState.isGivePrize();
                            if(give_prize == true) {
                                if (show_message_once == false) {
                                    Toast.makeText(getApplicationContext(), "You won a prize!", Toast.LENGTH_SHORT).show();
                                    show_message_once = true;
                                }
                            }
                        }
                        drawView.invalidate();
                        handler.postDelayed(this, delay);
                    } else {
                        handler.postDelayed(this, delay);
                    }
                } else {
                    pause.setText(R.string.start_new_game);
                }
            }
        };
        loop.run();
    }

    @Override
    public void onClick(View action) {
        if (action == rotateAc) {
            gameState.rotateFallingTetraminoAntiClock();

        } else if (action == pause) {
            if (gameState.status) {
                if (gameState.pause) {
                    gameState.pause = false;
                    pause.setText(R.string.pause);
                    if (remainingPauseTime > 0) {
                        startPauseTimer(remainingPauseTime);
                    }
                } else {
                    pause.setText(R.string.play);
                    gameState.pause = true;
                    Toast.makeText(getApplicationContext(), "Max pause is 5 min. Resume when ready.", Toast.LENGTH_SHORT).show();
                    long startTime = System.currentTimeMillis();
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    startPauseTimer(MAX_PAUSE_DURATION);
                }
            } else {
                pause.setText(R.string.start_new_game);
                Intent intent = new Intent(Game.this, MainActivity.class);
                startActivity(intent);

            }

        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float Rotation = event.values[1];
        if (!isCalibrated) {

            calibrationOffset = Rotation;
            isCalibrated = true;
        }
        float calibratedRotation = Rotation - calibrationOffset;

        if (calibratedRotation > 0.8 || newRotation == 3) {
            gameState.moveFallingTetraminoRight();
            newRotation = 3;
            if (Rotation < 0) {
                newRotation = 0;
            }
        } else if (calibratedRotation < -0.8 || newRotation == -3) {

            gameState.moveFallingTetraminoLeft();
            newRotation = -3;
            if (calibratedRotation > 0) {
                newRotation = 0;
            }

        } else if (calibratedRotation >= -0.8 && calibratedRotation <= 0.8) {
            newRotation = 0;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(REMAINING_TIME_KEY, timeRemaining);
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeRemaining, COUNTDOWN_INTERVAL) {

            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                updateTimerText(timeRemaining);
            }

            @Override
            public void onFinish() {

                if (gameState.pause == false && gameState.status == true) {
                    finishingGame();
                } else if (gameState.pause == true) {
                    Paused();
                } else {
                    Lost();
                }
            }
        };
        countDownTimer.start();
        isTimerRunning = true;
    }

    private void updateTimerText(long millisUntilFinished) {
        int seconds = (int) (millisUntilFinished / 1000);
        timerTextView.setText(formatTime(seconds));

    }
    private void navigateBack() {
        // Close the current activity and navigate to the root activity to close the app
        Intent intent = new Intent(this, Login.class);
        resetTimer();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        navigateBack();
    }

    private String formatTime(long totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%d min %02d sec", minutes, seconds);
    }
    private void resetTimer() {
        timeRemaining = TOTAL_COUNTDOWN_TIME;
        updateTimerText(timeRemaining);
    }

    private void finishingGame() {
        if (gameState.status) {
            // If the game is still ongoing, post a delayed Runnable to check again later
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finishingGame(); // Check the status again after a delay
                }
            }, 1000); // Repeat after 1000 milliseconds (1 second, adjust as needed)
        } else {
            if (gameState.isGivePrize()) {
                points = gameState.isGivePoints();
                Intent intent = new Intent(Game.this, Redeem.class);
                intent.putExtra("points", points);
                startActivity(intent);
            } else {
                resetTimer();
                Toast.makeText(getApplicationContext(), "Thank you for playing", Toast.LENGTH_SHORT).show();
                finish();
            }

        }

    }

    private void Paused() {
        if (gameState.isGivePrize()) {
            Intent intent = new Intent(Game.this, Redeem.class);
            startActivity(intent);
        } else {
            resetTimer();
            Toast.makeText(getApplicationContext(), "Thank you for playing", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void Lost() {
        if (gameState.isGivePrize()) {
            Intent intent = new Intent(Game.this, Redeem.class);
            startActivity(intent);
        } else {
            resetTimer();
            Toast.makeText(getApplicationContext(), "Thank you for playing", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void startPauseTimer(long pauseDuration) {
        countDownTimer = new CountDownTimer(pauseDuration, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingPauseTime = millisUntilFinished;
                // You can update a TextView to display the remaining pause time if needed
                // For example: timerTextView.setText(formatTime(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                // When the pause timer finishes, automatically resume the game
                gameState.pause = false;
                pause.setText(R.string.pause);

                // Reset the remaining pause time
                remainingPauseTime = 0;

                // Restart the main game timer
                startTimer();
            }
        };
        countDownTimer.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        SharedPreferences prefs = getSharedPreferences(TIMER_PREFS, MODE_PRIVATE);
        Intent timerServiceIntent = new Intent(this, Timer.class);
        startService(timerServiceIntent);
        timeRemaining = prefs.getLong(REMAINING_TIME_KEY, TOTAL_COUNTDOWN_TIME);
        updateTimerText(timeRemaining);
        if (isTimerRunning) {
            startTimer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        SharedPreferences.Editor editor = getSharedPreferences(TIMER_PREFS, MODE_PRIVATE).edit();
        Intent timerServiceIntent = new Intent(this, Timer.class);
        stopService(timerServiceIntent);
        editor.putLong(REMAINING_TIME_KEY, timeRemaining);
        editor.apply();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            isTimerRunning = false;
        }
    }
    public static boolean give_prize() {
        return give_prize;
    }
    public int getPoints() {
        return points;
    }

    public boolean getPause() {
        return info_pause;
    }
}



/////////////////////////////////////////////////////////////////////////////////////////////////////////////////





