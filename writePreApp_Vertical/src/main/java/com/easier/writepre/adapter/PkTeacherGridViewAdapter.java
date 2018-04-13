package com.easier.writepre.adapter;

import java.util.List;
import com.easier.writepre.R;
import com.easier.writepre.http.Constant;
import com.easier.writepre.response.PkWorksQueryTeacherResponse.ContentInfo;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.widget.RoundImageView;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.AutoUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PkTeacherGridViewAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	private List<ContentInfo> list;

	private Context context;

	public PkTeacherGridViewAdapter(Context context, List<ContentInfo> list) {
		this.inflater = LayoutInflater.from(context);
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public ContentInfo getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(
					R.layout.pk_teacher_student_grid_item, parent, false);
			holder.tv_teacher_student_name = (TextView) convertView
					.findViewById(R.id.tv_teacher_student_name);
			holder.tv_teacher_student_ticket = (TextView) convertView
					.findViewById(R.id.tv_teacher_student_ticket);
			holder.iv_teacher_student_bg = (ImageView) convertView
					.findViewById(R.id.iv_teacher_student_bg);
			holder.iv_teacher_student_head = (RoundImageView) convertView
					.findViewById(R.id.iv_teacher_student_head);
			convertView.setTag(holder);
			AutoUtils.autoSize(convertView);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ContentInfo cInfo = getItem(position);

		holder.iv_teacher_student_head.setImageView(StringUtil.getHeadUrl(cInfo.getHead_img()));
		holder.iv_teacher_student_head.setIconView(cInfo.getIs_teacher());
		// BitmapHelp.getBitmapUtils().display(holder.iv_teacher_student_head,
		// StringUtil.getHeadUrl(cInfo.getHead_img()),
		// new BitmapLoadCallBack<View>() {
		//
		// @Override
		// public void onLoadCompleted(View arg0, String arg1, Bitmap arg2,
		// BitmapDisplayConfig arg3,
		// BitmapLoadFrom arg4) {
		// holder.iv_teacher_student_head.setImageBitmap(arg2);
		//
		// }
		//
		// @Override
		// public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
		// holder.iv_teacher_student_head.setImageResource(R.drawable.empty_head);
		//
		// }
		// });

		BitmapHelp.getBitmapUtils().display(
				holder.iv_teacher_student_bg,
				StringUtil.getImgeUrl(cInfo.getWorks().getImgs()[0])
						+ Constant.SMALL_IMAGE_SUFFIX,
				new BitmapLoadCallBack<View>() {

					@Override
					public void onLoadCompleted(View arg0, String arg1,
							Bitmap arg2, BitmapDisplayConfig arg3,
							BitmapLoadFrom arg4) {
						holder.iv_teacher_student_bg.setImageBitmap(arg2);

					}

					@Override
					public void onLoadFailed(View arg0, String arg1,
							Drawable arg2) {
						holder.iv_teacher_student_bg
								.setImageResource(R.drawable.empty_photo);

					}
				});

		holder.tv_teacher_student_ticket.setText(cInfo.getOk_num() + "票");

		holder.tv_teacher_student_name.setText(StringUtil.FormatStrLength(
				cInfo.getUname(), 4));

		return convertView;
	}

	public class ViewHolder {
		RoundImageView iv_teacher_student_head;
		ImageView iv_teacher_student_bg;
		TextView tv_teacher_student_name, tv_teacher_student_ticket;
	}
}
