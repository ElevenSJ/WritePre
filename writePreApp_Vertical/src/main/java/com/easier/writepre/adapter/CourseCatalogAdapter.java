package com.easier.writepre.adapter;

import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.entity.BaseCourseCategory;
import com.easier.writepre.entity.BaseCourseCategoryInfo;
import com.easier.writepre.entity.CourseCategoryInfo;
import com.easier.writepre.entity.CourseCategoryList;
import com.easier.writepre.entity.VodCourseCategoryInfo;
import com.easier.writepre.entity.VodCourseCategoryList;
import com.easier.writepre.ui.CourseCatalogActivityNew;
import com.sj.autolayout.utils.AutoUtils;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CourseCatalogAdapter extends BaseExpandableListAdapter {
    private Context mCtx;
    private List<BaseCourseCategoryInfo> groupList= new ArrayList<>();

    public CourseCatalogAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public void setData(BaseCourseCategory cata) {
        if (cata == null) {
            return;
        }
        if (cata instanceof CourseCategoryList) {
            if (((CourseCategoryList) cata).getList()!=null){
                groupList.addAll(((CourseCategoryList) cata).getList());
            }
        } else if (cata instanceof VodCourseCategoryList) {
            if (((VodCourseCategoryList) cata).getList()!=null){
                groupList.addAll(((VodCourseCategoryList) cata).getList());
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
        return groupList == null ? 0 : groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // TODO Auto-generated method stub
        if (groupList.get(groupPosition) instanceof CourseCategoryInfo){
            return ((CourseCategoryInfo)groupList.get(groupPosition)).getChild() == null ? 0 : ((CourseCategoryInfo)groupList.get(groupPosition)).getChild().size();
        }else{
            return 0;
        }

    }

    @Override
    public BaseCourseCategoryInfo getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return groupList.get(groupPosition);
    }

    @Override
    public CourseCategoryInfo getChild(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        if (groupList.get(groupPosition) instanceof CourseCategoryInfo){
            return ((CourseCategoryInfo)groupList.get(groupPosition)).getChild().get(childPosition);
        }
        return null;
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
        if (getGroup(groupPosition) instanceof CourseCategoryInfo){
            if (!TextUtils.isEmpty(((CourseCategoryInfo)getGroup(groupPosition)).getIs_end())
                    && ((CourseCategoryInfo)getGroup(groupPosition)).getIs_end().equals("1")) {
                groupHolder.isReadImg.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(((CourseCategoryInfo)getGroup(groupPosition)).getIs_read())
                        || ((CourseCategoryInfo)getGroup(groupPosition)).getIs_read().equals("0")) {
                    groupHolder.isReadImg.setImageResource(R.drawable.id_read_no);
                } else {
                    groupHolder.isReadImg.setImageResource(R.drawable.id_read_yes);
                }
            } else {
                groupHolder.isReadImg.setVisibility(View.INVISIBLE);
            }
        }else{
            groupHolder.isReadImg.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        ChildHolder childHolder;
        if (convertView == null) {
            childHolder = new ChildHolder();
            convertView = LayoutInflater.from(mCtx).inflate(R.layout.course_catalog_child_item, parent, false);
            childHolder.isReadImg = (ImageView) convertView.findViewById(R.id.isRead_img);
            childHolder.textView = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(childHolder);
            AutoUtils.autoSize(convertView);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }
        childHolder.textView.setText(getChild(groupPosition, childPosition).getTitle());
        if (getGroup(groupPosition) instanceof CourseCategoryInfo) {
            if (!TextUtils.isEmpty(getChild(groupPosition, childPosition).getIs_end())
                    && getChild(groupPosition, childPosition).getIs_end().equals("1")) {
                childHolder.isReadImg.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(getChild(groupPosition, childPosition).getIs_read())
                        || getChild(groupPosition, childPosition).getIs_read().equals("0")) {
                    childHolder.isReadImg.setImageResource(R.drawable.id_read_no);
                } else {
                    childHolder.isReadImg.setImageResource(R.drawable.id_read_yes);
                }
            } else {
                childHolder.isReadImg.setVisibility(View.INVISIBLE);
            }
        }
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
        ImageView isReadImg;
        TextView textView;
    }

}
