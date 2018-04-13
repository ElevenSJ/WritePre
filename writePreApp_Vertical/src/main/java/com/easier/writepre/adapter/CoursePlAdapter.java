package com.easier.writepre.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.entity.CoursePLList;
import com.easier.writepre.ui.UserInfoActivity;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.widget.RoundImageView;
import com.sj.autolayout.utils.AutoUtils;
import com.sj.autolayout.utils.DateKit;

import java.util.List;

/**
 * 课程评论适配器
 */
@SuppressLint("InflateParams")
public class CoursePlAdapter extends BaseAdapter {

    private Context mContext;

    private List<CoursePLList> plList;

    public CoursePlAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<CoursePLList> data) {
        plList = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return plList == null ? 0 : plList.size();
    }


    @Override
    public CoursePLList getItem(int position) {
        return plList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.course_pl_list_item,
                    parent, false);
            holder.UserIcon = (RoundImageView) convertView.findViewById(R.id.user_icon);
            holder.user_name = (TextView) convertView.findViewById(R.id.user_name);
            holder.user_ratingBar = (RatingBar) convertView.findViewById(R.id.user_ratingBar);
            holder.user_time = (TextView) convertView.findViewById(R.id.user_time);
            holder.user_desc = (TextView) convertView.findViewById(R.id.user_desc);
            convertView.setTag(holder);
            AutoUtils.autoSize(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final CoursePLList PLDetail = getItem(position);
        holder.UserIcon.setImageView(StringUtil.getHeadUrl(PLDetail.getHead_img()));
        holder.UserIcon.setIconView(PLDetail.getIs_teacher());
        holder.user_name.setText(PLDetail.getUname());
        holder.user_ratingBar.setRating((float) PLDetail.getLevel());
        holder.user_time.setText(DateKit.timeFormat(PLDetail.getCtime()));
//        holder.user_desc.setText(TextUtils.isEmpty(PLDetail.getTitle()) ? "这家伙很懒,啥都没留下!" : PLDetail.getTitle());
        if(TextUtils.isEmpty(PLDetail.getTitle())){
            holder.user_desc.setVisibility(View.GONE);
        }else {
            holder.user_desc.setVisibility(View.VISIBLE);
            holder.user_desc.setText(PLDetail.getTitle());
        }
        holder.UserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toUserDetail(PLDetail.getUser_id());
            }
        });
        return convertView;
    }

    /**
     * 查看用户详情
     */
    public void toUserDetail(String userId) {
        Intent intent = new Intent(mContext, UserInfoActivity.class);
        intent.putExtra("user_id", userId);
        mContext.startActivity(intent);
    }

    public static class ViewHolder {
        private RoundImageView UserIcon;
        private RatingBar user_ratingBar;
        private TextView user_name;
        private TextView user_time;
        private TextView user_desc;

    }
}
