package com.easier.writepre.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.entity.PkContentInfo;
import com.easier.writepre.http.Constant;
import com.easier.writepre.response.YoukuVodgpListResponse.YoukuVodgpInfo;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.widget.RoundImageView;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;

import java.util.List;

/**
 * 大会首页(大会优秀视频作品)
 * @author zhaomaohan
 * 
 */
public class PkWorksVideoGridViewAdapter extends BaseAdapter {

	private Context context;

	private List<PkContentInfo> list;

	private LayoutInflater inflater;

	public PkWorksVideoGridViewAdapter(Context context, List<PkContentInfo> list) {
		this.inflater = LayoutInflater.from(context);
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public PkContentInfo getItem(int position) {
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
			convertView = inflater.inflate(R.layout.pk_works_video_grid_item, null);
			holder.tv_teacher_student_name = (TextView) convertView.findViewById(R.id.tv_teacher_student_name);
			holder.iv_teacher_student_head = (RoundImageView) convertView.findViewById(R.id.iv_teacher_student_head);
			holder.tv_video_name = (TextView) convertView
					.findViewById(R.id.tv_video_name);
			holder.iv_video_bg = (ImageView) convertView
					.findViewById(R.id.iv_video_bg);
			holder.iv_video_icon = (ImageView) convertView
					.findViewById(R.id.iv_video_icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final PkContentInfo yInfo = getItem(position);

		BitmapHelp.getBitmapUtils().display(holder.iv_video_bg, StringUtil.getImgeUrl( yInfo.getWorks().getImgs()[0])+ Constant.BIG_IMAGE_SUFFIX,new BitmapLoadCallBack<View>() {

			@Override
			public void onLoadCompleted(View arg0, String arg1, Bitmap arg2, BitmapDisplayConfig arg3,
					BitmapLoadFrom arg4) {
				holder.iv_video_bg.setImageBitmap(arg2);
				
			}

			@Override
			public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
				holder.iv_video_bg.setImageResource(R.drawable.empty_photo);
				
			}
		});

		holder.tv_video_name.setText(yInfo.getTitle());

		holder.iv_video_icon.bringToFront();

		holder.iv_teacher_student_head.setImageView(StringUtil.getHeadUrl(yInfo.getHead_img()));
		holder.iv_teacher_student_head.setIconView(yInfo.getIs_teacher());
		holder.tv_teacher_student_name.setText(StringUtil.FormatStrLength(yInfo.getUname(), 4));

		return convertView;
	}

	public class ViewHolder {
		ImageView iv_video_bg, iv_video_icon;
		TextView tv_video_name;
		RoundImageView iv_teacher_student_head;
		TextView tv_teacher_student_name;
	}
}
