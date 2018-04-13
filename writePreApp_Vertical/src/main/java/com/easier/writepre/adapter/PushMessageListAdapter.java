package com.easier.writepre.adapter;

import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.db.DBPushMessageHelper;
import com.easier.writepre.entity.ActionItem;
import com.easier.writepre.entity.PushMessageEntity;
import com.easier.writepre.mainview.SocialMainView;
import com.easier.writepre.ui.ApplyForCertificationActvity;
import com.easier.writepre.ui.AttentionActivity;
import com.easier.writepre.ui.CircleDetailActivity;
import com.easier.writepre.ui.CircleMsgDetailActivity;
import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.ui.MessageDetailActivity;
import com.easier.writepre.ui.PkMsgDetailActivity;
import com.easier.writepre.ui.PkTeacherStudentInfoActivity;
import com.easier.writepre.ui.TopicDetailActivity;
import com.easier.writepre.ui.WebViewActivity;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.widget.MyDialog;
import com.sj.autolayout.utils.AutoUtils;
import com.sj.autolayout.utils.DateKit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 推送消息适配器
 */
@SuppressLint("InflateParams")
public class PushMessageListAdapter extends BaseAdapter {

	private Context mContext;

	private List<PushMessageEntity> list = new ArrayList<PushMessageEntity>();

	public PushMessageListAdapter(Context context) {
		mContext = context;
	}

	public void setData(List<PushMessageEntity> data) {
		list.clear();
		list.addAll(data);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public PushMessageEntity getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.push_message_item, parent, false);
			holder.imgIsRead = (ImageView) convertView.findViewById(R.id.img_read);
			holder.TvTitle = (TextView) convertView.findViewById(R.id.tv_title);
			holder.TvMsg = (TextView) convertView.findViewById(R.id.tv_msg);
			holder.TvTime = (TextView) convertView.findViewById(R.id.tv_creat_time);
			convertView.setTag(holder);
			AutoUtils.autoSize(convertView);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final PushMessageEntity pushMessageEntity = getItem(position);
		if (TextUtils.isEmpty(pushMessageEntity.getUser_id())) {
			holder.TvTitle.setText("【系统消息】 " + pushMessageEntity.getTitle());
		} else {
			holder.TvTitle.setText("  "+pushMessageEntity.getTitle());
		}
		holder.TvMsg.setText(pushMessageEntity.getDesc());
		holder.TvTime.setText(DateKit.timeFormat(pushMessageEntity.getCtime()));
		if (pushMessageEntity.getIsRead().equals("0")) {
			holder.imgIsRead.setVisibility(View.VISIBLE);
		} else {
			holder.imgIsRead.setVisibility(View.INVISIBLE);
		}
		convertView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				showDeleteDialog(position);
				return false;
			}
		});
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PushMessageEntity entity = getItem(position);
				entity.setIsRead("1");
				DBPushMessageHelper.update(entity);
				notifyDataSetChanged();
				toAction(entity);
			}

		});
		return convertView;
	}

	/**
	 * 消息对应处理
	 * 
	 * @param entity
	 */
	protected void toAction(PushMessageEntity entity) {
		if (TextUtils.isEmpty(entity.getType())) {
			return;
		}
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		if (entity.getType().equals("inlink")) {
			intent.setClass(mContext, WebViewActivity.class);
			intent.putExtra("title", entity.getTitle());
			intent.putExtra("url", entity.getLink_url());
			mContext.startActivity(intent);
		} else if (entity.getType().equals("text")) {
			intent.setClass(mContext, MessageDetailActivity.class);
			intent.putExtra("message", entity);
			mContext.startActivity(intent);
		} else if (entity.getType().equals("module")) {
			if (TextUtils.isEmpty(entity.getModule())) {
				return;
			}
			if (entity.getModule().equals("square")) {
				int index = SocialMainView.TAB_SQUARE;
				intent.setClass(mContext, MainActivity.class);
				intent.putExtra(MainActivity.MAIN_TAB_INDEX, MainActivity.TYPE_TWO);
				intent.putExtra(SocialMainView.TAB_INDEX, index);
				intent.putExtra(SocialMainView.ITEM_SQUARE_INDEX, SocialMainView.ITEM_1);
				mContext.startActivity(intent);
			} else if (entity.getModule().equals("circle")) {
				int index = SocialMainView.TAB_CIRCLE;// 隐藏大会时圈子为1
				intent.setClass(mContext, MainActivity.class);
				intent.putExtra(MainActivity.MAIN_TAB_INDEX, MainActivity.TYPE_TWO);
				intent.putExtra(SocialMainView.TAB_INDEX, index);
				intent.putExtra(SocialMainView.ITEM_CIRCLE_INDEX, SocialMainView.ITEM_2);
				mContext.startActivity(intent);
			}else if (entity.getModule().equals("pk")) {
				// index = SocialMainView.TAB_CIRCLE;
			}
			else if (entity.getModule().equals("pk_works")) {
				// index = SocialMainView.TAB_CIRCLE;
				if (!TextUtils.isEmpty(entity.getExt_info())) {
					intent.setClass(mContext, PkTeacherStudentInfoActivity.class);
					intent.putExtra("works_id", entity.getExt_info()); // 圈子id
					mContext.startActivity(intent);
				}
			} else if (entity.getModule().equals("circle_admin")) {
				int index = SocialMainView.TAB_CIRCLE;
				if (!TextUtils.isEmpty(entity.getExt_info())) {
					intent.setClass(mContext, CircleDetailActivity.class);
					intent.putExtra("circle_id", entity.getExt_info()); // 圈子id
					mContext.startActivity(intent);
				}
			} else if (entity.getModule().equals("square_post")) {
				if (TextUtils.isEmpty(entity.getExt_info())) {
					return;
				}
				intent.putExtra(CircleMsgDetailActivity.ACTIVITY_TYPE, TopicDetailActivity.LIST_ACTIVITY);
				intent.setClass(mContext, TopicDetailActivity.class);
				intent.putExtra("id", entity.getExt_info()); // 帖子id
				mContext.startActivity(intent);

			} else if (entity.getModule().equals("circle_post")) {
				if (TextUtils.isEmpty(entity.getExt_info())) {
					return;
				}
				String[] ids = entity.getExt_info().split(",");
				if (ids.length < 2) {
					return;
				}
				intent.putExtra(CircleMsgDetailActivity.ACTIVITY_TYPE, CircleMsgDetailActivity.LIST_ACTIVITY);
				intent.setClass(mContext, CircleMsgDetailActivity.class);
				intent.putExtra("circle_id", ids[0]); // 帖子id
				intent.putExtra("id", ids[1]); // 帖子id
				mContext.startActivity(intent);
			} else if (entity.getModule().equals("teacher_req")) {
				// 老师申请
				if (LoginUtil.checkLogin(mContext)) {
					intent.setClass(mContext, ApplyForCertificationActvity.class);
					mContext.startActivity(intent);
				}

			} else if (entity.getModule().equals("user_care")) {
				// 我的关注
				if (LoginUtil.checkLogin(mContext)) {
					intent.setClass(mContext, AttentionActivity.class);
					intent.putExtra("id", SPUtils.instance().getLoginEntity().get_id());
					intent.putExtra("select_no", 1);
					mContext.startActivity(intent);
				}
			}
		}
	}

	protected void showDeleteDialog(final int position) {
		MyDialog deleteDialog = new MyDialog(mContext, R.style.loading_dialog);

		deleteDialog.cleanAction();
		deleteDialog.addAction(new ActionItem(mContext, 0, "删除"));

		if (!deleteDialog.isShowing()) {
			deleteDialog.show();
			deleteDialog.setItemOnClickListener(new MyDialog.OnItemOnClickListener() {

				@Override
				public void onItemClick(ActionItem item) {
					DBPushMessageHelper.delRes(getItem(position));
					list.remove(position);
					notifyDataSetChanged();
				}
			});
		}
	}

	public static class ViewHolder {
		private ImageView imgIsRead;
		private TextView TvTitle;
		private TextView TvMsg;
		private TextView TvTime;

	}
}
