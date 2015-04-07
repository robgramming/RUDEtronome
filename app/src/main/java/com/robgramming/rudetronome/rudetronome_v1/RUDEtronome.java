package com.robgramming.rudetronome.rudetronome_v1;

import android.os.Handler;

public class RUDEtronome {
    /*
        Declare RUDEtronome's doubles
     */
    private double beatSound = 440;
    private double bpm;
    private double differenceBPM;
    private double incrementBPMtime;
    private double[] silenceSoundArray;
    private double[] soundClickArray;
    private double stepBPM;
    /*
        Declare RUDEtonome's integers
     */
    private int duration;
    private int newBPM;
    private int peakBPM;
    private int silence;
    private int startBPM;
    private final int tick = 500;
    /*
        Declare RUDEtronome's booleans
     */
    private boolean peaked = false;
    private boolean play = true;
    /*
        Initialise audioGenerator (creates the beeps).
     */
    private AudioGenerator audioGenerator = new AudioGenerator(8000);
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            // System.out.println("bpm = "+bpm+", newBPM = "+newBPM+", peakBPM = "+peakBPM+", startBPM ="+startBPM);
            if (!peaked)
                newBPM++;
            else
                newBPM--;
            if (newBPM >= peakBPM) {
                peaked = true;
                MainActivity.mediaPlayerPeaked.start();
            }
            if (newBPM == startBPM) {
                MainActivity.mediaPlayerPeaked.start();
            }
            bpm = newBPM;
            MainActivity.textViewCurrentBPM.setText(""+newBPM);
            calcSilence();
            if (newBPM <= startBPM) {
                timerHandler.removeCallbacks(timerRunnable);
            } else {
                timerHandler.postDelayed(this, (long) stepBPM);
            }
        }
    };
    public RUDEtronome() {
        audioGenerator.createPlayer();
    }
    /*
        calcSilence will build the audio so that the beeps are followed by the correct duration of
        silence to match the current BPM.
     */
    public void calcSilence() {
        silence = (int) (((60/bpm)*8000)-tick); // (((60/80)*8000)-1000 = 5000
        soundClickArray = new double[this.tick];
        silenceSoundArray = new double[this.silence];
        double[] tick = audioGenerator.getSineWave(this.tick, 8000, beatSound);
        for(int i=0;i<this.tick;i++) {
            soundClickArray[i] = tick[i];
        }
        for (int i = 0; i < silence; i++) silenceSoundArray[i] = 0;
    }
    /*
        The play method is called when the play button is pressed.
     */
    public void play() {
        calcSilence();
        incrementBPMtime = (double) duration /2;
        differenceBPM = peakBPM-startBPM;
        if (differenceBPM == 0)
            stepBPM = incrementBPMtime*1000;
        else
            stepBPM = (incrementBPMtime/differenceBPM)*1000;
        newBPM = startBPM;
        timerHandler.postDelayed(timerRunnable, (long) stepBPM);
        do {
            audioGenerator.writeSound(soundClickArray);
            audioGenerator.writeSound(silenceSoundArray);
        } while(play);
    }
    /*
        The stop method is called when the play button is pressed while RUDEtronome is playing.
     */
    public void stop() {
        play = false;
        audioGenerator.destroyAudioTrack();
        timerHandler.removeCallbacks(timerRunnable);
        MainActivity.textViewCurrentBPM.setText("0");
    }
    /*
        Variables are parsed here which are set in the UI.
     */
    public void setBpm(int startBPM, int peakBPM, int duration) {
        this.bpm = startBPM;
        this.startBPM = startBPM;
        this.peakBPM = peakBPM;
        this.duration = duration*60;
    }
}
