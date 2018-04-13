package com.easier.writepre.adapter;

import android.content.Context;
import android.content.res.ObbInfo;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.entity.RecommendInfo;
import com.easier.writepre.entity.SearchInfo;
import com.easier.writepre.utils.DisplayUtil;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SpanStringUtil;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.widget.RoundImageView;
import com.sj.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索适配器
 */
public class SearchAdapter extends BaseAdapter {
    private Context mCtx;
    private ArrayList<SearchInfo> listSearchData = new ArrayList<>();
    public String keyWord = "";

    public SearchAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public void setData(ArrayList<SearchInfo> listData) {
        if (listData != null) {
            this.listSearchData.clear();
            this.listSearchData.addAll(listData);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        if (listSearchData != null) {
            this.listSearchData.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return listSearchData.size();
    }

    @Override
    public Object getItem(int position) {
        return listSearchData.size() > 0 ? listSearchData.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        //筛选布局
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mCtx).inflate(R.layout.search_item, parent, false);
            holder.iv_header_icon = (RoundImageView) convertView.findViewById(R.id.iv_img_head);
            holder.tv_uname = (TextView) convertView.findViewById(R.id.tv_uname);
            holder.tv_signature = (TextView) convertView.findViewById(R.id.tv_signature);
            convertView.setTag(holder);
            AutoUtils.autoSize(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SearchInfo searchInfo = (SearchInfo) getItem(position);
        if (searchInfo != null) {
            holder.tv_signature.setText(searchInfo.getSignature());
            if (TextUtils.isEmpty(keyWord)) {
                holder.tv_uname.setText(searchInfo.getUname());
            } else {
                holder.tv_uname.setText(SpanStringUtil.matcherSearchTitle(mCtx.getResources().getColor(R.color.text_color_red_1), searchInfo.getUname(), keyWord));
            }
            holder.iv_header_icon.setImageView(StringUtil.getHeadUrl(searchInfo.getHead_img()));
            holder.iv_header_icon.setIconView(searchInfo.getIs_teacher());
        }
        return convertView;
    }

    public class ViewHolder {
        RoundImageView iv_header_icon;
        TextView tv_uname, tv_signature;
    }
}
