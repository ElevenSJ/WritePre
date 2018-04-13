package com.easier.writepre.adapter;

import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.entity.ClassHour;
import com.easier.writepre.entity.WordInfo;
import com.easier.writepre.ui.DiyCourseClassActivity;
import com.easier.writepre.utils.LogUtils;
import com.sj.autolayout.utils.AutoUtils;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

public class DiyCourseClassAdapter extends BaseExpandableListAdapter {
	private Context mCtx;
	private String title;
	private List<ClassHour> groupList;

	private OnChildItemClick click;

	public DiyCourseClassAdapter(Context ctx, String title) {
		this.mCtx = ctx;
		this.title = title;
	}

	public void setData(List<ClassHour> data) {
		if (data != null) {
			groupList = data;
			notifyDataSetChanged();
		}
	};

	public List<ClassHour> getData() {
		return groupList;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return groupList == null ? 0 : groupList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		// return groupList.get(groupPosition).getWords_ref() == null ? 0 :
		// groupList.get(groupPosition).getWords_ref().size();
		return 1;
	}

	@Override
	public ClassHour getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return groupList.get(groupPosition);
	}

	@Override
	public WordInfo getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return groupList.get(groupPosition).getWords_ref().get(childPosition);
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
		final GroupHolder groupHolder;
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
		final ClassHour classHour = getGroup(groupPosition);
		groupHolder.textNum.setText((groupPosition + 1)+"");
		
		groupHolder.textView.setTag(classHour);
		if (TextUtils.isEmpty(classHour.getTitle())) {
			groupHolder.textView.setText("");
		}else
			groupHolder.textView.setText(classHour.getTitle());
		groupHolder.textView.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				LogUtils.e("课件名称:"+s.toString());
				ClassHour cHour = (ClassHour) groupHolder.textView.getTag();
				cHour.setTitle(s.toString());
			}
		});
		
		return convertView;
	}

	@Override
	public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		ChildHolder childHolder;
		if (convertView == null) {
			childHolder = new ChildHolder();
			convertView = LayoutInflater.from(mCtx).inflate(R.layout.diy_course_class_child, parent, false);
			childHolder.noScrollgridview = (GridView) convertView.findViewById(R.id.noScrollgridview);
			convertView.setTag(childHolder);
			AutoUtils.autoSize(convertView);
		} else {
			childHolder = (ChildHolder) convertView.getTag();
		}
		final ImageAdapter mAdapter = new ImageAdapter(mCtx, childHolder.noScrollgridview, DiyCourseClassActivity.MAX_SIZE, 4,
				true, false);
		mAdapter.setGroupIndex(groupPosition);
		childHolder.noScrollgridview.setAdapter(mAdapter);
		if (groupList.get(groupPosition).getWords_ref().isEmpty()) {
			mAdapter.setData(null);
		} else {
			ArrayList<String> imgStrings = new ArrayList<String>();
			for (int i = 0; i < groupList.get(groupPosition).getWords_ref().size(); i++) {
				imgStrings.add(groupList.get(groupPosition).getWords_ref().get(i).getUrl());
			}
			mAdapter.setSelectedData(imgStrings);
			mAdapter.setData(imgStrings);
		}
		mAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
			@Override
			public void onClick(int position, View v) {
				if (click != null) {
					click.onChildItemClick(groupPosition, position);
				}
			}

			@Override
			public void onDelete(int position, View v) {

			}
		});
//		childHolder.noScrollgridview.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//			}
//		});
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
		private GridView noScrollgridview;
	}

	public interface OnChildItemClick {
		void onChildItemClick(int groupPosition, int childPosition);
	}

	public void setOnChildItemClick(OnChildItemClick click) {
		this.click = click;
	}
}
