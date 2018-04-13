package com.easier.writepre.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.easier.writepre.R;
import com.easier.writepre.ui.SquareImageLookActivity;
import com.easier.writepre.utils.StringUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.sj.autolayout.utils.ScreenUtils;

/**
 * 作业图片左右滑动展示
 *
 * @author zhoulu
 */
@SuppressLint("InflateParams")
public class TaskImageSlidePageAdapter extends PagerAdapter {

    private String[] urls;

    private Context mContext;

    private LayoutInflater mInflater;

    public TaskImageSlidePageAdapter(Context context, String urls[]) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.urls = urls;
    }

    @Override
    public int getCount() {
        return urls.length;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View pageView = (View) mInflater.inflate(R.layout.task_image_viewpager_item, null);
        SimpleDraweeView mImage = (SimpleDraweeView) pageView.findViewById(R.id.viewpager_item_image);
        Uri uri = Uri.parse(TextUtils.isEmpty(StringUtil.getImgeUrl(urls[position])) ? "" : StringUtil.getImgeUrl(urls[position]));
        int[] wh = ScreenUtils.getScreenSize(mContext, true);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(wh[0], wh[1]))
                .setAutoRotateEnabled(true)
                .build();
        DraweeController draweeController = mImage.getController();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setOldController(draweeController)
                .setImageRequest(request)
                .build();
        mImage.setController(controller);
        mImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                imageBrower(position);
            }
        });
        container.addView(pageView);
        return pageView;
    }

    /**
     * 点击查看大图
     *
     * @param position
     */
    private void imageBrower(int position) {
        Intent intent = new Intent(mContext, SquareImageLookActivity.class);
        intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_INDEX, position);
        mContext.startActivity(intent);
    }
}
