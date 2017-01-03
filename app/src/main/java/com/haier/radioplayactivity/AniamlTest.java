package com.haier.radioplayactivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Harry.Kong on 2016/12/30.
 */

public class AniamlTest extends Activity {
    Button btn_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animal);
        btn_text = (Button) findViewById(R.id.btn_text);
        Animation mAnimation = AnimationUtils.loadAnimation(this, R.anim.test);
        btn_text.startAnimation(mAnimation);
        btn_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AniamlTest.this, "测试", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        /**
         * 当hasFocus为true的时候，说明Activity的Window对象已经获取焦点，进而Activity界面已经加载绘制完成
         */
        if (hasFocus) {
            int widht = btn_text.getLeft();
            int height = btn_text.getTop();
            Log.i("kuandu--->", "onWindowFocusChanged width:" + widht + "   "
                    + "  height:" + height);
        }
    }
}
