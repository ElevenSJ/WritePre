package com.easier.writepre.rongyun;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.utils.LogUtils;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.MessageContent;
import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;


public class SealNotificationReceiver extends PushMessageReceiver {
    @Override
    public boolean onNotificationMessageArrived(Context context, PushNotificationMessage message) {
        return false;
    }

    @Override
    public boolean onNotificationMessageClicked(Context context, PushNotificationMessage message) {
        String type = message.getConversationType().getName();
        String tagName = message.getObjectName();
        if (TextUtils.equals("group", type)) {
            //群组a会话
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("rong_id", message.getTargetId());
            intent.putExtra("rong_title", message.getTargetUserName());
            intent.putExtra("rong_type", message.getConversationType().getName());
            intent.putExtra("tagName", tagName);
            context.startActivity(intent);
        } else if (TextUtils.equals("private", type)) {
            //私聊
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("rong_id", message.getSenderId());
            intent.putExtra("rong_title", message.getTargetUserName());
            intent.putExtra("rong_type", message.getConversationType().getName());
            intent.putExtra("tagName", tagName);
            context.startActivity(intent);
        }
        return true;
    }
}
