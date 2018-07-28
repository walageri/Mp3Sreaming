package com.blogspot.programer27android.mp3stream;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.concurrent.TimeUnit;
import dyanamitechetan.vusikview.VusikView;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnBufferingUpdateListener,MediaPlayer.OnCompletionListener{
    private ImageView btnplaypause;
    private SeekBar seekBar;
    private TextView tv;

    private VusikView musicView;

    private MediaPlayer mediaPlayer;
    private int mediaFileLength;
    private int realTimeLength;
    final Handler handler=new Handler();
    String link1 ="http://mic.duytan.edu.vn:86/ncs.mp3", link2="https://goo.gl/qbFUor",
            link3="https://guruinovatif-my.sharepoint.com/personal/geri_guruinovatif_net/Documents/kajian_dan_murotal/murotal_persurat/Hafs/Fahad%20Al%20kandari/001.mp3";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicView=findViewById(R.id.MV);

        seekBar=findViewById(R.id.seek);
        seekBar.setMax(99); //100%(0-99)
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(mediaPlayer.isPlaying()){
                    SeekBar seekBar =(SeekBar)view;
                    int playposition = (mediaFileLength/100)*seekBar.getProgress();
                    mediaPlayer.seekTo(playposition);
                }
                return false;
            }
        });
        tv=findViewById(R.id.timer);
        btnplaypause =findViewById(R.id.play_pause);
        btnplaypause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
                AsyncTask<String,String,String> mp3play = new AsyncTask<String, String, String>() {
                    @Override
                    protected void onPreExecute() {
                        mDialog.setMessage("Mohon Tunggu");
                        mDialog.show();
                    }
                    @Override
                    protected String doInBackground(String... strings) {
                        try {
                            mediaPlayer.setDataSource(strings[0]);
                            mediaPlayer.prepare();
                        } catch (Exception e) {
                        }
                        return "";
                    }
                    @Override
                    protected void onPostExecute(String s) {
                        mediaFileLength=mediaPlayer.getDuration();
                        realTimeLength =mediaFileLength;
                        if (!mediaPlayer.isPlaying()){
                            mediaPlayer.start();
                            btnplaypause.setImageResource(R.drawable.pause);
                        }else{
                            mediaPlayer.pause();
                            btnplaypause.setImageResource(R.drawable.play);
                        }
                        updateSeekBar();
                        mDialog.dismiss();
                    }
                };
                mp3play.execute(link1);
                musicView.start();
            }
        });

        mediaPlayer=new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }
    private  void updateSeekBar(){
        seekBar.setProgress((int)(((float)mediaPlayer.getCurrentPosition()/mediaFileLength)*100));
        if (mediaPlayer.isPlaying()){
            Runnable update=new Runnable() {
                @Override
                public void run() {
                    updateSeekBar();
                    realTimeLength -= 1000;
                    tv.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(realTimeLength),
                            TimeUnit.MILLISECONDS.toSeconds(realTimeLength) -
                                    TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(realTimeLength))
                    ));
                }
            };
            handler.postDelayed(update,1000);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        seekBar.setSecondaryProgress(i);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        btnplaypause.setImageResource(R.drawable.play);
        musicView.stopNotesFall();
    }
}
