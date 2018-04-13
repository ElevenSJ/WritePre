package com.easier.writepre.inputmessage;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.widget.Button;

import com.easier.writepre.R;
import com.easier.writepre.inputmessage.recoder.AudioMediaPlayer;
import com.easier.writepre.utils.ToastUtil;

/**
 * Created by zhoulu on 2016/10/26.
 */

public class Record {
    String url;
    int time;
    Button currentPlayBtn;
    Drawable drawable_bg;
    Drawable drawable_an;
    AnimationDrawable animationDrawable;//录音播放公共动画
    String tvlen = "            ";

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Record(Context context,String url, int time, Button view) {
        drawable_bg=context.getResources().getDrawable(R.drawable.rc_ic_voice_receive);
        drawable_an = context.getResources().getDrawable(R.drawable.rc_an_voice_receive);
        this.url = url;
        this.time = time;
        this.currentPlayBtn = view;
        animationDrawable = (AnimationDrawable) drawable_an;
//            int len=time/6;
//            for (int i=0;i<len;i++)
//            {
//                tvlen+="  ";
//            }
        view.setText(tvlen + time + "'' ");
    }

    public void playVoice() {
        startAnimation();
        AudioMediaPlayer.getInstance().play(url);
        AudioMediaPlayer.getInstance().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopVioce();
            }
        });
        AudioMediaPlayer.getInstance().setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                ToastUtil.show("语音播放失败!");
                stopVioce();
                return false;
            }
        });
    }

    public void startAnimation() {
        // 这一步必须要做,否则不会显示.
        drawable_an.setBounds(0, 0, drawable_an.getMinimumWidth(), drawable_an.getMinimumHeight());
        currentPlayBtn.setCompoundDrawables(drawable_an, null, null, null);
        animationDrawable.start();
    }

    public void stopAnimation() {
        animationDrawable.stop();
        /// 这一步必须要做,否则不会显示.
        drawable_bg.setBounds(0, 0, drawable_bg.getMinimumWidth(), drawable_bg.getMinimumHeight());
        currentPlayBtn.setCompoundDrawables(drawable_bg, null, null, null);
    }

    public void stopVioce() {
        stopAnimation();
        AudioMediaPlayer.getInstance().release();
    }
}
