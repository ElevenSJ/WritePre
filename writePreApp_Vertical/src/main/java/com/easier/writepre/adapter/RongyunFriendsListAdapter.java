package com.easier.writepre.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.entity.JointAttentionInfo;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.widget.RoundImageView;
import com.sj.autolayout.utils.AutoUtils;

/**
 * 关注适配器
 *
 * @author zhoulu
 */
public class RongyunFriendsListAdapter extends BaseAdapter {

    private Context mContext;

    private List<JointAttentionInfo> list = new ArrayList<JointAttentionInfo>();
    /**
     * 0 显示 TA的关注 1TA的粉丝
     */
    private int flag = 0;

    private String user_id;// 用户ID

    public RongyunFriendsListAdapter(Context context) {
        mContext = context;
    }

    public void clearData() {
        if (list != null) {
            list.clear();
            notifyDataSetChanged();
        }
    }

    public void setData(List<JointAttentionInfo> data) {
        if (data == null) {
            return;
        }
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(List<JointAttentionInfo> data) {
        if (data == null) {
            return;
        }
        list.addAll(data);
        notifyDataSetChanged();
    }

    public List<JointAttentionInfo> getData() {
        return list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public JointAttentionInfo getItem(int position) {
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
                    R.layout.rongyun_friends_item, parent, false);
            holder.iv_head = (RoundImageView) convertView
                    .findViewById(R.id.iv_head);

            holder.tv_uname = (TextView) convertView
                    .findViewById(R.id.tv_uname);
            holder.tv_signature = (TextView) convertView
                    .findViewById(R.id.tv_signature);
            holder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
            convertView.setTag(holder);
            AutoUtils.autoSize(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        JointAttentionInfo contentInfo = getItem(position);
        holder.tv_uname.setText(contentInfo.getUname());
        if(TextUtils.isEmpty(contentInfo.getSignature())){
            holder.tv_signature.setVisibility(View.GONE);
        }else{
            holder.tv_signature.setVisibility(View.VISIBLE);
        }
        holder.tv_signature.setText(contentInfo.getSignature());

        holder.iv_head.setImageView(StringUtil.getHeadUrl(contentInfo
                .getHead_img()));
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
        public TextView tv_uname, tv_signature, tvLetter;

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

    public void showDelDialog(Context context, final String id) {

        final Dialog dialog = new Dialog(context, R.style.loading_dialog);
        View view = LayoutInflater.from(context).inflate(
                R.layout.dialog_islogin, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_login_now);
        tv.setText("确定取消关注?");
        TextView confirm = (TextView) view.findViewById(R.id.tv_login);
        confirm.setText("确定");
        confirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_cancel).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });
        dialog.dismiss();
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

}
