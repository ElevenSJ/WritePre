package com.easier.writepre.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.response.QuestionsListResponse;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.widget.RoundCustomImageView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.AutoUtils;
import com.sj.autolayout.utils.DateKit;

import java.util.List;

public class PracticeQuestionsListAdapter extends BaseAdapter {

    private Context mContext;

    private List<QuestionsListResponse.QuestionsInfo> listData;


    public PracticeQuestionsListAdapter(List<QuestionsListResponse.QuestionsInfo> listData, Context mContext) {
        this.listData = listData;
        this.mContext = mContext;
    }


    @Override
    public int getCount() {
        if (listData == null) {
            return 0;
        } else {
            return listData.size();
        }
    }

    @Override
    public QuestionsListResponse.QuestionsInfo getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_questions_list, parent, false);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tv_description = (TextView) convertView.findViewById(R.id.tv_description);
            viewHolder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
            viewHolder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
            viewHolder.img_face = (SimpleDraweeView) convertView.findViewById(R.id.img_face);
            convertView.setTag(viewHolder);
            AutoUtils.autoSize(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String title = getItem(position).getTitle();
        String description = getItem(position).getDesc();
        String face = getItem(position).getFace_url();

        viewHolder.tv_title.setText(TextUtils.isEmpty(title) ? "" : "【" + title + "】");
        viewHolder.tv_description.setText(description);
        viewHolder.tv_num.setText(TextUtils.isEmpty(getItem(position).getCurrent_num()) ? "0题" : (getItem(position).getCurrent_num() + "题"));
        viewHolder.tv_status.setText(TextUtils.equals(getItem(position).getStatus(), "ok") ? "已更新" : "更新中");
        LogUtils.e("封面图片地址:" + StringUtil.getImgeUrl(face));
        Uri uri = Uri.parse(TextUtils.isEmpty(StringUtil.getImgeUrl(face)) ? "" : StringUtil.getImgeUrl(face));
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(WritePreApp.getApp().getWidth(1), AutoUtils.getPercentHeightSize(R.dimen.beitie_face_height)))
                .setAutoRotateEnabled(true)
                .build();
        DraweeController draweeController = viewHolder.img_face.getController();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setOldController(draweeController)
                .setImageRequest(request)
                .build();
        viewHolder.img_face.setController(controller);
        return convertView;
    }

    class ViewHolder {
        TextView tv_title;// 标题
        TextView tv_description;// 描述
        TextView tv_num;
        TextView tv_status;
        SimpleDraweeView img_face;// 图片封面
    }
}
