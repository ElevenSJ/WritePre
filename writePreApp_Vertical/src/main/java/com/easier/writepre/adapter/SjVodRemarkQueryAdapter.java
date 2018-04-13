package com.easier.writepre.adapter;

import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.easier.writepre.R;
import com.easier.writepre.entity.SjVodRemarkQueryInfo;
import com.easier.writepre.ui.UserInfoActivity;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.SpanStringUtil;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.widget.RoundImageView;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.DateKit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SjVodRemarkQueryAdapter extends BaseAdapter {

	private List<SjVodRemarkQueryInfo> list;
	private Context mContext;

	public SjVodRemarkQueryAdapter(List<SjVodRemarkQueryInfo> list,
			Context mContext) {
		// super();
		this.list = list;
		this.mContext = mContext;
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
	public SjVodRemarkQueryInfo getItem(int position) {
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
					R.layout.item_topic_comment, null);
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
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final SjVodRemarkQueryInfo svrqi = getItem(position);
		String head = svrqi.getHead_img();
		
		viewHolder.img_head.setImageView(StringUtil.getHeadUrl(head));
		viewHolder.img_head.setIconView(svrqi.getIs_teacher());
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
				intent.putExtra("user_id", svrqi.getUser_id());
				mContext.startActivity(intent);
			}
		});
		// ImageLoader.getInstance().displayImage( head,viewHolder.img_head);
//		String comment = svrqi.getTitle().toString();
		String username = svrqi.getUname().toString();
		viewHolder.tv_username.setText(username);

		String createTime = DateKit.timeFormat(svrqi.getCtime());
//		viewHolder.tv_time_ago.setVisibility(View.VISIBLE);
		viewHolder.tv_time_ago.setText(createTime);
		viewHolder.ll_comment_reply.setVisibility(View.VISIBLE);
		String comment = "";
		String replyUsername = svrqi.getReply_to_uname()
				.toString();
		if (!replyUsername.equals("")) {
			comment = "回复@" + replyUsername + ":"
					+ svrqi.getTitle().toString();
			viewHolder.tv_comment.setText(new SpanStringUtil()
					.getClickableSpan(mContext,svrqi.getReply_to_user(),
							comment));
			viewHolder.tv_comment
			.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return SpanStringUtil.onTouchClick(v, event);
				}
			});
//			viewHolder.tv_comment.setText(":" + comment);
//			viewHolder.tv_comment_reply.setVisibility(View.VISIBLE);
//			viewHolder.tv_reply_username.setVisibility(View.VISIBLE);
//			viewHolder.tv_reply_username.setText(replyUsername + "");
//			viewHolder.tv_reply_username
//					.setOnClickListener(new OnClickListener() {
//
//						@Override
//						public void onClick(View v) {
//							Intent intent = new Intent(mContext,
//									UserInfoActivity.class);
//							intent.putExtra("user_id", list.get(position)
//									.getReply_to_user());
//							mContext.startActivity(intent);
//						}
//					});
		} else {
			comment = svrqi.getTitle().toString();
			viewHolder.tv_comment.setText(comment);
//			viewHolder.tv_comment.setText(comment);
//			viewHolder.tv_comment_reply.setVisibility(View.GONE);
//			viewHolder.tv_reply_username.setVisibility(View.GONE);
		}

		// viewHolder.tv_comment.setVisibility(View.VISIBLE);

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
	}
}
