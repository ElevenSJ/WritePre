package com.easier.writepre.adapter;

import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.entity.CircleInfo;
import com.easier.writepre.ui.CircleDetailActivity;
import com.easier.writepre.ui.CircleListActivity;
import com.easier.writepre.ui.CircleMsgListActivity;
import com.easier.writepre.ui.UserInfoActivity;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.sj.autolayout.utils.AutoUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 发现圈子
 */
@SuppressLint("InflateParams")
public class CircleSearchListAdapter extends BaseAdapter {

    private Context mContext;

    private List<CircleInfo> list = new ArrayList<CircleInfo>();

    private LayoutInflater mInflater;

    public CircleSearchListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    public void setData(List<CircleInfo> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CircleInfo getItem(int position) {
        return list.get(position);
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
            convertView = mInflater.inflate(R.layout.circle_search_item, parent, false);
            holder.ImgIcon = (ImageView) convertView.findViewById(R.id.img_icon);
            holder.TvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.LayoutTag = (LinearLayout) convertView.findViewById(R.id.layout_tag);
            holder.Tvtag = (TextView) convertView.findViewById(R.id.tv_tag);
            holder.TvNum = (TextView) convertView.findViewById(R.id.tv_num);
            holder.TvPostNum = (TextView) convertView.findViewById(R.id.tv_post_num);
            holder.TvIsOpen = (TextView) convertView.findViewById(R.id.tv_is_open);
            convertView.setTag(holder);
            AutoUtils.autoSize(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final CircleInfo circleInfo = getItem(position);

        BitmapHelp.getBitmapUtils().display(holder.ImgIcon, StringUtil.getImgeUrl(circleInfo.getFace_url()));

        holder.Tvtag.setVisibility(View.GONE);
        holder.LayoutTag.setVisibility(View.GONE);
        holder.TvName.setText(circleInfo.getName());

        holder.TvNum.setText(circleInfo.getNum() + "");
        holder.TvPostNum.setText(circleInfo.getPost_num() + "");
        String openStr = circleInfo.getIs_open().equals("0") ? "私密" : "公开";
        holder.TvIsOpen.setText(openStr);
        if (circleInfo.getIs_open().equals("0")) {
            holder.TvIsOpen.setVisibility(View.INVISIBLE);
        } else {
            holder.TvIsOpen.setVisibility(View.VISIBLE);
        }
        holder.ImgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toCircleDetail(position);
            }
        });
        return convertView;
    }

    /**
     * 查看圈子详情
     */
    public void toCircleDetail(int position) {
        CircleInfo CircleInfo = getItem(position);
        Intent intent = new Intent();
        // 私有查看圈子介绍，公开查看圈子动态
        intent.setClass(mContext, CircleDetailActivity.class);
        intent.putExtra("circle_id", CircleInfo.get_id()); // 圈子id
        intent.putExtra("circle_name", CircleInfo.getName()); // 圈子名称
        if (CircleInfo.getUser_id().equals(SPUtils.instance().getLoginEntity().get_id())) {
            intent.putExtra("is_role", true);
        } else {
            intent.putExtra("is_role", false);
        }
        mContext.startActivity(intent);
    }

    public static class ViewHolder {
        private ImageView ImgIcon;
        private TextView TvName;
        private TextView Tvtag;
        private LinearLayout LayoutTag;
        private TextView TvNum;
        private TextView TvPostNum;
        private TextView TvIsOpen;

    }
}
