package com.easier.writepre.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.entity.GroupNoticeInfo;
import com.easier.writepre.entity.GroupNoticeLookedMemberInfo;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.http.WritePreListener;
import com.easier.writepre.param.GroupNoticePostDelParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.GroupNoticePostDelResponse;
import com.easier.writepre.ui.SquareImageLookActivity;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.RoundImageView;
import com.easier.writepre.widget.SquareAllEssenceGridView;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.AutoUtils;
import com.sj.autolayout.utils.DateKit;

import java.util.ArrayList;
import java.util.List;

/**
 * 群公告签到成员适配器
 *
 * @author zhoulu
 */
@SuppressLint("InflateParams")
public class GroupNoticeLookedMemberListAdapter extends BaseAdapter {

    private Context mContext;
    private List<GroupNoticeLookedMemberInfo> data = new ArrayList<>();

    public GroupNoticeLookedMemberListAdapter(Context context) {
        mContext = context;
    }

    public String getLastDataId() {
        if (data.isEmpty()) {
            return null;
        }
        return data.get(data.size() - 1).get_id();
    }

    public void setData(List<GroupNoticeLookedMemberInfo> data) {
        if (data == null) {
            return;
        }
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private int index = -1;


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_noticelooked, parent, false);
            holder.iv_head = (RoundImageView) convertView.findViewById(R.id.iv_head);
            holder.tv_uname = (TextView) convertView.findViewById(R.id.tv_uname);
            holder.tv_tag = (TextView) convertView.findViewById(R.id.tv_tag);
            convertView.setTag(holder);
            AutoUtils.autoSize(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final GroupNoticeLookedMemberInfo groupNoticeLookedMemberInfo = (GroupNoticeLookedMemberInfo) getItem(position);
        holder.iv_head.setImageView(StringUtil.getHeadUrl(groupNoticeLookedMemberInfo.getHead_img()));
        holder.iv_head.setIconView(groupNoticeLookedMemberInfo.getIs_teacher());
        String userName = "";
        if (!TextUtils.isEmpty(groupNoticeLookedMemberInfo.getCircle_uname())) {
            userName = groupNoticeLookedMemberInfo.getCircle_uname();
        } else {
            userName = groupNoticeLookedMemberInfo.getUname();
        }
        holder.tv_uname.setText(userName); // 昵称
        if (TextUtils.equals("0", groupNoticeLookedMemberInfo.getRole())) {
            holder.tv_tag.setVisibility(View.GONE);
        } else {
            holder.tv_tag.setVisibility(View.GONE);
        }
        return convertView;
    }


    public class ViewHolder {
        public TextView tv_uname, tv_tag;
        private RoundImageView iv_head;
    }

}
