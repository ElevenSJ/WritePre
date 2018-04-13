package com.easier.writepre.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.http.Constant;
import com.easier.writepre.response.VShowUserResponse.UserInfo;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.StringUtil;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.AutoUtils;

import java.util.List;

public class VShowUserGridViewAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<UserInfo> list;

    private Context context;

    public VShowUserGridViewAdapter(Context context, List<UserInfo> list) {
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public UserInfo getItem(int position) {
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
                    R.layout.grid_item_micro_exhibition, parent, false);
            holder.tv_name = (TextView) convertView
                    .findViewById(R.id.tv_name);
            holder.tv_title = (TextView) convertView
                    .findViewById(R.id.tv_title);
            holder.iv_user_url = (ImageView) convertView
                    .findViewById(R.id.iv_user_url);
            convertView.setTag(holder);
            AutoUtils.autoSize(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        UserInfo cInfo = getItem(position);

        BitmapHelp.getBitmapUtils().display(
                holder.iv_user_url,
                StringUtil.getImgeUrl(cInfo.getPhoto_url())
                        + Constant.VSHOW_IMAGE_SUFFIX,
                new BitmapLoadCallBack<View>() {

                    @Override
                    public void onLoadCompleted(View arg0, String arg1,
                                                Bitmap arg2, BitmapDisplayConfig arg3,
                                                BitmapLoadFrom arg4) {
                        holder.iv_user_url.setImageBitmap(arg2);

                    }

                    @Override
                    public void onLoadFailed(View arg0, String arg1,
                                             Drawable arg2) {
                        holder.iv_user_url
                                .setImageResource(R.drawable.empty_photo);

                    }
                });

        holder.tv_title.setText(cInfo.getTitle());
        holder.tv_name.setText(cInfo.getReal_name());
        return convertView;
    }

    public class ViewHolder {
        ImageView iv_user_url;
        TextView tv_name, tv_title;
    }
}
