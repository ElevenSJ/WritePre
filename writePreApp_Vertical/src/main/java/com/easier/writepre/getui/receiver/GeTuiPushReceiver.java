package com.easier.writepre.getui.receiver;

import java.util.Random;

import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.entity.GeTuiMessageEntity;
import com.easier.writepre.manager.BindClientManager;
import com.easier.writepre.ui.LoginActivity;
import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.ui.PushMessageActivity;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.Utils;
import com.google.gson.Gson;
import com.igexin.sdk.PushConsts;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

/**
 * 个推消息接收器
 *
 * @author dqt
 */
public class GeTuiPushReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        System.out.println(bundle.toString());
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                byte[] payload = bundle.getByteArray("payload");
                LogUtils.e("接收到透传消息" + new String(payload));
                notifyMessage(context, new String(payload));
                break;
            case PushConsts.GET_CLIENTID:
                LogUtils.e("接收到clientId" + bundle.getString("clientid"));
                WritePreApp.clientId = bundle.getString("clientid");
                BindClientManager.getInstance(context.getApplicationContext())
                        .bindClientId();
                break;
            case PushConsts.THIRDPART_FEEDBACK:
                break;
            default:
                break;
        }
    }


    private void notifyMessage(Context context, String msg) {
        LogUtils.e("弹出透传消息通知栏");
        int notificationId = new Random().nextInt(100);
        Gson mGson = new Gson();
        GeTuiMessageEntity entity = mGson.fromJson(msg, GeTuiMessageEntity.class);
        String alertMsg = entity.getMsg();
        String msgType = TextUtils.isEmpty(entity.getType()) ? "" : entity.getType();
        NotificationManager notificationMgr = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent();
        intent.setClass(context, PushMessageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

//        if (Utils.getCurrentCode(WritePreApp.getApp()) < 21) {
            Notification notification = new Notification(R.drawable.ic_launcher, alertMsg, System.currentTimeMillis());

            notification.tickerText=alertMsg;
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_LIGHTS;

            notification.setLatestEventInfo(context, context.getResources().getString(R.string.app_name), alertMsg,
                    PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT));
            notificationMgr.notify(notificationId, notification);

//        }else{
//            NotificationCompat.Builder mNotifyBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
//                    .setTicker(alertMsg)
//                    .setContentTitle(context.getResources().getString(R.string.app_name))
//                    .setContentText(alertMsg)
//                    .setContentIntent(PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT))
//                    .setSmallIcon(R.drawable.ic_launcher)
//                    .setAutoCancel(true)
//                    .setFullScreenIntent(PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT), false);
//            notificationMgr.notify(notificationId, mNotifyBuilder.build());
//        }
    }
}
