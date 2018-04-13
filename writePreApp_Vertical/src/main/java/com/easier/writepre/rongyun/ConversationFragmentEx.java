package com.easier.writepre.rongyun;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.utils.CheckPermissionUtils;

import io.rong.imkit.fragment.ConversationFragment;

/**
 * 自定义会话页面
 * Created by zhoulu on 2016/11/20.
 */

public class ConversationFragmentEx extends ConversationFragment {

    /**
     * 打开权限设置提示
     */
    public void showSettingsDialog(final Context context) {
        final Dialog dialog = new Dialog(context, R.style.loading_dialog);
        View view = LayoutInflater.from(context).inflate(
                R.layout.dialog_islogin, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_login_now);
        tv.setText("录音权限被禁用,是否前往打开？");
        TextView confirm = (TextView) view.findViewById(R.id.tv_login);
        confirm.setText("去打开");
        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CheckPermissionUtils.getAppDetailSettingIntent(context);
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_cancel).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });
        dialog.dismiss();
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @Override
    public void onVoiceInputToggleTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!CheckPermissionUtils.isHasAudioRecordPermission(this.getActivity())) {
                showSettingsDialog(this.getActivity());
                return;
            }
        }
        super.onVoiceInputToggleTouch(v, event);
    }
}
