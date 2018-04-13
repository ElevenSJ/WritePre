package com.easier.writepre.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.http.Constant;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.widget.SquareAllEssenceGridView;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.AutoUtils;

/**
 * 广场（全部、精华图片）
 *
 * @author sunjie
 */
public class SquareAllEssenceGridAdapter extends BaseAdapter {

    private String[] files;

    private LayoutInflater mLayoutInflater;

    private String image_suffix;

    private Context context;

    private SquareAllEssenceGridView gridView;
    private ViewGroup.LayoutParams glayoutParams;

    @SuppressWarnings("deprecation")
    public SquareAllEssenceGridAdapter(SquareAllEssenceGridView gView, Context context, String[] data) {
        this.gridView = gView;
        this.context = context;
        this.files = data;
        this.mLayoutInflater = LayoutInflater.from(context);
        initGridViewParams();
    }

    private void initGridViewParams() {
        if (files == null) {
            return;
        }
        glayoutParams = (ViewGroup.LayoutParams) gridView.getLayoutParams();
        image_suffix = Constant.SMALL_IMAGE_SUFFIX;
        if (files.length == 1) {
            glayoutParams.width = LayoutParams.WRAP_CONTENT;
//            image_suffix = Constant.BIG_IMAGE_SUFFIX;
        } else if (files.length == 2) {
            glayoutParams.width = WritePreApp.getApp().getWidth(1f) / 8 * 5;
//            image_suffix = Constant.MIDDLE_IMAGE_SUFFIX;
        } else {
            glayoutParams.width = WritePreApp.getApp().getWidth(1f) / 4 * 3;
//            image_suffix = Constant.SMALL_IMAGE_SUFFIX;
        }
        glayoutParams.height = LayoutParams.WRAP_CONTENT;
        gridView.setLayoutParams(glayoutParams);
    }

    @Override
    public int getCount() {
        return files == null ? 0 : files.length;
    }

    @Override
    public String getItem(int position) {
        return files[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SquareAllEssenceGridViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new SquareAllEssenceGridViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.gridview_item, parent, false);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.album_image);
            convertView.setTag(viewHolder);
            AutoUtils.autoSize(convertView);
        } else {
            viewHolder = (SquareAllEssenceGridViewHolder) convertView.getTag();
            viewHolder.imageView.setVisibility(View.INVISIBLE);
            viewHolder.imageView.setImageResource(R.drawable.gray);
        }
        LayoutParams layoutParams;
        if (files.length > 2) {
            layoutParams = new RelativeLayout.LayoutParams((WritePreApp.getApp().getWidth(1)) / 3, (WritePreApp.getApp().getWidth(1)) / 3);
        } else {
            layoutParams = new RelativeLayout.LayoutParams((WritePreApp.getApp().getWidth(1)) / 2, (WritePreApp.getApp().getWidth(1)) / 2);
        }
        viewHolder.imageView.setLayoutParams(layoutParams);
        viewHolder.imageView.setVisibility(View.VISIBLE);
        BitmapHelp.getBitmapUtils().display(viewHolder.imageView, StringUtil.getImgeUrl(getItem(position)) + image_suffix, new BitmapLoadCallBack<View>() {

            @Override
            public void onLoadCompleted(View arg0, String arg1, Bitmap arg2, BitmapDisplayConfig arg3,
                                        BitmapLoadFrom arg4) {
                if (viewHolder.imageView.getVisibility() == View.VISIBLE) {
                    viewHolder.imageView.setImageBitmap(arg2);
                }
            }

            @Override
            public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
                if (viewHolder.imageView.getVisibility() == View.VISIBLE) {
                    viewHolder.imageView.setImageResource(R.drawable.empty_photo);
                }

            }
        });
        return convertView;
    }

    public void setData(String[] img_url) {
        this.files = img_url;
        initGridViewParams();
        notifyDataSetChanged();
    }

    private static class SquareAllEssenceGridViewHolder {
        ImageView imageView;
    }
}
