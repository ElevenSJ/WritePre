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
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.entity.TecSchoolNewsInfo;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.StringUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.AutoUtils;

import java.io.File;
import java.util.List;

/**
 * 学院主页书院风采的adapter
 * Created by zhaomaohan on 2017/1/12.
 */

public class ColleageNewsAdapter extends BaseAdapter {
    private LayoutInflater inflater;

    private List<TecSchoolNewsInfo> list;

    private Context context;

    public ColleageNewsAdapter(Context context, List<TecSchoolNewsInfo> list) {
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public TecSchoolNewsInfo getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(
                    R.layout.list_item_college_news, parent, false);

            holder.tv_title = (TextView) convertView
                    .findViewById(R.id.tv_title);
            holder.tv_content = (TextView) convertView
                    .findViewById(R.id.tv_content);
            holder.imageView = (SimpleDraweeView) convertView
                    .findViewById(R.id.imageView);
            convertView.setTag(holder);
            AutoUtils.autoSize(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TecSchoolNewsInfo cInfo = getItem(position);
        holder.tv_title.setText(cInfo.getTitle());
        holder.tv_content.setText(cInfo.getDesc());
        holder.imageView.setImageURI(StringUtil.getImgeUrl(list.get(position).getPic_url()));
        return convertView;
    }

    public class ViewHolder {
        SimpleDraweeView imageView;
        TextView tv_content;
        TextView tv_title;
    }
}
