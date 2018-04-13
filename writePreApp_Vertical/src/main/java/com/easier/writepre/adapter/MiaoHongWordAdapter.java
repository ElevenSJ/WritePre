package com.easier.writepre.adapter;

import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.entity.CourseContentInfo;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.StringUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sj.autolayout.utils.AutoUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MiaoHongWordAdapter extends RecyclerView.Adapter<MiaoHongWordAdapter.ItemViewHolder> {

	private List<CourseContentInfo> mDatas;
	private LayoutInflater mInflater;
	private OnItemClickListener mOnItemClickListener;
	private int beforeIndex = -1;
	private int currIndex = 0;

	public MiaoHongWordAdapter(Context context, List<CourseContentInfo> mDatas) {
		this.mDatas = mDatas;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getItemCount() {
		return mDatas.size();
	}

	@SuppressLint("NewApi")
	@Override
	public void onBindViewHolder(final ItemViewHolder itemViewHolder, final int i) {
		if (currIndex == i) {
			itemViewHolder.iv_ge.setVisibility(View.VISIBLE);
		} else {
			itemViewHolder.iv_ge.setVisibility(View.GONE);
		}
		itemViewHolder.iv_icon.setImageURI(StringUtil.getImgeUrl(mDatas.get(i).getPic_url_min()));
		if (mOnItemClickListener != null) {
			/**
			 * 这里加了判断，itemViewHolder.itemView.hasOnClickListeners()
			 * 目的是减少对象的创建，如果已经为view设置了click监听事件,就不用重复设置了
			 * 不然每次调用onBindViewHolder方法，都会创建两个监听事件对象，增加了内存的开销
			 */
			if (!itemViewHolder.itemView.hasOnClickListeners()) {
				itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int pos = itemViewHolder.getPosition();
						mOnItemClickListener.onItemClick(v, pos);
						setSelectedPosition(pos);
					}
				});
				itemViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						int pos = itemViewHolder.getPosition();
						mOnItemClickListener.onItemLongClick(v, pos);
						return true;
					}
				});
			}
		}
	}

	@Override
	public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View v = mInflater.inflate(R.layout.adapter_miaohong_item, viewGroup, false);
		ItemViewHolder holder = new ItemViewHolder(v);
		AutoUtils.autoSize(v);
		return holder;
	}

	public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
		this.mOnItemClickListener = mOnItemClickListener;
	}

	/**
	 * 处理item的点击事件和长按事件
	 */
	public interface OnItemClickListener {
		public void onItemClick(View view, int position);

		public void onItemLongClick(View view, int position);
	}

	class ItemViewHolder extends RecyclerView.ViewHolder {

		public SimpleDraweeView iv_icon;
		public ImageView iv_ge;

		public ItemViewHolder(View itemView) {
			super(itemView);
			iv_icon = (SimpleDraweeView) itemView.findViewById(R.id.iv_icon);
			iv_ge = (ImageView) itemView.findViewById(R.id.iv_ge);
		}
	}

	public void setSelectedPosition(int currIndex) {
		this.currIndex = currIndex;
		if (beforeIndex!=-1&&beforeIndex!=currIndex){
			notifyItemRangeChanged(beforeIndex,getItemCount());
		}
		this.beforeIndex = currIndex;
		notifyItemRangeChanged(currIndex,getItemCount());
	}

}