package com.easier.writepre.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.entity.PkWorksRemarkQueryInfo;
import com.easier.writepre.inputmessage.Record;
import com.easier.writepre.inputmessage.recoder.AudioMediaPlayer;
import com.easier.writepre.ui.UserInfoActivity;
import com.easier.writepre.utils.SpanStringUtil;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.widget.RoundImageView;
import com.sj.autolayout.utils.AutoUtils;
import com.sj.autolayout.utils.DateKit;

import java.util.List;

public class PkWorksRemarkQueryAdapter extends BaseAdapter {

    private List<PkWorksRemarkQueryInfo> list;
    private Context mContext;
    private Record currentRecord = null;

    public PkWorksRemarkQueryAdapter(List<PkWorksRemarkQueryInfo> list,
                                     Context mContext) {
        // super();
        this.list = list;
        this.mContext = mContext;
    }

    public void stopVoice() {
        if (currentRecord != null) {
            currentRecord.stopVioce();
            currentRecord = null;
        }
    }

    @Override
    public int getCount() {
        if (list == null)
            return 0;
        else {
            return list.size();
        }
    }

    @Override
    public PkWorksRemarkQueryInfo getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_topic_comment, parent, false);
            viewHolder.img_head = (RoundImageView) convertView
                    .findViewById(R.id.img_head);
            viewHolder.tv_username = (TextView) convertView
                    .findViewById(R.id.tv_username);
            viewHolder.tv_time_ago = (TextView) convertView
                    .findViewById(R.id.tv_time_ago);
            viewHolder.ll_comment_reply = (LinearLayout) convertView
                    .findViewById(R.id.ll_comment_reply);
//			viewHolder.tv_comment_reply = (TextView) convertView
//					.findViewById(R.id.tv_comment_reply);
//			viewHolder.tv_reply_username = (TextView) convertView
//					.findViewById(R.id.tv_reply_username);
            viewHolder.tv_comment = (TextView) convertView
                    .findViewById(R.id.tv_comment);
            viewHolder.btn_record = (Button) convertView.findViewById(R.id.btn_record);
            convertView.setTag(viewHolder);
            AutoUtils.autoSize(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final PkWorksRemarkQueryInfo pwrqi = getItem(position);
        String head = pwrqi.getHead_img();

        viewHolder.img_head.setImageView(StringUtil.getHeadUrl(head));
        viewHolder.img_head.setIconView(pwrqi.getIs_teacher());
        if (!TextUtils.equals("", pwrqi.getVoice_url()) && pwrqi.getVoice_url().endsWith(".amr")) {
            viewHolder.ll_comment_reply.setVisibility(View.VISIBLE);
            viewHolder.btn_record.setTag(new Record(mContext, StringUtil.getVoiceUrl(pwrqi.getVoice_url()), Integer.parseInt(TextUtils.isEmpty(pwrqi.getVoice_len()) ? "0" : pwrqi.getVoice_len()), viewHolder.btn_record));
            viewHolder.btn_record.setVisibility(View.VISIBLE);
            if (TextUtils.equals(AudioMediaPlayer.getInstance().getPath(), StringUtil.getVoiceUrl(pwrqi.getVoice_url())) && AudioMediaPlayer.getInstance().isPlaying()) {
                ((Record) viewHolder.btn_record.getTag()).startAnimation();
            } else {
                ((Record) viewHolder.btn_record.getTag()).stopAnimation();
            }
            viewHolder.btn_record.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button selectBtn = (Button) v;
                    if (currentRecord != null) {
                        if (TextUtils.equals(currentRecord.getUrl(), StringUtil.getVoiceUrl(pwrqi.getVoice_url()))) {
                            currentRecord.stopVioce();
                            currentRecord = null;
                        } else {
                            currentRecord.stopVioce();
                            currentRecord = null;
                            currentRecord = (Record) selectBtn.getTag();
                            currentRecord.playVoice();
                        }

                    } else {
                        currentRecord = (Record) selectBtn.getTag();
                        currentRecord.playVoice();
                    }
                }
            });
        } else {
            viewHolder.ll_comment_reply.setVisibility(View.GONE);
            viewHolder.btn_record.setVisibility(View.GONE);
        }
//		BitmapHelp.getBitmapUtils().display(viewHolder.img_head, StringUtil.getHeadUrl(head),new BitmapLoadCallBack<View>() {
//
//			@Override
//			public void onLoadCompleted(View arg0, String arg1, Bitmap arg2, BitmapDisplayConfig arg3,
//					BitmapLoadFrom arg4) {
//				viewHolder.img_head.setImageBitmap(arg2);
//				
//			}
//
//			@Override
//			public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
//				viewHolder.img_head.setImageResource(R.drawable.empty_head);
//				
//			}
//		});
        viewHolder.img_head.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(mContext, UserInfoActivity.class);
                intent.putExtra("user_id", pwrqi.getUser_id());
                mContext.startActivity(intent);
            }
        });
        // ImageLoader.getInstance().displayImage( head,viewHolder.img_head);

        String username = pwrqi.getUname().toString();
        viewHolder.tv_username.setText(username);

        String createTime = DateKit.timeFormat(pwrqi.getCtime());
//		viewHolder.tv_time_ago.setVisibility(View.VISIBLE);
        viewHolder.tv_time_ago.setText(createTime);
        viewHolder.ll_comment_reply.setVisibility(View.VISIBLE);
        String comment = "";
        String replyUsername = pwrqi.getReply_to_uname()
                .toString();
        if (!replyUsername.equals("")) {
            comment = "回复@" + replyUsername + ":"
                    + pwrqi.getTitle().toString();
            viewHolder.tv_comment.setText(new SpanStringUtil()
                    .getClickableSpan(mContext, pwrqi.getReply_to_user(),
                            comment));
            viewHolder.tv_comment
                    .setOnTouchListener(new View.OnTouchListener() {

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return SpanStringUtil.onTouchClick(v, event);
                        }
                    });
//			viewHolder.tv_comment_reply.setVisibility(View.VISIBLE);
//			viewHolder.tv_reply_username.setVisibility(View.VISIBLE);
//			viewHolder.tv_reply_username.setText(replyUsername + "");
//			viewHolder.tv_reply_username.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					Intent intent = new Intent(mContext,UserInfoActivity.class);
//					intent.putExtra("user_id", pwrqi.getReply_to_user());
//					mContext.startActivity(intent);
//				}
//			});
        } else {
//			viewHolder.tv_comment_reply.setVisibility(View.GONE);
//			viewHolder.tv_reply_username.setVisibility(View.GONE);
            comment = pwrqi.getTitle().toString();
            // viewHolder.tv_comment.setVisibility(View.VISIBLE);
            viewHolder.tv_comment.setText(comment);
        }


        convertView.setBackgroundColor(Color.WHITE);// 这里设置为白色
        return convertView;
    }

    class ViewHolder {
        RoundImageView img_head;// 头像
        TextView tv_username;// 用户名
        TextView tv_time_ago;// 创建时间
        LinearLayout ll_comment_reply;
        //		TextView tv_comment_reply;// 回复
//		TextView tv_reply_username;// @鸣人
        TextView tv_comment;// 评论
        Button btn_record;
    }
}
