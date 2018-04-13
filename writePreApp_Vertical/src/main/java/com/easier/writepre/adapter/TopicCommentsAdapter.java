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
import com.easier.writepre.entity.TopicCommentsInfo;
import com.easier.writepre.inputmessage.Record;
import com.easier.writepre.inputmessage.recoder.AudioMediaPlayer;
import com.easier.writepre.ui.UserInfoActivity;
import com.easier.writepre.utils.SpanStringUtil;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.widget.RoundImageView;
import com.sj.autolayout.utils.AutoUtils;
import com.sj.autolayout.utils.DateKit;

import java.util.List;

public class TopicCommentsAdapter extends BaseAdapter {
    private List<TopicCommentsInfo> mTopicCommentsList;

    private Context mContext;

    private String zanOrComment;
    private Record currentRecord = null;

    public TopicCommentsAdapter(List<TopicCommentsInfo> mTopicCommentsList, Context mContext,String zanOrComment) {
        this.mTopicCommentsList = mTopicCommentsList;
        this.mContext = mContext;
        this.zanOrComment = zanOrComment;
    }

    public void stopVoice() {
        if (currentRecord != null) {
            currentRecord.stopVioce();
            currentRecord = null;
        }
    }

    public void setData(List<TopicCommentsInfo> mTopicCommentsList,String zanOrComment){
        this.mTopicCommentsList = mTopicCommentsList;
        this.zanOrComment = zanOrComment;
    }
    @Override
    public int getCount() {
        if (mTopicCommentsList == null)
            return 0;
        else {
            return mTopicCommentsList.size();
        }
    }

    @Override
    public TopicCommentsInfo getItem(int position) {
        return mTopicCommentsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_topic_comment, parent, false);
            viewHolder.img_head = (RoundImageView) convertView.findViewById(R.id.img_head);
            viewHolder.tv_username = (TextView) convertView.findViewById(R.id.tv_username);
            viewHolder.tv_time_ago = (TextView) convertView.findViewById(R.id.tv_time_ago);
            viewHolder.ll_comment_reply = (LinearLayout) convertView.findViewById(R.id.ll_comment_reply);
            // viewHolder.tv_comment_reply = (TextView) convertView
            // .findViewById(R.id.tv_comment_reply);
            // viewHolder.tv_reply_username = (TextView) convertView
            // .findViewById(R.id.tv_reply_username);
            viewHolder.tv_comment = (TextView) convertView.findViewById(R.id.tv_comment);
            viewHolder.btn_record = (Button) convertView.findViewById(R.id.btn_record);
            convertView.setTag(viewHolder);
            AutoUtils.autoSize(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final TopicCommentsInfo tci = getItem(position);
        if (!TextUtils.equals("", tci.getVoice_url()) && tci.getVoice_url().endsWith(".amr")) {
            viewHolder.ll_comment_reply.setVisibility(View.VISIBLE);
            viewHolder.btn_record.setTag(new Record(mContext,StringUtil.getVoiceUrl(tci.getVoice_url()), Integer.parseInt(TextUtils.isEmpty(tci.getVoice_len()) ? "0" : tci.getVoice_len()), viewHolder.btn_record));
            viewHolder.btn_record.setVisibility(View.VISIBLE);
            if (TextUtils.equals(AudioMediaPlayer.getInstance().getPath(), StringUtil.getVoiceUrl(tci.getVoice_url())) && AudioMediaPlayer.getInstance().isPlaying()) {
                ((Record) viewHolder.btn_record.getTag()).startAnimation();
            } else {
                ((Record) viewHolder.btn_record.getTag()).stopAnimation();
            }
            viewHolder.btn_record.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button selectBtn = (Button) v;
                    if (currentRecord != null) {
                        if (TextUtils.equals(currentRecord.getUrl(), StringUtil.getVoiceUrl(tci.getVoice_url()))) {
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

        String head = tci.getHead_img();

//		BitmapHelp.getBitmapUtils().display(viewHolder.img_head, StringUtil.getHeadUrl(head),
//				new BitmapLoadCallBack<View>() {
//
//					@Override
//					public void onLoadCompleted(View arg0, String arg1, Bitmap arg2, BitmapDisplayConfig arg3,
//							BitmapLoadFrom arg4) {
//						((ImageView)arg0).setImageBitmap(arg2);
//					}
//
//					@Override
//					public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
//						((ImageView)arg0).setImageResource(R.drawable.empty_head);
//
//					}
//				});

        viewHolder.img_head.setImageView(StringUtil.getHeadUrl(head));
        viewHolder.img_head.setIconView(tci.getIs_teacher());
        String username = tci.getUname().toString();
        viewHolder.tv_username.setText(username);
        String createTime = DateKit.timeFormat(tci.getCtime());
        viewHolder.tv_time_ago.setText(createTime);
        String comment = "";
        if (zanOrComment.equals("1")) {
            viewHolder.ll_comment_reply.setVisibility(View.VISIBLE);
            String replyUsername = tci.getReply_to_uname().toString();
            // viewHolder.tv_comment_reply.setVisibility(View.VISIBLE);
            // viewHolder.tv_reply_username.setVisibility(View.VISIBLE);
            if (!replyUsername.equals("")) {
                comment = "回复@" + replyUsername + ":" + tci.getTitle().toString();
                viewHolder.tv_comment
                        .setText(new SpanStringUtil().getClickableSpan(mContext, tci.getReply_to_user(), comment));
                // viewHolder.tv_comment.setMovementMethod(LinkMovementMethod
                // .getInstance());
                viewHolder.tv_comment.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return SpanStringUtil.onTouchClick(v, event);
                    }
                });
                // viewHolder.tv_comment_reply.setVisibility(View.VISIBLE);
                // viewHolder.tv_reply_username.setVisibility(View.VISIBLE);
                // viewHolder.tv_reply_username.setText(replyUsername + "");
                // viewHolder.tv_reply_username
                // .setOnClickListener(new OnClickListener() {
                //
                // @Override
                // public void onClick(View v) {
                // Intent intent = new Intent(mContext,
                // UserInfoActivity.class);
                // intent.putExtra("user_id", mTopicCommentsList
                // .get(position).getReply_to_user());
                // mContext.startActivity(intent);
                // }
                // });
            } else {
                comment = tci.getTitle().toString();
                viewHolder.tv_comment.setText(comment);
                // viewHolder.tv_comment_reply.setVisibility(View.GONE);
                // viewHolder.tv_reply_username.setVisibility(View.GONE);
            }

            // viewHolder.tv_comment.setVisibility(View.VISIBLE);

            // if (!replyUsername.equals("")) {
            // viewHolder.tv_comment.setText(":" + comment);
            // } else
            // viewHolder.tv_comment.setText(comment);
        }

        viewHolder.img_head.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserInfoActivity.class);
                intent.putExtra("user_id", mTopicCommentsList.get(position).getUser_id());
                mContext.startActivity(intent);
            }
        });
        convertView.setBackgroundColor(Color.WHITE);// 这里设置为白色
        return convertView;
    }

    class ViewHolder {
        RoundImageView img_head;// 头像
        TextView tv_username;// 用户名
        TextView tv_time_ago;// 创建时间
        LinearLayout ll_comment_reply;
        // TextView tv_comment_reply;// 回复
        // TextView tv_reply_username;// @鸣人
        TextView tv_comment;// 评论
        Button btn_record;

    }


}