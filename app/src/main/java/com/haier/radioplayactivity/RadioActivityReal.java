package com.haier.radioplayactivity;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harry.Kong on 2016/12/30.
 */

public class RadioActivityReal extends Activity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, View.OnClickListener {
    private ImageView imageView14;
    private TextView textView26;
    private LinearLayout lin_top;
    private ListView lv_radio;
    private ImageButton ibt_play_pravious;
    private ToggleButton tb_play_program;
    private ImageButton ibt_play_next;
    private SeekBar sb_play_progress_radio;
    private ImageView down;
    private SeekBar volume_seekbar;
    private RadioAdapter radioAdapter;
    private Button btn_text;
    private List<String> stringList = new ArrayList<>();
    private AudioManager audioMgr = null; // Audio管理器，用了控制音量
    private int maxVolume = 50; // 最大音量值
    private MediaPlayer mediaPlayer;
    private int location = 0;//正在播放第几个
    private int progress = 0;//加载进度
    private boolean isPlaying = false;//是否正在播放
    private boolean isPausing = false;//没有播是暂停还是未播放
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
    List<String> listName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio2);
        initView();
        audioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // 获取最大音乐音量
        maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        stringList = getFile();
        radioAdapter = new RadioAdapter(RadioActivityReal.this, stringList);
        lv_radio.setAdapter(radioAdapter);
        adjustVolume();
        // playRadio();
    }

    private void initView() {
        imageView14 = (ImageView) findViewById(R.id.imageView14);
        textView26 = (TextView) findViewById(R.id.textView26);
        lin_top = (LinearLayout) findViewById(R.id.lin_top);
        lv_radio = (ListView) findViewById(R.id.lv_radio);
        lv_radio.setOnItemClickListener(onItemClickListener);
        ibt_play_pravious = (ImageButton) findViewById(R.id.ibt_play_pravious);
        tb_play_program = (ToggleButton) findViewById(R.id.tb_play_radio_program);
        ibt_play_next = (ImageButton) findViewById(R.id.ibt_play_next);
        sb_play_progress_radio = (SeekBar) findViewById(R.id.sb_play_progress_radio);
        down = (ImageView) findViewById(R.id.down);
        volume_seekbar = (SeekBar) findViewById(R.id.volume_seekbar);

        ibt_play_pravious.setOnClickListener(this);
        tb_play_program.setOnClickListener(this);
        ibt_play_next.setOnClickListener(this);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnPreparedListener(this);
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
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Log.d("position--->", i + "位置");
            radioAdapter.setSeclection(i);
            radioAdapter.notifyDataSetChanged();
            location = i;
            playRadio();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibt_play_pravious:
                if (location > 0) {
                    location--;
                    radioAdapter.setSeclection(location);
                    radioAdapter.notifyDataSetChanged();
                    playRadio();
                }
                if (location == 10) {
//                    lv_radio.smoothScrollBy(lv_radio.getHeight(), 0);
//
//
//                    // lv_radio.scrollBy(0, 800);
//                    radioAdapter.notifyDataSetChanged();
                } else if (location == 7) {
                    int number = lv_radio.getChildCount();
                    Log.d("location--->", number + "个数");
                    lv_radio.setSelected(true);
                    setListViewPos(number + 1);
                    //   lv_radio.smoothScrollBy(-lv_radio.getHeight(), 0);
                    //  lv_radio.scrollBy(0, -800);
                    Log.d("location--->", lv_radio.getHeight() + "距离上");
                    lv_radio.smoothScrollToPosition(0, 3);

                }
                break;
            case R.id.tb_play_radio_program:
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

                break;
            case R.id.ibt_play_next:
                if (location < stringList.size() - 1) {
                    location++;
                    radioAdapter.setSeclection(location);
                    radioAdapter.notifyDataSetChanged();
                    playRadio();
                }
                if (location == 7) {
//                    lv_radio.scrollBy(0, 800);
//                    radioAdapter.notifyDataSetChanged();
//                    int number = lv_radio.getChildCount();
//                    Log.d("location--->", number + "个数");
//                    lv_radio.setSelected(true);
                    //  setListViewPos(number + 1);
                    Log.d("location--->", lv_radio.getHeight() + "距离下");

                   // lv_radio.smoothScrollBy(lv_radio.getHeight(), 3);
                    lv_radio.smoothScrollToPosition(10, 3);
                } else if (location == 0) {
                    lv_radio.scrollBy(0, -800);
                    radioAdapter.notifyDataSetChanged();

                }
                break;
        }
    }

    /**
     * 获取文件夹下面所有的符合要求的文件
     */
    public List<String> getFile() {
        List<String> stringList = new ArrayList<>();
        File file = new File("/mnt/sdcard/fm");//创建目录
        if (file.exists()) {
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
                stringList.add(g.getName());
                System.out.println(g.getName());
            }
        }


        return stringList;
    }


    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
        progress = percent;
        sb_play_progress_radio.setSecondaryProgress(percent);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        isPlaying = false;
        tb_play_program.setChecked(false);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        tb_play_program.setChecked(true);

    }

    private void playRadio() {
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
            // mediaPlayer = MediaPlayer.create(this, listRaw.get(location));
            Log.v("threadname", "地址" + (Environment.getExternalStorageDirectory().getPath() + File.separator + "sdcard/radio_one.mp3"));
            mediaPlayer.setDataSource("/mnt/sdcard/fm/" + stringList.get(location));
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

    private void startThread() {
        new Thread() {
            @Override
            public void run() {
                super.run();
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

    /**
     * 调整音量
     */
    private void adjustVolume() {
        audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, 0,
                AudioManager.FLAG_PLAY_SOUND);//调整音量时播放声音
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
        isPlaying = false;
        super.onDestroy();
    }

    private void setListViewPos(int pos) {
        if (android.os.Build.VERSION.SDK_INT >= 8) {
            lv_radio.smoothScrollToPosition(pos);
        } else {
            lv_radio.setSelection(pos);
        }
    }
}
