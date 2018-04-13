package com.easier.writepre.fragment;

import com.easier.writepre.R;
import com.easier.writepre.utils.Bimp;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.PhotoView;
import com.easier.writepre.widget.PhotoViewAttacher.OnPhotoTapListener;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * @author kai.zhong
 */
public class ImageDetailFragment extends BaseFragment {

    private String mImageUrl;

    public PhotoView mImageView;

    private ProgressBar progressBar;

    private ImageView ivSpotLight; // 是否开启聚光灯效果

    // private PhotoViewAttacher mAttacher;

    public Bitmap bitmap;

    private boolean YNAddSpotLight; // 是否添加聚光灯效果

    public static ImageDetailFragment newInstance(String imageUrl) {
        final ImageDetailFragment f = new ImageDetailFragment();
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
        return R.layout.image_detail_fragment;
    }

    @Override
    protected void init() {
        ivSpotLight = (ImageView) findViewById(R.id.iv_spot_light);
       // ivSpotLight.setVisibility(View.VISIBLE);// 聚光灯效果(每个页面都有图标)
        mImageView = (PhotoView) findViewById(R.id.image);
        mImageView.setOnPhotoTapListener(new OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View arg0, float arg1, float arg2) {
                getActivity().finish();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.loading);

        ivSpotLight.setOnClickListener(this);
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
                        if (isDestory) {
                            return;
                        }
                        bitmap = arg2;
                        if (Bimp.map.get(mImageUrl) != null
                                && Bimp.map.get(mImageUrl)) {
                            mImageView.setImageBitmap(Bimp.addFilterBitmap(
                                    arg2, getActivity()));
                            ivSpotLight
                                    .setImageResource(R.drawable.spotlight_remove);
                        } else {
                            mImageView.setImageBitmap(arg2);
                            ivSpotLight
                                    .setImageResource(R.drawable.spotlight_add);
                        }
                        progressBar.setVisibility(View.GONE);
                        // mAttacher.update();
                    }

                    @Override
                    public void onLoadFailed(View arg0, String arg1,
                                             Drawable arg2) {
                        if (isDestory) {
                            return;
                        }
                        bitmap = null;
                        progressBar.setVisibility(View.GONE);
                        mImageView.setImageResource(R.drawable.empty_photo);

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
        switch (v.getId()) {
            case R.id.iv_spot_light:
                if (bitmap == null) {
                    ToastUtil.show("图片加载完成功后再点击");
                    return;
                } else {
                    if (YNAddSpotLight) {
                        mImageView.setImageBitmap(bitmap);
                        YNAddSpotLight = false;
                        Bimp.map.put(mImageUrl, YNAddSpotLight);
                        ivSpotLight.setImageResource(R.drawable.spotlight_add);
                    } else {
                        mImageView.setImageBitmap(Bimp.addFilterBitmap(bitmap,
                                getActivity()));
                        YNAddSpotLight = true;
                        Bimp.map.put(mImageUrl, YNAddSpotLight);
                        ivSpotLight.setImageResource(R.drawable.spotlight_remove);
                    }
                    // ivSpotLight.setEnabled(false);
                }
                break;
        }
    }

}
