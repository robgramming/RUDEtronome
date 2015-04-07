package com.robgramming.rudetronome.rudetronome_v1;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener {
    /*
        Declare on-screen variables.
    */
    ImageButton imageButtonPlay;
    SeekBar seekBarStartBPM;
    EditText editTextStartBPM;
    SeekBar seekBarPeakBPM;
    EditText editTextPeakBPM;
    SeekBar seekBarDuration;
    EditText editTextDuration;
    TextView textViewElapsedTime;
    public static TextView textViewCurrentBPM;
    /*
        Declare RUDEtronome integers.
     */
    int startBPM = 80;
    int peakBPM = 80;
    int duration = 4;
    /*
        Declare RUDEtronome booleans.
     */
    boolean playing = false;
    /*
        RUDEtronomeAsyncTask is the method which initiates RUDEtronome when Play Button is pressed.
        rudeTask is the short name that references it.
     */
    private RUDEtronomeAsyncTask rudeTask;
    /*
        Declares a variable for MediaPlayer.
     */
    public static MediaPlayer mediaPlayerPeaked;
    /*
        The clock displaying time elapsed is handled here.
     */
    long startTime = 0;
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            textViewElapsedTime.setText(String.format("%d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 1000);
        }
    };
    /*
        The app loads here at onCreate.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
            Setup on-screen objects.
         */
        imageButtonPlay = (ImageButton)findViewById(R.id.imageButtonPlay);
        seekBarStartBPM = (SeekBar)findViewById(R.id.seekBarStartBPM);
        editTextStartBPM = (EditText)findViewById(R.id.editTextStartBPM);
        seekBarPeakBPM = (SeekBar)findViewById(R.id.seekBarPeakBPM);
        editTextPeakBPM = (EditText)findViewById(R.id.editTextPeakBPM);
        seekBarDuration = (SeekBar)findViewById(R.id.seekBarDuration);
        editTextDuration = (EditText)findViewById(R.id.editTextDuration);
        textViewElapsedTime = (TextView)findViewById(R.id.textViewTimeElapsedIndicated);
        textViewCurrentBPM = (TextView)findViewById(R.id.textViewCurrentBPMIndicated);
        /*
           Listen for changes to on-screen adjustables (EditTexts, SeekBars & ImageButton)
         */
        seekBarStartBPM.setOnSeekBarChangeListener(this);
        seekBarPeakBPM.setOnSeekBarChangeListener(this);
        seekBarDuration.setOnSeekBarChangeListener(this);
        editTextStartBPM.addTextChangedListener(generalTextWatcher);
        editTextPeakBPM.addTextChangedListener(generalTextWatcher);
        editTextDuration.addTextChangedListener(generalTextWatcher);
        imageButtonPlay.setOnClickListener(imgButtonHandler);
        /*
            RUDEtronomeAsyncTask is the method which initiates RUDEtronome when Play Button is pressed.
            rudeTask is the short name that references it.
         */
        rudeTask = new RUDEtronomeAsyncTask();
        /*
            Creates instance of MediaPlayer to play the peak tone when peakBPM is achieved.
         */
        mediaPlayerPeaked = MediaPlayer.create(this, R.raw.peak);
    }
    @Override
     public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        playing = false;
        rudeTask.stop();
        rudeTask = new RUDEtronomeAsyncTask();
        imageButtonPlay.setImageResource(android.R.drawable.ic_media_play);
    }
    /*
        When the Image Button is pressed to Play or Pause RUDEtronome.
     */
    View.OnClickListener imgButtonHandler = new View.OnClickListener() {
        public void onClick(View v) {
            /*
                PLAY.
             */
            if (!playing) {
                playing = true;
                textViewCurrentBPM.setText(""+startBPM);
                startTime = System.currentTimeMillis();
                timerHandler.postDelayed(timerRunnable, 0);
                rudeTask.execute();
                imageButtonPlay.setImageResource(android.R.drawable.ic_media_pause);
            /*
                STOP.
             */
            } else if (playing) {
                playing = false;
                rudeTask.stop();
                rudeTask = new RUDEtronomeAsyncTask();
                imageButtonPlay.setImageResource(android.R.drawable.ic_media_play);
                timerHandler.removeCallbacks(timerRunnable);
                // A good time to run the Garbage Collector:
                Runtime.getRuntime().gc();
            }
        }
    };
    /*
        Declared above as rudeTask, this starts RUDEtronome when the Play Button is pressed.
     */
    private class RUDEtronomeAsyncTask extends AsyncTask<Void,Void,String> {
        RUDEtronome rudetronome;
        /*
            RUDEtronome is created here which in turn initialises the AudioGenerator to create the
            beeps and silence in RUDEtronome.
         */
        RUDEtronomeAsyncTask() {
            rudetronome = new RUDEtronome();
        }
        /*
            Passes RUDEtronome's variables to the audio to create beeps at the correct BPM.
            Plays RUDEtronome.
         */
        protected String doInBackground(Void... params) {
            rudetronome.setBpm(startBPM, peakBPM, duration);
            rudetronome.play();
            return null;
        }
        /*
            Stops RUDEtronome.
         */
        public void stop() {
            rudetronome.stop();
            rudetronome = null;
        }
    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
    /*
        When the Seek Bars (sliders) are adjusted the Edit Text boxes are updated with the values.
        The RUDEtronome variables are set here:
            startBPM
            peakBPM
            duration
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == seekBarStartBPM) {
            startBPM = progress+40;
            editTextStartBPM.setText(""+startBPM);
            /*
                This prevents startBPM from being set higher than peakBPM.
             */
            if (seekBarStartBPM.getProgress() > seekBarPeakBPM.getProgress()) {
                seekBarPeakBPM.setProgress(seekBarStartBPM.getProgress());
            }
        }
        if (seekBar == seekBarPeakBPM) {
            peakBPM = progress+40;
            editTextPeakBPM.setText(""+peakBPM);
            /*
                This prevents peakBPM from being set lower than startBPM.
             */
            if (seekBarPeakBPM.getProgress() < seekBarStartBPM.getProgress()) {
                seekBarStartBPM.setProgress(seekBarPeakBPM.getProgress());
            }
        }
        if (seekBar == seekBarDuration) {
            duration = progress+1;
            editTextDuration.setText(""+duration);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private TextWatcher generalTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
        /*
            When the Edit Texts are updated the Seek Bars (sliders) are set to the new values.
            The RUDEtronome variables are set here:
                startBPM
                peakBPM
                duration
         */
        @Override
        public void afterTextChanged(Editable s) {
            if (!s.toString().isEmpty()) {
                int i = Integer.parseInt(s.toString());
                if (editTextStartBPM.getText().hashCode() == s.hashCode()) {
                    if (i >= 40 && i <= 240) {
                        startBPM = i;
                        seekBarStartBPM.setProgress(i - 40);
                    }
                }
                if (editTextPeakBPM.getText().hashCode() == s.hashCode()) {
                    if (i >= 40 && i <= 240) {
                        peakBPM = i;
                        seekBarPeakBPM.setProgress(i - 40);
                    }
                }
                if (editTextDuration.getText().hashCode() == s.hashCode()) {
                    if (i >= 1 && i <= 16) {
                        duration = i;
                        seekBarDuration.setProgress(i - 1);
                    }
                }
            }
        }
    };
}
