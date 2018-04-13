package com.easier.writepre.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.entity.CourseContentInfo;
import com.easier.writepre.entity.DIYCourseDetailBody;
import com.easier.writepre.ui.MiaoHongActivity;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.widget.SquareAllEssenceGridView;
import com.sj.autolayout.utils.AutoUtils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DiyCourseCatalogAdapter extends BaseExpandableListAdapter {
    private Context mCtx;
    private List<DIYCourseDetailBody> groupList;
    private String courseName;

    public DiyCourseCatalogAdapter(Context ctx, String courseName) {
        this.mCtx = ctx;
        this.courseName = courseName;
    }

    public void setData(List<DIYCourseDetailBody> data) {
        if (data != null) {
            groupList = data;
            notifyDataSetChanged();
        }
    }

    ;

    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
        return groupList == null ? 0 : groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // TODO Auto-generated method stub
        return groupList.get(groupPosition).getWords_ref() == null
                || groupList.get(groupPosition).getWords_ref().isEmpty() ? 0 : 1;
    }

    @Override
    public DIYCourseDetailBody getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return groupList.get(groupPosition);
    }

    @Override
    public CourseContentInfo getChild(int groupPosition, int childPosition) {
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
        GroupHolder groupHolder;
        if (convertView == null) {
            groupHolder = new GroupHolder();
            convertView = LayoutInflater.from(mCtx).inflate(R.layout.course_catalog_group_item, parent, false);
            groupHolder.isReadImg = (ImageView) convertView.findViewById(R.id.isRead_img);
            groupHolder.textView = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(groupHolder);
            AutoUtils.autoSize(convertView);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        groupHolder.textView.setText(getGroup(groupPosition).getTitle());
        if (!TextUtils.isEmpty(getGroup(groupPosition).getIs_read())
                && getGroup(groupPosition).getIs_read().equals("0")) {
            groupHolder.isReadImg.setImageResource(R.drawable.id_read_no);
        } else {
            groupHolder.isReadImg.setImageResource(R.drawable.id_read_yes);
        }
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
        ArrayList<String> imgStrings = new ArrayList<String>();
        for (int i = 0; i < groupList.get(groupPosition).getWords_ref().size(); i++) {
            imgStrings.add(StringUtil.getImgeUrl(groupList.get(groupPosition).getWords_ref().get(i).getPic_url()));
        }
        ImageAdapter mAdapter = new ImageAdapter(mCtx, childHolder.noScrollgridview, imgStrings.size(), 4, false,
                false);
        mAdapter.setData(imgStrings);
        childHolder.noScrollgridview.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, View v) {
                DIYCourseDetailBody dIYCourseDetailBody = getGroup(groupPosition);
                // 跳转到描红页面
                Intent intent = new Intent(mCtx, MiaoHongActivity.class);
                intent.putExtra("courseName", courseName);
                // 当前点击的范字id
                String fz_id = dIYCourseDetailBody.getWords_ref().get(position).get_id();
                intent.putExtra("fz_id", fz_id);
                intent.putExtra("wordList", (Serializable) dIYCourseDetailBody.getWords_ref());
                mCtx.startActivity(intent);
                if (getGroup(groupPosition).getIs_read().equals("0")) {
                    getGroup(groupPosition).setIs_read("1");
                }
            }

            @Override
            public void onDelete(int position, View v) {

            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return true;
    }

    public class GroupHolder {
        ImageView isReadImg;
        TextView textView;
    }

    public class ChildHolder {
        private SquareAllEssenceGridView noScrollgridview;
    }

}
