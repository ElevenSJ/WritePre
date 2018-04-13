package com.easier.writepre.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.entity.CircleInfo;
import com.easier.writepre.entity.JointAttentionInfo;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.widget.RoundImageView;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 关注适配器
 *
 * @author zhoulu
 */
public class RongyunGroupListAdapter extends BaseAdapter {

    private Context mContext;

    private List<CircleInfo> list = new ArrayList<CircleInfo>();
    /**
     * 0 显示 TA的关注 1TA的粉丝
     */
    private int flag = 0;

    private String user_id;// 用户ID

    public RongyunGroupListAdapter(Context context) {
        mContext = context;
    }

    public void clearData() {
        if (list != null) {
            list.clear();
            notifyDataSetChanged();
        }
    }

    public void setData(List<CircleInfo> data) {
        if (data == null) {
            return;
        }
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(List<CircleInfo> data) {
        if (data == null) {
            return;
        }
        list.addAll(data);
        notifyDataSetChanged();
    }

    public List<CircleInfo> getData() {
        return list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public CircleInfo getItem(int position) {
        return list == null ? null : list.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.rongyun_group_item, parent, false);
            holder.iv_head = (RoundImageView) convertView
                    .findViewById(R.id.iv_head);
            holder.TvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.Tvtag = (TextView) convertView.findViewById(R.id.tv_tag);
            holder.Tvtype = (ImageView) convertView.findViewById(R.id.tv_type);
//            holder.LayoutNotifyNum = (LinearLayout) convertView.findViewById(R.id.layout_notify_num);
//            holder.TvNotifyNum = (TextView) convertView.findViewById(R.id.tv_notify_num);
            holder.TvNum = (TextView) convertView.findViewById(R.id.tv_num);
            holder.TvPostNum = (TextView) convertView.findViewById(R.id.tv_post_num);
            holder.TvIsOpen = (TextView) convertView.findViewById(R.id.tv_is_open);
            holder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
            convertView.setTag(holder);
            AutoUtils.autoSize(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CircleInfo contentInfo = getItem(position);
        holder.TvName.setText(contentInfo.getName());

        holder.iv_head.setImageView(StringUtil.getHeadUrl(contentInfo
                .getFace_url()));

        if (contentInfo.getUser_id().equals(SPUtils.instance().getLoginEntity().get_id())) {
            holder.Tvtype.setVisibility(View.VISIBLE);
        } else {
            holder.Tvtype.setVisibility(View.GONE);
        }
        if (contentInfo.getType().equals("0")) {
            holder.Tvtag.setText("学习");
        } else if (contentInfo.getType().equals("2")) {
            holder.Tvtag.setText("班级");
        } else {
            holder.Tvtag.setText("交友");
        }

        holder.TvName.setText(contentInfo.getName());

        holder.TvNum.setText("(" + contentInfo.getNum() + "人)");
        holder.TvPostNum.setText(contentInfo.getPost_num() + "");
        String openStr = contentInfo.getIs_open().equals("0") ? "私密" : "公开";
        holder.TvIsOpen.setText(openStr);
        // 根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            holder.tvLetter.setVisibility(View.VISIBLE);
            holder.tvLetter.setText(contentInfo.getSortLetters());
        } else {
            holder.tvLetter.setVisibility(View.GONE);
        }

        return convertView;
    }

    public class ViewHolder {
        private RoundImageView iv_head;
        public TextView tvLetter;
        private TextView TvName;
        private TextView Tvtag;
        private ImageView Tvtype;
        private TextView TvNum;
        private LinearLayout LayoutNotifyNum;
        private TextView TvNotifyNum;
        private TextView TvPostNum;
        private TextView TvIsOpen;

    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        if (list != null && !list.isEmpty())
            return list.get(position).getSortLetters().charAt(0);
        return 0;
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }


}
