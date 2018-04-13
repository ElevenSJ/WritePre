package com.easier.writepre.rongyun;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;

import com.easier.writepre.R;
import com.easier.writepre.video.VideoRecordActivity;

import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;

/**
 * 小视频
 * Created by zhoulu on 2016/10/27.
 */

public class ShortVideoPlugin implements IPluginModule {
    private int REQUEST_CONTACT = 1000;// ActivityForResult 的请求码

    public ShortVideoPlugin() {
    }

    @Override
    public Drawable obtainDrawable(Context context) {
        return context.getResources().getDrawable(R.drawable.icon_video);
    }

    @Override
    public String obtainTitle(Context context) {
        return context.getString(R.string.smallvideo);
    }

    @Override
    public void onClick(Fragment fragment, RongExtension rongExtension) {
        fragment.getActivity().startActivityForResult(new Intent(fragment.getActivity(), VideoRecordActivity.class), 1000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO 这里是视频录制完成后的操作处理
    }


}
