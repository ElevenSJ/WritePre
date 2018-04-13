package com.easier.writepre.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.entity.CollegeMenuInfo;
import com.sj.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * 学院主页gridview菜单的adapter
 * Created by zhaomaohan on 2017/1/11.
 */

public class ColleageMenuAdapter extends BaseAdapter {
    private LayoutInflater inflater;

    private List<CollegeMenuInfo> list;

    private Context context;

    public ColleageMenuAdapter(Context context, List<CollegeMenuInfo> list) {
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CollegeMenuInfo getItem(int position) {
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
                    R.layout.grid_item_college_menu, parent, false);

            holder.textView = (TextView) convertView
                    .findViewById(R.id.textView);
            holder.imageView = (ImageView) convertView
                    .findViewById(R.id.imageView);
            convertView.setTag(holder);
            AutoUtils.autoSize(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CollegeMenuInfo cInfo = getItem(position);
        holder.textView.setText(cInfo.getText());
        holder.imageView.setImageResource(list.get(position).getImage());
        return convertView;
    }

    public class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
