package com.easier.writepre.rongyun;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easier.writepre.R;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;

/**
 * 融云群公告消息展示
 * Created by zhoulu on 2016/10/28.
 */
@ProviderTag(messageContent = WPNoticeMessage.class, showPortrait = false, hide = true, showProgress = false, showSummaryWithName = false)
public class WPNoticeMessageItemProvider extends IContainerItemProvider.MessageProvider<WPNoticeMessage> {
    class ViewHolder {
        RelativeLayout rel_notice_layout;
        TextView textView;
    }

    @Override
    public void bindView(View view, int i, WPNoticeMessage wpNoticeMessage, UIMessage uiMessage) {
        ViewHolder holder = (WPNoticeMessageItemProvider.ViewHolder) view.getTag();
        if (uiMessage.getMessageDirection() == Message.MessageDirection.SEND) {//消息方向，自己发送的
            holder.rel_notice_layout.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_right);
        } else {
            holder.rel_notice_layout.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_left);
        }
        holder.textView.setText(new StringBuilder().append("\u3000").append(wpNoticeMessage.getMessage()).toString());
    }

    @Override
    public Spannable getContentSummary(WPNoticeMessage wpNoticeMessage) {
        return new SpannableString("[公告消息]");
    }

    @Override
    public void onItemClick(View view, int i, WPNoticeMessage wpNoticeMessage, UIMessage uiMessage) {

    }

    @Override
    public void onItemLongClick(View view, int i, WPNoticeMessage wpNoticeMessage, UIMessage uiMessage) {

    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.rongyun_noticemessage_item, null);
        ViewHolder holder = new ViewHolder();
        holder.rel_notice_layout = (RelativeLayout) view.findViewById(R.id.rel_notice_layout);
        holder.textView = (TextView) view.findViewById(R.id.tv_notice_content);
        view.setTag(holder);
        return view;
    }
}