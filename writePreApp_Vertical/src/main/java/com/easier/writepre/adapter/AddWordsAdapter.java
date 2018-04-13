package com.easier.writepre.adapter;

import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.entity.CourseResDir;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.SquareAllEssenceGridView;
import com.sj.autolayout.utils.AutoUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddWordsAdapter extends BaseExpandableListAdapter {
	private Context mCtx;
	private List<CourseResDir> groupList;

	private OnItemClickListener itemClick;

	private int tabGroupPosition, tabPosition;

	public AddWordsAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public void setData(List<CourseResDir> data) {
		if (data != null) {
			groupList = data;
			notifyDataSetChanged();
		}
	};

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return groupList == null ? 0 : groupList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return groupList.get(groupPosition).getChildren() == null
				|| groupList.get(groupPosition).getChildren().size() == 0 ? 0 : 1;
	}

	@Override
	public CourseResDir getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return groupList.get(groupPosition);
	}

	@Override
	public CourseResDir getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return groupList.get(groupPosition).getChildren().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		GroupHolder groupHolder;
		if (convertView == null) {
			groupHolder = new GroupHolder();
			convertView = LayoutInflater.from(mCtx).inflate(R.layout.diy_course_class_group, parent, false);
			groupHolder.textNum = (Button) convertView.findViewById(R.id.tv_num);
			groupHolder.textView = (EditText) convertView.findViewById(R.id.tv_name);
			convertView.setTag(groupHolder);
			AutoUtils.autoSize(convertView);
		} else {
			groupHolder = (GroupHolder) convertView.getTag();
		}
		groupHolder.textNum.setVisibility(View.GONE);
		groupHolder.textView.setEnabled(false);
		groupHolder.textView.setText(getGroup(groupPosition).getTitle());
		return convertView;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		ChildHolder childHolder;
		if (convertView == null) {
			childHolder = new ChildHolder();
			convertView = LayoutInflater.from(mCtx).inflate(R.layout.diy_course_class_child, parent, false);
			childHolder.noScrollgridview = (SquareAllEssenceGridView) convertView.findViewById(R.id.noScrollgridview);
			convertView.setTag(childHolder);
			AutoUtils.autoSize(convertView);
		} else {
			childHolder = (ChildHolder) convertView.getTag();
		}
		TitlePopAdapter popAdapter = new TitlePopAdapter(mCtx);
		String[] childStrings = new String[getGroup(groupPosition).getChildren().size()];
		for (int i = 0; i < getGroup(groupPosition).getChildren().size(); i++) {
			childStrings[i] = getGroup(groupPosition).getChildren().get(i).getTitle();
		}
		popAdapter.setData(childStrings);
		childHolder.noScrollgridview.setAdapter(popAdapter);
		childHolder.noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (itemClick != null) {
					itemClick.onItemClick(parent, view, groupPosition, position);
				}
			}
		});
		if (tabGroupPosition == groupPosition) {
			popAdapter.setChecked(tabPosition);
		}else{
			popAdapter.setChecked(-1);
		}
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	public class GroupHolder {
		Button textNum;
		EditText textView;
	}

	public class ChildHolder {
		private SquareAllEssenceGridView noScrollgridview;
	}

	public void setOnChildItemClick(OnItemClickListener itemClick) {
		this.itemClick = itemClick;
	}

	public void setCheckItem(int tabGroupPosition, int tabPosition) {
		this.tabGroupPosition = tabGroupPosition;
		this.tabPosition = tabPosition;
		notifyDataSetChanged();
	}
}
