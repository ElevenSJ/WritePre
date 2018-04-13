package com.easier.writepre.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.entity.PkInfoNew;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.widget.RoundCustomImageView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.AutoUtils;
import com.sj.autolayout.utils.DateKit;

import java.util.List;

/**
 * Created by SunJie on 17/3/14.
 */

public class PkInfoAdapter extends BaseAdapter {
    private Context ctx;

    private List<PkInfoNew> pks;

    public PkInfoAdapter(Context ctx) {
        this.ctx = ctx;
    }

    public void setData(List<PkInfoNew> infos){
        pks = infos;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return pks == null ? 0 : pks.size();
    }

    @Override
    public PkInfoNew getItem(int position) {
        return pks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(ctx).inflate(R.layout.item_pk_info,
                    parent, false);
            viewHolder.imageView = (SimpleDraweeView) convertView
                    .findViewById(R.id.iv_img);
            viewHolder.tvPkTitle = (TextView) convertView
                    .findViewById(R.id.tv_pk_title);
            viewHolder.tvPkStatus = (TextView) convertView
                    .findViewById(R.id.tv_pk_status);
            convertView.setTag(viewHolder);
            AutoUtils.autoSize(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PkInfoNew pkInfo = getItem(position);
        viewHolder.tvPkTitle.setText(pkInfo.getTitle());
        viewHolder.imageView.setImageURI(StringUtil.getImgeUrl(pkInfo.getFace_url()));
        if (DateKit.dayBetweenFormat(pkInfo.getStart_date())<=0){
            if (DateKit.dayBetweenFormat(pkInfo.getEnd_date())<0){
                viewHolder.tvPkStatus.setText("感谢参与");
                viewHolder.tvPkStatus.setBackgroundResource(R.color.text_gray);
            }else{
                viewHolder.tvPkStatus.setText("正在进行");
                viewHolder.tvPkStatus.setBackgroundResource(R.color.social_red);
            }
        }else{
            viewHolder.tvPkStatus.setText("敬请期待");
            viewHolder.tvPkStatus.setBackgroundResource(R.color.social_red);
        }
        return convertView;
    }

    class ViewHolder {
        SimpleDraweeView imageView;
        TextView tvPkTitle;
        TextView tvPkStatus;
    }

}
