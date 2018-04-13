package com.easier.writepre.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
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
import com.easier.writepre.entity.CircleCommentsInfo;
import com.easier.writepre.inputmessage.Record;
import com.easier.writepre.inputmessage.recoder.AudioMediaPlayer;
import com.easier.writepre.ui.UserInfoActivity;
import com.easier.writepre.utils.SpanStringUtil;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.RoundImageView;
import com.sj.autolayout.utils.DateKit;

import java.util.List;

public class CircleCommentsAdapter extends BaseAdapter {
    private List<CircleCommentsInfo> mCircleCommentsList;

    private Context mContext;

    private String zanOrComment;
    private Record currentRecord = null;

    public CircleCommentsAdapter(List<CircleCommentsInfo> mCircleCommentsList,
                                 Context mContext,String zanOrComment) {
        // super();
        this.mCircleCommentsList = mCircleCommentsList;
        this.mContext = mContext;
        this.zanOrComment = zanOrComment;
    }

    public void stopVoice() {
        if (currentRecord != null) {
            currentRecord.stopVioce();
            currentRecord = null;
        }
    }
    public void setData(List<CircleCommentsInfo> mCircleCommentsList,String zanOrComment){
        this.mCircleCommentsList = mCircleCommentsList;
        this.zanOrComment = zanOrComment;
    }

    @Override
    public int getCount() {
        if (mCircleCommentsList == null)
            return 0;
        else {
            return mCircleCommentsList.size();
        }
    }

    @Override
    public CircleCommentsInfo getItem(int position) {
        // TODO Auto-generated method stub
        return mCircleCommentsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder;
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
            viewHolder.tv_comment = (TextView) convertView
                    .findViewById(R.id.tv_comment);
            viewHolder.btn_record = (Button) convertView.findViewById(R.id.btn_record);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final CircleCommentsInfo cci = getItem(position);
        String head = cci.getHead_img();

        viewHolder.img_head.setImageView(StringUtil.getHeadUrl(head));
        viewHolder.img_head.setIconView(cci.getIs_teacher());
        if (!TextUtils.equals("", cci.getVoice_url())&&cci.getVoice_url().endsWith(".amr")) {
            viewHolder.ll_comment_reply.setVisibility(View.VISIBLE);
            viewHolder.btn_record.setTag(new Record(mContext,StringUtil.getVoiceUrl(cci.getVoice_url()), Integer.parseInt(TextUtils.isEmpty(cci.getVoice_len()) ? "0" : cci.getVoice_len()), viewHolder.btn_record));
            viewHolder.btn_record.setVisibility(View.VISIBLE);
            if (TextUtils.equals(AudioMediaPlayer.getInstance().getPath(), StringUtil.getVoiceUrl(cci.getVoice_url())) && AudioMediaPlayer.getInstance().isPlaying()) {
                ((Record) viewHolder.btn_record.getTag()).startAnimation();
            } else {
                ((Record) viewHolder.btn_record.getTag()).stopAnimation();
            }
            viewHolder.btn_record.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button selectBtn = (Button) v;
                    if (currentRecord != null) {
                        if (TextUtils.equals(currentRecord.getUrl(),StringUtil.getVoiceUrl(cci.getVoice_url()))) {
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

        viewHolder.tv_username.setText(cci.getUname());

        String createTime = DateKit.timeFormat(cci.getCtime());
        viewHolder.tv_time_ago.setVisibility(View.VISIBLE);
        viewHolder.tv_time_ago.setText(createTime);

        viewHolder.img_head.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                toUserDetail(cci.getUser_id());
            }
        });
        String comment = "";
        if (zanOrComment.equals("1")) {
            viewHolder.ll_comment_reply.setVisibility(View.VISIBLE);
            String replyUsername = cci.getReply_to_uname().toString();
            if (!replyUsername.equals("")) {
                comment = "回复@" + replyUsername + ":"
                        + cci.getTitle().toString();
                viewHolder.tv_comment.setText(new SpanStringUtil()
                        .getClickableSpan(mContext, cci.getReply_to_user(),
                                comment));
                viewHolder.tv_comment
                        .setOnTouchListener(new View.OnTouchListener() {

                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return SpanStringUtil.onTouchClick(v, event);
                            }
                        });
            } else {
                comment = cci.getTitle().toString();
                viewHolder.tv_comment.setText(comment);
            }
        }
        convertView.setBackgroundColor(Color.WHITE);// 这里设置为白色
        return convertView;
    }

    class ViewHolder {
        RoundImageView img_head;// 头像
        TextView tv_username;// 用户名
        TextView tv_time_ago;// 创建时间
        LinearLayout ll_comment_reply;
        TextView tv_comment;// 评论
        Button btn_record;//语音

    }

    /**
     * 查看用户详情
     */
    public void toUserDetail(String userId) {
        Intent intent = new Intent(mContext, UserInfoActivity.class);
        intent.putExtra("user_id", userId);
        mContext.startActivity(intent);
    }

}