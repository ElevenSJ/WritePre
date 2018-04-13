package com.easier.writepre.rongyun;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.entity.ContentInfo;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.ui.MediaActivity;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.StringUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.rong.common.RLog;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.manager.SendImageManager;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.widget.ArraysDialogFragment;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imkit.widget.provider.ImageMessageItemProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.utils.BitmapUtil;

/**
 * 融云小视频消息展示
 * Created by zhoulu on 2016/10/28.
 */
@ProviderTag(messageContent = WPShortVideoMessage.class)
public class WPShortVideoMessageItemProvider extends IContainerItemProvider.MessageProvider<WPShortVideoMessage> {
    private HttpHandler handler;

    private class ViewHolder {
        AsyncImageView iv_cover;
        ImageView iv_play;
        TextView tv_download_msg;
    }

    @Override
    public void bindView(View view, int i, WPShortVideoMessage wpShortVideoMessage, UIMessage uiMessage) {
        ViewHolder holder = (ViewHolder) view.getTag();

        if (uiMessage.getMessageDirection() == Message.MessageDirection.SEND) {//消息方向，自己发送的
            view.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_right);
        } else {
            view.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_left);
        }
        //这里处理需要加载的封面
        if (TextUtils.isEmpty(wpShortVideoMessage.getImageContent())) {
            Bitmap bmp = Bitmap.createBitmap(180, 120, Bitmap.Config.ARGB_8888);
            bmp.eraseColor(view.getResources().getColor(R.color.black));
            holder.iv_cover.setImageBitmap(bmp);
        } else {
            holder.iv_cover.setImageBitmap(BitmapUtil.getBitmapFromBase64(wpShortVideoMessage.getImageContent()));
        }
    }

    @Override
    public Spannable getContentSummary(WPShortVideoMessage wpShortVideoMessage) {
        return new SpannableString("[小视频]");
    }

    @Override
    public void onItemClick(View view, int i, WPShortVideoMessage wpShortVideoMessage, UIMessage uiMessage) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        String url = StringUtil.getVideoUrl(wpShortVideoMessage.getFileUri());
        Intent intent = new Intent(view.getContext(), MediaActivity.class);
        intent.putExtra(MediaActivity.URL, url);
        view.getContext().startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int i, WPShortVideoMessage wpShortVideoMessage, final UIMessage uiMessage) {
        long deltaTime = RongIM.getInstance().getDeltaTime();
        long normalTime = System.currentTimeMillis() - deltaTime;
        boolean enableMessageRecall = false;
        int messageRecallInterval = -1;
        boolean hasSent = !uiMessage.getSentStatus().equals(Message.SentStatus.SENDING) && !uiMessage.getSentStatus().equals(Message.SentStatus.FAILED);

        try {
            enableMessageRecall = RongContext.getInstance().getResources().getBoolean(io.rong.imkit.R.bool.rc_enable_message_recall);
            messageRecallInterval = RongContext.getInstance().getResources().getInteger(io.rong.imkit.R.integer.rc_message_recall_interval);
        } catch (Resources.NotFoundException var14) {
            RLog.e("WPShortVideoMessageItemProvider", "rc_message_recall_interval not configure in rc_config.xml");
            var14.printStackTrace();
        }

        String[] items;
        if (hasSent && enableMessageRecall && normalTime - uiMessage.getSentTime() <= (long) (messageRecallInterval * 1000) && uiMessage.getSenderUserId().equals(RongIM.getInstance().getCurrentUserId())) {
            items = new String[]{view.getContext().getResources().getString(io.rong.imkit.R.string.rc_dialog_item_message_delete), view.getContext().getResources().getString(io.rong.imkit.R.string.rc_dialog_item_message_recall)};
        } else {
            items = new String[]{view.getContext().getResources().getString(io.rong.imkit.R.string.rc_dialog_item_message_delete)};
        }

        OptionsPopupDialog.newInstance(view.getContext(), items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
            public void onOptionsItemClicked(int which) {
                if (which == 0) {
                    SendImageManager.getInstance().cancelSendingImage(uiMessage.getConversationType(), uiMessage.getTargetId(), uiMessage.getMessageId());
                    RongIM.getInstance().deleteMessages(new int[]{uiMessage.getMessageId()}, (RongIMClient.ResultCallback) null);
                } else if (which == 1) {
                    RongIM.getInstance().recallMessage(uiMessage.getMessage());
                }

            }
        }).show();
    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.rongyun_videomessage_item, null);
        ViewHolder holder = new ViewHolder();
        holder.iv_cover = (AsyncImageView) view.findViewById(R.id.iv_cover);
        holder.iv_play = (ImageView) view.findViewById(R.id.iv_play);
        holder.tv_download_msg = (TextView) view.findViewById(R.id.tv_download_msg);
        view.setTag(holder);
        return view;
    }
}