package com.haier.radioplayactivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Creat by Harry.Kong
 */
public class RadioActivity extends Activity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, View.OnClickListener {
    private List<String> listRaw;
    private TextView tv_radio_name0, tv_radio_name1, tv_radio_name2, tv_radio_name3, tv_radio_name4;
    private SeekBar sb_play_progress_radio;
    protected ToggleButton tb_play_program;
    private int location = 0;//正在播放第几个
    MediaPlayer mediaPlayer;
    private int progress = 0;//加载进度
    private boolean isPlaying = false;//是否正在播放
    private boolean isPausing = false;//没有播是暂停还是未播放
    PowerManager.WakeLock wl;//create用于设置屏幕常量，
    private AudioManager audioMgr = null; // Audio管理器，用了控制音量
    private int maxVolume = 50; // 最大音量值
    private int curVolume = 20; // 当前音量值
    private int stepVolume = 0; // 每次调整的音量幅度
    private SeekBar volume_seekbar;
    private LinearLayout lin_radioplay5;
    private LinearLayout lin_radioplay6;
    private LinearLayout lin_radioplay7;
    private LinearLayout lin_radioplay8;
    private LinearLayout lin_radioplay9;
    private LinearLayout lin_radioplay10;
    private TextView tv_radio_name5;
    private TextView tv_radio_name6;
    private TextView tv_radio_name7;
    private TextView tv_radio_name8;
    private TextView tv_radio_name9;
    private TextView tv_radio_name10;
    private ScrollView sl_radio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_radio);
        listRaw = new ArrayList<>();
        listRaw.add("/mnt/sdcard/fm/radio_one.mp3");
        listRaw.add("/mnt/sdcard/fm/radio_two.mp3");
        listRaw.add("/mnt/sdcard/fm/radio_three.mp3");
        listRaw.add("/mnt/sdcard/fm/radio_four.mp3");
        listRaw.add("/mnt/sdcard/fm/radio_five.mp3");
        listRaw.add("/mnt/sdcard/fm/radio_six.mp3");
        listRaw.add("/mnt/sdcard/fm/radio_seven.mp3");
        listRaw.add("/mnt/sdcard/fm/radio_eight.mp3");
        listRaw.add("/mnt/sdcard/fm/radio_nine.mp3");
        listRaw.add("/mnt/sdcard/fm/radio_ten.mp3");
        listRaw.add("/mnt/sdcard/fm/radio_eleven.mp3");
        File file = new File("/mnt/sdcard/fm");//创建目录
        File[] f = file.listFiles(new FileFilter() {//列出目录下的符合条件的文件
            @Override
            public boolean accept(File ff) {//重写accept方法
                if (ff.getName().endsWith(".mp3")) {//name以.java结尾的为符合条件，将被筛出
                    return true;

                } else {
                    return false;
                }
            }

        });
        for (File g : f) {//输出筛出的文件
            System.out.println(g.getName());
        }
        Thread thread = Thread.currentThread();
        String name = thread.getName();
        initView();
        //设置屏幕常量
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "MyTag");
//在释放之前，屏幕一直亮着（SCREEN_DIM_WAKE_LOCK有可能会变暗,但是还可以看到屏幕内容,PowerManager.SCREEN_BRIGHT_WAKE_LOCK不会变暗）
//        wl.acquire();
        //  getRadioUrl();
        audioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // 获取最大音乐音量
        maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 初始化音量大概为最大音量的1/2
        curVolume = maxVolume / 2;
        // 每次调整的音量大概为最大音量的1/6
        stepVolume = maxVolume / 6;
        adjustVolume();
        location = 0;
        playRadio();

    }

    /**
     * 调整音量
     */
    private void adjustVolume() {
        audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume,
                AudioManager.FLAG_PLAY_SOUND);//调整音量时播放声音
    }

    private void initView() {
        tb_play_program = (ToggleButton) findViewById(R.id.tb_play_radio_program);
        tb_play_program.setOnClickListener(this);
        tb_play_program.setChecked(false);
        sb_play_progress_radio = (SeekBar) findViewById(R.id.sb_play_progress_radio);
        tv_radio_name1 = (TextView) findViewById(R.id.tv_radio_name1);
        tv_radio_name2 = (TextView) findViewById(R.id.tv_radio_name2);
        tv_radio_name3 = (TextView) findViewById(R.id.tv_radio_name3);
        tv_radio_name4 = (TextView) findViewById(R.id.tv_radio_name4);
        tv_radio_name0 = (TextView) findViewById(R.id.tv_radio_name0);
        findViewById(R.id.lin_radioplay1).setOnClickListener(this);
        findViewById(R.id.lin_radioplay2).setOnClickListener(this);
        findViewById(R.id.lin_radioplay3).setOnClickListener(this);
        findViewById(R.id.lin_radioplay4).setOnClickListener(this);
        findViewById(R.id.lin_radioplay0).setOnClickListener(this);
        findViewById(R.id.ibt_play_next).setOnClickListener(this);
        findViewById(R.id.ibt_play_pravious).setOnClickListener(this);
        //findViewById(R.id.imbt_radioPlay_finish).setOnClickListener(this);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnPreparedListener(this);
        volume_seekbar = (SeekBar) findViewById(R.id.volume_seekbar);
        volume_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, i,
                        AudioManager.FLAG_PLAY_SOUND);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb_play_progress_radio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress() * mediaPlayer.getDuration() / 100);
            }
        });


        lin_radioplay5 = (LinearLayout) findViewById(R.id.lin_radioplay5);
        lin_radioplay5.setOnClickListener(this);
        lin_radioplay6 = (LinearLayout) findViewById(R.id.lin_radioplay6);
        lin_radioplay6.setOnClickListener(this);
        lin_radioplay7 = (LinearLayout) findViewById(R.id.lin_radioplay7);
        lin_radioplay7.setOnClickListener(this);
        lin_radioplay8 = (LinearLayout) findViewById(R.id.lin_radioplay8);
        lin_radioplay8.setOnClickListener(this);
        lin_radioplay9 = (LinearLayout) findViewById(R.id.lin_radioplay9);
        lin_radioplay9.setOnClickListener(this);
        lin_radioplay10 = (LinearLayout) findViewById(R.id.lin_radioplay10);
        lin_radioplay10.setOnClickListener(this);
        tv_radio_name5 = (TextView) findViewById(R.id.tv_radio_name5);
        tv_radio_name5.setOnClickListener(this);
        tv_radio_name6 = (TextView) findViewById(R.id.tv_radio_name6);
        tv_radio_name6.setOnClickListener(this);
        tv_radio_name7 = (TextView) findViewById(R.id.tv_radio_name7);
        tv_radio_name7.setOnClickListener(this);
        tv_radio_name8 = (TextView) findViewById(R.id.tv_radio_name8);
        tv_radio_name8.setOnClickListener(this);
        tv_radio_name9 = (TextView) findViewById(R.id.tv_radio_name9);
        tv_radio_name9.setOnClickListener(this);
        tv_radio_name10 = (TextView) findViewById(R.id.tv_radio_name10);
        tv_radio_name10.setOnClickListener(this);
        sl_radio = (ScrollView) findViewById(R.id.sl_radio);
        sl_radio.setOnClickListener(this);
    }

    private void playNext() {
        if (location == 10) {
            location = 0;
        } else {
            location++;
        }
        if (location == 5) {
            sl_radio.scrollBy(0, 800);
        } else if (location == 0) {
            sl_radio.scrollBy(0, -800);
        }
        playRadio();
    }

    private void playPrivous() {
        if (location == 0) {
            location = 10;
        } else {
            location--;
        }
        if (location == 10) {
            sl_radio.scrollBy(0, 800);
        } else if (location == 5) {
            sl_radio.scrollBy(0, -800);
        }
        playRadio();
    }

    private void playRadioPause() {
        progress = 0;
        if (null != mediaPlayer) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                tb_play_program.setChecked(true);
                isPlaying = true;
                isPausing = false;
                startThread();
            } else {
                playRadio();
            }
        } else {
            playRadio();
        }


    }

    private void playRadio() {
        setRadiaNameColotr();
        isPausing = false;
        progress = 0;
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            } else {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            }

            mediaPlayer.reset();//重置参数
            /**
             * 调用setDataSource();方法，并传入想要播放的音频文件的HTTP位置。
             */
            Log.v("threadname", "地址" + (listRaw.get(0)));
            // mediaPlayer = MediaPlayer.create(this, listRaw.get(location));
            Log.v("threadname", "地址" + (Environment.getExternalStorageDirectory().getPath() + File.separator + "sdcard/radio_one.mp3"));
            mediaPlayer.setDataSource(listRaw.get(location));
            sb_play_progress_radio.setEnabled(true);
            mediaPlayer.prepare();//加载必要的音频资源
            mediaPlayer.start();
            tb_play_program.setChecked(true);
            isPlaying = true;
            startThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                if (sb_play_progress_radio != null) {
                    sb_play_progress_radio.setProgress((msg.arg2 * 100 / mediaPlayer.getDuration()));

                }
            }
            super.handleMessage(msg);
        }
    };

    private void startThread() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Log.v("threadname", "" + isPlaying);
                while (isPlaying) {//启动线程刷新进度条
                    Message me = new Message();
                    me.arg1 = 1;
                    me.arg2 = mediaPlayer.getCurrentPosition();
                    myHandler.sendMessage(me);
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException ex) {

                    }

                }
            }
        }.start();
    }


    private void setRadiaNameColotr() {
        tv_radio_name0.setTextColor(Color.parseColor("#d5d5d5"));
        tv_radio_name1.setTextColor(Color.parseColor("#d5d5d5"));
        tv_radio_name2.setTextColor(Color.parseColor("#d5d5d5"));
        tv_radio_name3.setTextColor(Color.parseColor("#d5d5d5"));
        tv_radio_name4.setTextColor(Color.parseColor("#d5d5d5"));
        tv_radio_name5.setTextColor(Color.parseColor("#d5d5d5"));
        tv_radio_name6.setTextColor(Color.parseColor("#d5d5d5"));
        tv_radio_name7.setTextColor(Color.parseColor("#d5d5d5"));
        tv_radio_name8.setTextColor(Color.parseColor("#d5d5d5"));
        tv_radio_name9.setTextColor(Color.parseColor("#d5d5d5"));
        tv_radio_name10.setTextColor(Color.parseColor("#d5d5d5"));
        if (location == 0) {
            tv_radio_name0.setTextColor(Color.parseColor("#4eb7ff"));
        } else if (location == 1) {
            tv_radio_name1.setTextColor(Color.parseColor("#4eb7ff"));
        } else if (location == 2) {
            tv_radio_name2.setTextColor(Color.parseColor("#4eb7ff"));
        } else if (location == 3) {
            tv_radio_name3.setTextColor(Color.parseColor("#4eb7ff"));
        } else if (location == 4) {
            tv_radio_name4.setTextColor(Color.parseColor("#4eb7ff"));
        } else if (location == 5) {
            tv_radio_name5.setTextColor(Color.parseColor("#4eb7ff"));
        } else if (location == 6) {
            tv_radio_name6.setTextColor(Color.parseColor("#4eb7ff"));
        } else if (location == 7) {
            tv_radio_name7.setTextColor(Color.parseColor("#4eb7ff"));
        } else if (location == 8) {
            tv_radio_name8.setTextColor(Color.parseColor("#4eb7ff"));
        } else if (location == 9) {
            tv_radio_name9.setTextColor(Color.parseColor("#4eb7ff"));
        } else if (location == 10) {
            tv_radio_name10.setTextColor(Color.parseColor("#4eb7ff"));
        }
    }

    /**
     * 当MediaPlayer正在缓冲时，将调用该Activity的onBufferingUpdate方法。
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        progress = percent;
        sb_play_progress_radio.setSecondaryProgress(percent);
    }

    /**
     * 当完成prepareAsync方法时，将调用onPrepared方法，表明音频准备播放。
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
//        sb_play_progress_radio.setEnabled(true);
//        mp.start();
        tb_play_program.setChecked(true);
//        isPlaying = true;
//        startThread();
    }

    /**
     * 当MediaPlayer完成播放音频文件时，将调用onCompletion方法。
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        isPlaying = false;
        tb_play_program.setChecked(false);
    }

    /**
     * 当按手机上的返回键的时候，会自动调用系统的onKeyDown方法，而onKeyDown方法又会调用onDestroy()方法销毁该Activity
     * ， 此时如果onDestroy()方法不重写，那么正在播放的音乐是不会停止的（大家可以试一下），所以这时候要重写onDestroy()方法，
     * 在该方法中 加入mediaPlayer.stop()方法，表示按返回键的时候，会调用mediaPlayer对象的stop方法，从而停止音乐的播放。
     */
    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
        isPlaying = false;
        if (wl != null) {
//            wl.release();
        }
        super.onDestroy();
    }

    protected void onPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
//        isUpadteProgress = false;
        isPlaying = false;
        super.onPause();
    }

    /**
     * 如果MediaPlayer出现错误，将调用onError方法。
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
//                statusTextView
//                        .setText("MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK"
//                                + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
//                statusTextView.setText("MEDIA_ERROR_SERVER_DIED" + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
//                statusTextView.setText("MEDIA_ERROR_UNKNOWN");
                break;
            default:
                break;
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tb_play_radio_program) {
            if (tb_play_program.isChecked()) {
                if (isPausing) {
                    playRadioPause();
                } else {
                    playRadio();
                }
                isPlaying = true;
                isPausing = false;

            } else {
                if (null != mediaPlayer) {
                    mediaPlayer.pause();
                    isPlaying = false;
                    isPausing = true;
                }
            }
        } else if (v.getId() == R.id.lin_radioplay0) {
            location = 0;
            playRadio();
        } else if (v.getId() == R.id.lin_radioplay1) {
            location = 1;
            playRadio();
        } else if (v.getId() == R.id.lin_radioplay2) {
            location = 2;
            playRadio();
        } else if (v.getId() == R.id.lin_radioplay3) {
            location = 3;
            playRadio();
        } else if (v.getId() == R.id.lin_radioplay4) {
            location = 4;
            playRadio();
        } else if (v.getId() == R.id.lin_radioplay5) {
            location = 5;
            playRadio();
        } else if (v.getId() == R.id.lin_radioplay6) {
            location = 6;
            playRadio();
        } else if (v.getId() == R.id.lin_radioplay7) {
            location = 7;
            playRadio();
        } else if (v.getId() == R.id.lin_radioplay8) {
            location = 8;
            playRadio();
        } else if (v.getId() == R.id.lin_radioplay9) {
            location = 9;
            playRadio();
        } else if (v.getId() == R.id.lin_radioplay10) {
            location = 10;
            playRadio();
        } else if (v.getId() == R.id.ibt_play_pravious) {
            playPrivous();
        } else if (v.getId() == R.id.ibt_play_next) {
            playNext();
        }
    }

}
