package com.easier.writepre.widget;

import com.easier.writepre.R;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.PopupWindow;
import android.widget.TextView;

public class WordsPopupWindow extends PopupWindow {

	private TextView closeTxt;
	private PullToRefreshExpandableListView listView;
	private View mMenuView;

	// private Context context;

	private OnClickListener onclick;

	public WordsPopupWindow(Context context, int layoutId) {
		super(context);
		// this.context = context;
		mMenuView = LayoutInflater.from(context).inflate(layoutId, null);
		closeTxt = (TextView) mMenuView.findViewById(R.id.top_arrow);
		listView = (PullToRefreshExpandableListView) mMenuView
				.findViewById(R.id.listview);
		this.setContentView(mMenuView);
		this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
		this.setFocusable(true);
		ColorDrawable dw = new ColorDrawable(0x00000000);
		this.setBackgroundDrawable(dw);
		this.setOutsideTouchable(true);
		closeTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onclick != null) {
					onclick.onClick(v);
				}
			}
		});
	}

	public void setLayoutParams(int width, int height) {
		this.setWidth(width);
		this.setHeight(height);
	}

	public void setOnItemClick(OnItemClickListener itemClick) {
		if (listView != null) {
			listView.getRefreshableView().setOnItemClickListener(itemClick);
		}
	}

	public void setOnChildClickListener(OnChildClickListener listener) {
		if (listView != null) {
			listView.getRefreshableView().setOnChildClickListener(listener);
		}
	}

	public void setOnClick(OnClickListener click) {
		this.onclick = click;
	}

	public void setAdapater(BaseExpandableListAdapter adapter) {
		if (listView != null) {
			listView.getRefreshableView().setAdapter(adapter);
			for (int i = 0; i < adapter.getGroupCount(); i++) {
				listView.getRefreshableView().expandGroup(i);
			}
		}
	}

	public void setItemCheck(int position, boolean value) {
		if (listView != null) {
			listView.getRefreshableView().setItemChecked(position, value);
		}
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}
}
