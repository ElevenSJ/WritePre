package com.easier.writepre.adapter;

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
import com.easier.writepre.http.Constant;
import com.easier.writepre.response.VShowUserSecondResponse.AlbumInfo;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.StringUtil;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.AutoUtils;
import com.sj.autolayout.utils.DateKit;

import java.util.List;

@SuppressLint("InflateParams")
public class VShowUserSecondListAdapter extends BaseAdapter {

    private Context context;
    private List<AlbumInfo> list;
    private LayoutInflater inflater;

    public VShowUserSecondListAdapter(Context context, List<AlbumInfo> list) {
        super();
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public AlbumInfo getItem(int position) {
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
            convertView = inflater.inflate(R.layout.activity_v_show_second_item,
                    parent, false);
            viewHolder = new ViewHolder();
            viewHolder.line_v_show_bg = (LinearLayout) convertView.findViewById(R.id.line_v_show_bg);
            viewHolder.tv_type_face = (TextView) convertView.findViewById(R.id.tv_type_face);
            viewHolder.tv_ctime = (TextView) convertView.findViewById(R.id.tv_ctime);
            viewHolder.tv_look_num = (TextView) convertView.findViewById(R.id.tv_look_num);
            viewHolder.tv_pic_num = (TextView) convertView.findViewById(R.id.tv_pic_num);
            viewHolder.iv_album_url = (ImageView) convertView.findViewById(R.id.iv_album_url);
            convertView.setTag(viewHolder);
            AutoUtils.autoSize(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.line_v_show_bg.getBackground().setAlpha(50);

        final AlbumInfo albumInfo = getItem(position);

        if (albumInfo != null) {
            viewHolder.tv_type_face.setText(TextUtils.isEmpty(albumInfo.getTitle()) ? "" : albumInfo.getTitle());
            viewHolder.tv_ctime.setText(DateKit.Toymd(albumInfo.getCtime()));
            viewHolder.tv_look_num.setText(albumInfo.getView_num());
            viewHolder.tv_pic_num.setText(TextUtils.isEmpty(albumInfo.getPic_num()) ? "0" : albumInfo.getPic_num());
            if (!albumInfo.getFace_url().equals(viewHolder.iv_album_url.getTag())) {   // 判断是否加载过
                BitmapHelp.getBitmapUtils().display(viewHolder.iv_album_url,
                        StringUtil.getImgeUrl(albumInfo.getFace_url()) + Constant.BIG_IMAGE_SUFFIX,   //"group1/M00/03/D4/CjNTPVX5hHCAYxykAAFDgYizQwQ654.png"
                        new BitmapLoadCallBack<View>() {

                            @Override
                            public void onLoadCompleted(View arg0, String arg1,
                                                        Bitmap arg2, BitmapDisplayConfig arg3,
                                                        BitmapLoadFrom arg4) {
                                ((ImageView) arg0).setImageBitmap(arg2);
                                ((ImageView) arg0).setTag(albumInfo.getFace_url());
                            }

                            @Override
                            public void onLoadFailed(View arg0, String arg1,
                                                     Drawable arg2) {

                                ((ImageView) arg0)
                                        .setImageResource(R.drawable.empty_photo);
                            }
                        });
            }
        }
        return convertView;
    }

    /**
     * 刷新局部item
     *
     * @param view
     * @param num
     */
    public void updateItemView(View view, String num) {
        if (view.getTag() instanceof ViewHolder) {
            ViewHolder vh = (ViewHolder) view.getTag();
            vh.tv_look_num.setText(num);
        }
    }


    static class ViewHolder {
        private TextView tv_type_face, tv_ctime, tv_look_num, tv_pic_num;
        private ImageView iv_album_url;
        private LinearLayout line_v_show_bg;
    }
}
