package com.easier.writepre.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.entity.CourseCategoryInfo;
import com.easier.writepre.response.CourseContentResponse.CourseContentResponseBody;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.sj.autolayout.utils.AutoUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class AddwordsCourseAdapter extends BaseExpandableListAdapter {
	private Context mCtx;
	private List<CourseCategoryInfo> groupList;

	private HashMap<String, CourseContentResponseBody> childData = new HashMap<String, CourseContentResponseBody>();

	private int MAX_SIZE = 15;

	public AddwordsCourseAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public void setData(List<CourseCategoryInfo> data) {
		if (data != null) {
			groupList = data;
			notifyDataSetInvalidated();
		}
	}

	public void setChildData(Integer groupPosition, Integer childPosition, CourseContentResponseBody data) {
		childData.put(groupPosition + "" + childPosition, data);
		notifyDataSetInvalidated();
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return groupList == null ? 0 : groupList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return groupList.get(groupPosition).getChild().size();
	}

	@Override
	public CourseCategoryInfo getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return groupList.get(groupPosition);
	}

	@Override
	public CourseCategoryInfo getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return groupList.get(groupPosition).getChild().get(childPosition);
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
			groupHolder.textView = (TextView) convertView.findViewById(R.id.tv_name);
			convertView.setTag(groupHolder);
			AutoUtils.autoSize(convertView);
		} else {
			groupHolder = (GroupHolder) convertView.getTag();
		}
		groupHolder.textView.setText(getGroup(groupPosition).getTitle());
		return convertView;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		ChildHolder childHolder;
		if (convertView == null) {
			childHolder = new ChildHolder();
			convertView = LayoutInflater.from(mCtx).inflate(R.layout.add_words_child_item, parent, false);
			childHolder.textView = (TextView) convertView.findViewById(R.id.tv_name);
			childHolder.noScrollgridview = (GridView) convertView.findViewById(R.id.noScrollgridview);
			convertView.setTag(childHolder);
			AutoUtils.autoSize(convertView);
		} else {
			childHolder = (ChildHolder) convertView.getTag();
		}
		childHolder.textView.setText(getChild(groupPosition, childPosition).getTitle());
		CourseContentResponseBody data = childData.get(groupPosition + "" + childPosition);
		if (data != null) {
			ArrayList<String> imgStrings = new ArrayList<String>();
			for (int i = 0; i < data.getWords().size(); i++) {
				imgStrings.add(StringUtil.getImgeUrl(data.getWords().get(i).getPic_url()));
			}
			ImageAdapter mAdapter = new ImageAdapter(mCtx, childHolder.noScrollgridview, MAX_SIZE, 4, false, false);
			mAdapter.setData(imgStrings);
			childHolder.noScrollgridview.setAdapter(mAdapter);

			mAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {

				@Override
				public void onClick(int position, View v) {
					ToastUtil.show("选择" + groupPosition + "" + childPosition);
				}

				@Override
				public void onDelete(int position, View v) {

				}
			});
		}
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	public class GroupHolder {
		TextView textView;
	}

	public class ChildHolder {
		public TextView textView;
		private GridView noScrollgridview;
	}

}
