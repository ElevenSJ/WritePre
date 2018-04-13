package com.easier.writepre.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.entity.TecXsfCsInfo;
import com.easier.writepre.ui.CourseWebViewActivity;
import com.easier.writepre.ui.CurriculumInfoActivity;
import com.easier.writepre.ui.WebViewActivity;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.StringUtil;
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

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐课程适配器
 */
public class RecommendCourseAdapter extends BaseAdapter {

    private Context mContext;

    private List<TecXsfCsInfo> list;

    public RecommendCourseAdapter(Context context) {
        mContext = context;
        list = new ArrayList<>();
    }

    public void setData(List<TecXsfCsInfo> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(List<TecXsfCsInfo> data) {
        list.addAll(data);
        notifyDataSetChanged();
    }

    public void clearData() {
        list.clear();
        notifyDataSetChanged();
    }

    public String getLastId() {
        if (list.isEmpty()) {
            return "9";
        } else {
            return list.get(list.size() - 1).get_id();
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public TecXsfCsInfo getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_curriculum, parent, false);
            holder.iv_img = (SimpleDraweeView) convertView.findViewById(R.id.iv_img);
            holder.tv_curriculum_title = (TextView) convertView.findViewById(R.id.tv_curriculum_title);
            holder.tv_curriculum_title_hint = (TextView) convertView.findViewById(R.id.tv_curriculum_title_hint);
            holder.rel_content = (RelativeLayout) convertView.findViewById(R.id.rel_content);
            convertView.setTag(holder);
            AutoUtils.autoSize(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TecXsfCsInfo tecXsfCsInfo = getItem(position);
        if (tecXsfCsInfo != null) {
            holder.rel_content.setTag(tecXsfCsInfo);
            Uri uri = Uri.parse(TextUtils.isEmpty(StringUtil.getImgeUrl(tecXsfCsInfo.getPic_url())) ? "" : StringUtil.getImgeUrl(tecXsfCsInfo.getPic_url()));
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(AutoUtils.getPercentWidthSize(300), AutoUtils.getPercentHeightSize(230)))
                    .setAutoRotateEnabled(true)
                    .build();
            DraweeController draweeController= holder.iv_img.getController();
            PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setOldController(draweeController)
                    .setImageRequest(request)
                    .build();
            holder.iv_img.setController(controller);
            holder.tv_curriculum_title.setText(tecXsfCsInfo.getTitle());
            holder.tv_curriculum_title_hint.setText(tecXsfCsInfo.getDesc());
            holder.rel_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TecXsfCsInfo info = (TecXsfCsInfo) view.getTag();
                    if (TextUtils.equals(info.getType(), "html")) {
                        Intent intent = new Intent(mContext, CourseWebViewActivity.class);
                        intent.putExtra("url", StringUtil.getImgeUrl(info.getHtml_url()));
                        intent.putExtra("xsf_cs_id", info.get_id());
                        intent.putExtra("title", info.getTitle());
                        mContext.startActivity(intent);
                    } else if (TextUtils.equals(info.getType(), "video")) {
                        Intent intent = new Intent(mContext, CurriculumInfoActivity.class);
                        intent.putExtra("data", (TecXsfCsInfo) view.getTag());
                        mContext.startActivity(intent);
                    }

                }
            });
        }
        return convertView;
    }

    public class ViewHolder {
        private SimpleDraweeView iv_img;
        private TextView tv_curriculum_title;
        private TextView tv_curriculum_title_hint;
        private RelativeLayout rel_content;
    }
}
