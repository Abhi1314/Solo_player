package com.example.solo_player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import org.w3c.dom.Text;

import java.io.File;
import java.security.Key;
import java.util.ArrayList;

public class Player_Activity extends AppCompatActivity {
Button btnplay,btnnext,btnpre,btnff,btnfr,btnrepeat;
TextView txtsongname,txtstart,txtstop;
SeekBar seekmusic;
BarVisualizer visualizer;
String sngname;
ImageView imageView;
public static Boolean repeatflag=false;
public static final String Extra_Name="Song_name";
static MediaPlayer mediaPlayer;
ArrayList<File> mysongs;
Thread updateseekbar;
int position;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if(visualizer!=null){
            visualizer.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_);

        getSupportActionBar().setTitle("Now Playing..");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        btnplay=findViewById(R.id.play);
        btnnext=findViewById(R.id.next);
        btnff=findViewById(R.id.nextff);
        btnfr=findViewById(R.id.rewind);
        btnrepeat=findViewById(R.id.repeat);
        imageView=findViewById(R.id.imageview);
        btnpre=findViewById(R.id.previous);
        txtsongname=findViewById(R.id.txsong_name);
        txtstart=findViewById(R.id.txtsstart);
        txtstop=findViewById(R.id.txtsstop);
        seekmusic=findViewById(R.id.seekbar);
        visualizer=findViewById(R.id.bar);

        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Intent i=getIntent();
        Bundle bundle=i.getExtras();
        mysongs=(ArrayList)bundle.getParcelableArrayList("songs");
        String Songname=i.getStringExtra("songname");
        position=bundle.getInt("position",0);
        txtsongname.setSelected(true);
        Uri uri=Uri.parse(mysongs.get(position).toString());
        sngname=mysongs.get(position).getName();
        txtsongname.setText(sngname);
        mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();
        updateseekbar= new Thread(){
            @Override
            public void run() {
                int Totalduration=mediaPlayer.getDuration();
                int currentposition=0;
                while (currentposition<Totalduration){
                    try {
                        sleep(500);
                        currentposition=mediaPlayer.getCurrentPosition();
                        seekmusic.setProgress(currentposition);
                    }catch (InterruptedException | IllegalStateException e){
                       e.printStackTrace();
                    }
                }
            }
        };
        seekmusic.setMax(mediaPlayer.getDuration());
        updateseekbar.start();
        seekmusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.av_green), PorterDuff.Mode.MULTIPLY);
        seekmusic.getThumb().setColorFilter(getResources().getColor(R.color.av_green),PorterDuff.Mode.SRC_IN);
        seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        String endtime=creatTime(mediaPlayer.getDuration());
        txtstop.setText(endtime);
        final Handler handler=new Handler();
        final int delay=1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currenttime=creatTime(mediaPlayer.getCurrentPosition());
                txtstart.setText(currenttime);
                handler.postDelayed(this,delay);

            }
        },delay);

        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    btnplay.setBackgroundResource(R.drawable.playarrow);
                    mediaPlayer.pause();
                }else {
                    btnplay.setBackgroundResource(R.drawable.pause);
                    mediaPlayer.start();
                }

            }
        });
        //next song
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnnext.performClick();
            }
        });
        int audiosessionID=mediaPlayer.getAudioSessionId();
        if(audiosessionID!=-1){
            visualizer.setAudioSessionId(audiosessionID);
        }
        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=((position+1)%mysongs.size());
                Uri uri1=Uri.parse(mysongs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),uri1);
                sngname=mysongs.get(position).getName();
                txtsongname.setText(sngname);
                mediaPlayer.start();
                btnplay.setBackgroundResource(R.drawable.pause);
                StartAnimation(imageView);
                int audiosessionID=mediaPlayer.getAudioSessionId();
                if(audiosessionID!=-1){
                    visualizer.setAudioSessionId(audiosessionID);
                }
            }
        });
        btnpre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=((position-1)<0)?(mysongs.size()-1):(position-1);
                Uri uri2=Uri.parse(mysongs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),uri2);
                sngname=mysongs.get(position).getName();
                txtsongname.setText(sngname);
                mediaPlayer.start();
                btnplay.setBackgroundResource(R.drawable.pause);
                StartAnimation(imageView);
                int audiosessionID=mediaPlayer.getAudioSessionId();
                if(audiosessionID!=-1){
                    visualizer.setAudioSessionId(audiosessionID);
                }
            }
        });
        btnff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);

                }
            }
        });
        btnfr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }

            }
        });
        btnrepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatsong();
            }
        });

    }
    public void repeatsong(){
        if (repeatflag){

            btnrepeat.setBackgroundResource(R.drawable.repeat_24);
        }else {

           btnrepeat.setBackgroundResource(R.drawable.repeat_on);
        }
        repeatflag=!repeatflag;

    }
    public void StartAnimation(View view){
        ObjectAnimator animator=ObjectAnimator.ofFloat(imageView,"rotation",0f,360f);
        animator.setDuration(2000);
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

    public String creatTime(int duration)
    {
        String time="";
        int minute=duration/1000/60;
        int sec=duration/1000%60;
        time+=minute+":";
        if(sec<10)
        {
            time+="0";
        }
        time+=sec;
        return time;

    }
}