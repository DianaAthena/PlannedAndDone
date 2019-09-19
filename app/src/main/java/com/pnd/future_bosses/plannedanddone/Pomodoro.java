package com.pnd.future_bosses.plannedanddone;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class Pomodoro extends AppCompatActivity {

    String ms, ss;
    int timeMin = 25;
    int timeMinR = 25; //varijabla u kojoj se cuva vrijeme za reset
    int timeSec = 0;
    CountDownTimer ti;
    boolean isPaused = false;
    long timeR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);
        setTimer();
        ImageView im = (ImageView) findViewById( R.id.imageView3 );
        setImage(im);
    }

    private void resetColor(){
        TextView m = (TextView)findViewById(R.id.minutes);
        TextView s = (TextView)findViewById(R.id.seconds);
        int c = getResources().getColor(R.color.greenPomodoro);
        ((TextView) findViewById( R.id.col )).setTextColor(c);
        m.setTextColor(c);
        s.setTextColor(c);
    }

    public void pomoLong(View view) {

        if(ti != null)
            ti.cancel();
        timeMinR=10; timeMin = 10;
        isPaused = false;
        resetColor();
        setTimer();
        ImageView im = (ImageView) findViewById( R.id.imageView3 );
        setImage(im);
    }

    public void pomoShort(View view) {

        if(ti != null)
            ti.cancel();
        timeMinR = 5;
        timeMin = 5;
        isPaused = false;
        resetColor();
        setTimer();
        ImageView im = (ImageView) findViewById( R.id.imageView3 );
        setImage(im);
    }

    public void pomoWork(View view) {

        if(ti != null)
            ti.cancel();
        timeMinR = 25;
        timeMin = 25;
        isPaused = false;
        resetColor();
        setTimer();
        ImageView im = (ImageView) findViewById( R.id.imageView3 );
        setImage(im);
    }

    private void setTimer(){
        TextView min = (TextView)findViewById(R.id.minutes);
        TextView sec = (TextView)findViewById(R.id.seconds);
        String mins, secs;

        if(timeMin<10){
            //nalijepi 0 na pocetak
            mins = "0"+timeMin;
        }
        else mins = Integer.toString(timeMin);
        int timeMin = 0;
        if(timeSec<10){
            //nalijepi 0 na pocetak
            secs = "0"+timeSec;
        }
        else secs = Integer.toString(timeSec);

        min.setText(mins);
        sec.setText(secs);

    }

    public void resetTimer(View view) {
        //postavi timer na prije odabranu vrijednost

        if(ti != null)
            ti.cancel();
        setTimer();
        isPaused = false;
        resetColor();
        timeMin = timeMinR;

    }

    public void pauseTimer(View view) {
        //zaustavi timer
        if(ti != null) {
            ti.cancel();
            isPaused = true;
        }
    }

    private void setImage(ImageView im){

        if(timeMin == 25){
            //crtaj popodora
            im.setBackgroundResource(R.drawable.pomidor1);
        }
        else{
            //crtaj potato
            im.setBackgroundResource(R.drawable.potato);
        }
    }
    public void stratTimer(View view) {


        //provjeri je li timer pauziran i ako je pokreni ga s perthodnim vremenom
        if(isPaused == true){
            isPaused = false;

        }
        else{
            timeR = timeMinR * 60000;
            ImageView im = (ImageView) findViewById( R.id.imageView3 );
            setImage(im);
        }


        //inace pokreni timer od zadanog vremena
        final TextView m = (TextView) findViewById( R.id.minutes );
        final TextView s = (TextView) findViewById( R.id.seconds );
        resetColor();

        if(ti != null)
            ti.cancel();

        ti = new CountDownTimer(timeR, 1000) {

            public void onTick(long millisUntilFinished) {
                timeR = millisUntilFinished;
                if(TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished)<10){
                    //nalijepi 0 na pocetak
                    ms = "0"+(TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished));
                }
                else ms = Long.toString(TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished));

                if(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))<10){
                    //nalijepi 0 na pocetak
                    ss = "0"+(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                }
                else ss = Long.toString(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

                m.setText(ms);
                s.setText(ss);

            }

            public void onFinish() {
                ((TextView) findViewById( R.id.col )).setTextColor(Color.RED);
                m.setTextColor(Color.RED);
                s.setTextColor(Color.RED);
                ImageView image = new ImageView(Pomodoro.this);
                setImage(image);

                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(60,60);
                image.setLayoutParams(params);
                image.setAdjustViewBounds(true);
                ((LinearLayout) findViewById( R.id.linearLayout7 )).addView(image);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
            }
        }.start();

    }
}
