package com.easier.writepre.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.entity.AttentionInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.http.WritePreListener;
import com.easier.writepre.param.AttentionParams;
import com.easier.writepre.param.UnAttentionParams;
import com.easier.writepre.response.AttentionResponse;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.UnAttentionResponse;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.RoundImageView;
import com.sj.autolayout.utils.AutoUtils;

/**
 * 关注适配器
 * 
 * @author zhoulu
 * 
 */
public class AttentionAdapter extends BaseAdapter implements
		WritePreListener<BaseResponse> {

	private Context mContext;

	private List<AttentionInfo> list = new ArrayList<AttentionInfo>();
	/**
	 * 0 显示 TA的关注 1TA的粉丝
	 */
	private int flag = 0;

	private String user_id;// 用户ID
	private Handler mHandler;

	public AttentionAdapter(String user_id, Context context, int flag,
			Handler mHandler) {
		mContext = context;
		this.flag = flag;
		this.user_id = user_id;
		this.mHandler = mHandler;
	}

	public void clearData() {
		if (list != null) {
			list.clear();
			notifyDataSetChanged();
		}
	}

	public void setData(List<AttentionInfo> data) {
		if (data == null) {
			return;
		}
		list.clear();
		list.addAll(data);
		notifyDataSetChanged();
	}

	public void addData(List<AttentionInfo> data) {
		if (data == null) {
			return;
		}
		list.addAll(data);
		notifyDataSetChanged();
	}

	public List<AttentionInfo> getData() {
		return list;
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public AttentionInfo getItem(int position) {
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
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.attention_item, parent, false);
			holder.iv_head = (RoundImageView) convertView
					.findViewById(R.id.iv_head);

			holder.tv_uname = (TextView) convertView
					.findViewById(R.id.tv_uname);
			holder.tv_signature = (TextView) convertView
					.findViewById(R.id.tv_signature);
			holder.btn_stauts = (Button) convertView
					.findViewById(R.id.btn_attention_stauts);
			convertView.setTag(holder);
			AutoUtils.autoSize(convertView);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		AttentionInfo contentInfo = getItem(position);
		holder.tv_uname.setText(contentInfo.getUname());
		holder.tv_signature.setText(contentInfo.getSignature());
		if (TextUtils.equals(user_id, SPUtils.instance().getLoginEntity()
				.get_id())) {
			// 用户点击的是自己的个人信息,需要显示关注按钮
			holder.btn_stauts.setVisibility(View.VISIBLE);
			holder.btn_stauts.setTag(contentInfo.getUser_id());
			if (flag == 0) {
				// TA的关注(登录用户在自己的关注列中可以取消关注)
				holder.btn_stauts.setText("取消关注");
				holder.btn_stauts.setEnabled(true);
			} else {
				// TA的粉丝(登录用户可以关注自己的粉丝,进行相互关注)
				int type = 1;
				if (!TextUtils.isEmpty(contentInfo.getType())) {
					type = Integer.parseInt(contentInfo.getType());
				}
				switch (type) {
				case 1:
					holder.btn_stauts.setText("关注TA");
					holder.btn_stauts.setEnabled(true);
					break;
				case 2:
				case 3:
					holder.btn_stauts.setText("已关注");
					holder.btn_stauts.setEnabled(false);
					break;

				}
			}
		} else {
			// 用户点击的是他人的个人信息,需要隐藏关注按钮
			holder.btn_stauts.setVisibility(View.INVISIBLE);
		}

		holder.iv_head.setImageView(StringUtil.getHeadUrl(contentInfo
				.getHead_img()));
		// 设置角标
		holder.iv_head.setIconView(contentInfo.getIs_teacher());
		holder.btn_stauts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Button button = (Button) v;
				String txt = button.getText().toString();
				if (TextUtils.equals(txt, "取消关注")) {
					// 调用取消关注接口
					showDelDialog(mContext, (String) button.getTag());
				} else if (TextUtils.equals(txt, "关注TA")) {
					// 调用关注接口
					requestAttention((String) button.getTag());
				} else if (TextUtils.equals(txt, "已关注")) {
					// 打开个人详情页面
					if (flag == 1) {
						mHandler.obtainMessage(1, (String) button.getTag())
								.sendToTarget();
					}
					// requestUnAttention((String) button.getTag());
				}
			}
		});
		return convertView;
	}

	/**
	 * 请求关注
	 * 
	 */
	private void requestAttention(String id) {
		LogUtils.e("关注请求id:" + id);
		AttentionParams parms = new AttentionParams(id);
		RequestManager.request(mContext, id, parms, AttentionResponse.class,
				this, SPUtils.instance().getSocialPropEntity()
						.getApp_socail_server());
	}

	/**
	 * 请求取消关注
	 * 
	 */
	private void requestUnAttention(String id) {
		UnAttentionParams parms = new UnAttentionParams(id);
		RequestManager.request(mContext, id, parms, UnAttentionResponse.class,
				this, SPUtils.instance().getSocialPropEntity()
						.getApp_socail_server());
	}

	public class ViewHolder {
		private RoundImageView iv_head;
		public TextView tv_uname, tv_signature;
		private Button btn_stauts;

	}

	public void showDelDialog(Context context, final String id) {

		final Dialog dialog = new Dialog(context, R.style.loading_dialog);
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_islogin, null);
		TextView tv = (TextView) view.findViewById(R.id.tv_login_now);
		tv.setText("确定取消关注?");
		TextView confirm = (TextView) view.findViewById(R.id.tv_login);
		confirm.setText("确定");
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				requestUnAttention(id);
				dialog.dismiss();
			}
		});
		view.findViewById(R.id.tv_cancel).setOnClickListener(
				new OnClickListener() {

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
	public void onResponse(BaseResponse response) {

	}

	/**
	 * 更新数据
	 * 
	 * @param userid
	 * @param type
	 *            1 关注 2或者3 相互关注
	 */
	public synchronized void updateData(String userid, int type) {
		for (int i = 0; i < list.size(); i++) {
			if (TextUtils.equals(list.get(i).getUser_id(), userid)) {
				list.get(i).setType(type + "");
				break;
			}
		}
		notifyDataSetChanged();
	}

	/**
	 * 删除数据
	 * 
	 * @param userid
	 * @param type
	 *            1 关注 2或者3 相互关注
	 */
	public synchronized void removeData(String userid) {
		for (int i = 0; i < list.size(); i++) {
			if (TextUtils.equals(list.get(i).getUser_id(), userid)) {
				list.remove(i);
				break;
			}
		}
		notifyDataSetChanged();
	}

	@Override
	public void onResponse(String tag, BaseResponse response) {
		if ("0".equals(response.getResCode())) {
			if (response instanceof AttentionResponse) {
				// TODO 关注接口返回
				LogUtils.e("关注请求返回id:" + tag);
				if (flag == 0) {

				} else {
					updateData(tag, 2);
				}

			} else if (response instanceof UnAttentionResponse) {
				// TODO 取消关注接口返回
				if (flag == 0) {
					removeData(tag);
				} else {
					updateData(tag, 1);
				}
			}
		} else {
			ToastUtil.show(response.getResMsg());
		}

	}
}
