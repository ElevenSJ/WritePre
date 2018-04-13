package com.easier.writepre.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.response.ResPeriodResponse.ResPeriodInfo;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.StringUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.AutoUtils;

@SuppressLint("InflateParams")
public class FindClassicWorksListAdapter extends BaseAdapter {

    private Context context;
    private List<ResPeriodInfo> list;
    private LayoutInflater inflater;

    public FindClassicWorksListAdapter(Context context, List<ResPeriodInfo> list) {
        super();
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public ResPeriodInfo getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.find_classic_works_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
//            viewHolder.icon_point_bottom = (ImageView) convertView
//                    .findViewById(R.id.icon_point_bottom);
//            viewHolder.icon_point_top = (ImageView) convertView
//                    .findViewById(R.id.icon_point_top);
            viewHolder.img_period_url = (SimpleDraweeView) convertView
                    .findViewById(R.id.img_period_url);
            convertView.setTag(viewHolder);
            AutoUtils.autoSize(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final ResPeriodInfo rpInfo = getItem(position);

        if (rpInfo != null) {
            viewHolder.title.setText(TextUtils.isEmpty(rpInfo.getText()) ? "" : "「" + rpInfo.getText() + "」");
            viewHolder.text.setText(rpInfo.getDesc());
            viewHolder.img_period_url.setImageURI(StringUtil.getImgeUrl(rpInfo.getIcon_url()));
        }

//        if (position == 0) {
//            viewHolder.icon_point_top.setVisibility(View.VISIBLE);
//        } else
//            viewHolder.icon_point_top.setVisibility(View.GONE);
//
//        if (position == list.size() - 1) {
//            viewHolder.icon_point_bottom.setVisibility(View.VISIBLE);
//        } else
//            viewHolder.icon_point_bottom.setVisibility(View.GONE);

        return convertView;

    }

    static class ViewHolder {
        private TextView title, text;
        private SimpleDraweeView img_period_url;  //icon_point_bottom, icon_point_top
    }
}
