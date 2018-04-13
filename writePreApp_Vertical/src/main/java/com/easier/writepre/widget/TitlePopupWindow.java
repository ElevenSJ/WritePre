package com.easier.writepre.widget;

import com.easier.writepre.R;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;

public class TitlePopupWindow extends PopupWindow {

	private SquareAllEssenceGridView gridview;
	private View mMenuView;

	private Context context;

	public TitlePopupWindow(Context context,int layoutId) {
		super(context);
		this.context = context;
		mMenuView = LayoutInflater.from(context).inflate(layoutId, null);
		gridview = (SquareAllEssenceGridView) mMenuView.findViewById(R.id.gridview);
		this.setContentView(mMenuView);
		this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		ColorDrawable dw = new ColorDrawable(0x00000000);
		this.setBackgroundDrawable(dw);
		this.setOutsideTouchable(true);
	}

	public void setLayoutParams(int width,int height){
		this.setWidth(width);
		this.setHeight(height);
	}
	public void setOnItemClick(OnItemClickListener itemClick) {
		if (gridview != null) {
			gridview.setOnItemClickListener(itemClick);
		}
	}

	public void setAdapater(BaseAdapter adapter) {
		if (gridview != null) {
			gridview.setAdapter(adapter);
		}
	}

	public void setItemCheck(int position, boolean value) {
		if (gridview != null) {
			gridview.setItemChecked(position, value);
		}
	}
	public int getItemCheck() {
		int item = 0;
		if (gridview != null) {
			item = gridview.getCheckedItemPosition();
		}
		return item;
	}
	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
	}
}
