package com.easier.writepre.fragment;

import com.easier.writepre.R;
import com.easier.writepre.ui.FoundBeiTiePreviewActivity;
import com.easier.writepre.utils.AnimationsUtil;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.PhotoViewAttacher;
import com.easier.writepre.widget.PhotoViewAttacher.OnPhotoTapListener;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

public class FoundBeiTieImageDetailFragment extends BaseFragment {

    private String mImageUrl;

    public ImageView mImageView;

    private ProgressBar progressBar;

    public PhotoViewAttacher mAttacher;

    public static FoundBeiTieImageDetailFragment newInstance(String imageUrl) {
        final FoundBeiTieImageDetailFragment f = new FoundBeiTieImageDetailFragment();
        final Bundle args = new Bundle();
        args.putString("url", imageUrl);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString("url")
                : null;
    }

    @Override
    public int getContextView() {
        return R.layout.found_beitie_image_detail_fragment;
    }

    @Override
    protected void init() {

        mImageView = (ImageView) findViewById(R.id.image);

        mAttacher = new PhotoViewAttacher(mImageView);

        // mAttacher.setScaleType(ScaleType.FIT_XY);  //ScaleType.FIT_CENTER

        mAttacher.setScaleType(ScaleType.FIT_CENTER);

        progressBar = (ProgressBar) findViewById(R.id.loading);

        mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View arg0, float arg1, float arg2) {
                ((FoundBeiTiePreviewActivity) getActivity()).showOrHideBar();
            }
        });

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BitmapHelp.getBitmapUtils().display(mImageView, mImageUrl,
                new BitmapLoadCallBack<View>() {

                    @Override
                    public void onLoadCompleted(View arg0, String arg1,
                                                Bitmap arg2, BitmapDisplayConfig arg3,
                                                BitmapLoadFrom arg4) {
                        ((ImageView) arg0).setImageBitmap(arg2);
                        progressBar.setVisibility(View.GONE);
                        mAttacher.update();
                    }

                    @Override
                    public void onLoadFailed(View arg0, String arg1,
                                             Drawable arg2) {
                        String message = "图片无法显示";
                        ToastUtil.show(message);
                        progressBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onLoading(View container, String uri,
                                          BitmapDisplayConfig config, long total, long current) {
                        super.onLoading(container, uri, config, total, current);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });

    }

    @Override
    public void onClick(View v) {

    }

}
