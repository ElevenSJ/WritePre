package com.easier.writepre.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.easier.writepre.R;
import com.easier.writepre.utils.Bimp;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.PhotoView;
import com.easier.writepre.widget.PhotoViewAttacher;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;

import java.io.File;

/**
 * 考试查看大图
 *
 * @author zhoulu
 */
public class ExamImageLookActivity extends Activity {

    private String url; //图片网络路径
    private String localPath = "";//图片本地路径
    private PhotoView mImageView;
    private ProgressBar progressBar;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail_activity);
        mImageView = (PhotoView) findViewById(R.id.image);
        url = getIntent().getStringExtra("url");
        localPath = getIntent().getStringExtra("localPath");
        if (savedInstanceState != null) {
            url = savedInstanceState.getString("url");
            localPath = savedInstanceState.getString("localPath");
        }
        mImageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View arg0, float arg1, float arg2) {
                if (ExamImageLookActivity.this != null) {
                    ExamImageLookActivity.this.finish();
                }
            }
        });
        mImageView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.loading);
        if (!TextUtils.isEmpty(localPath)) {
            LogUtils.e("localPath=====" + localPath);
            loadLocalImag(localPath);
        } else {
            LogUtils.e("url=====" + url);
            if (url.startsWith("http")) {
                loadNetImage(url);
            } else {
                loadNetImage(StringUtil.getImgeUrl(url));
            }
        }
    }

    public void loadLocalImag(final String path) {
        mImageView.setTag(path);
        BitmapHelp.getBitmapUtils().display(mImageView, path, new BitmapLoadCallBack<View>() {

            @Override
            public void onLoadCompleted(View arg0, String arg1, Bitmap arg2, BitmapDisplayConfig arg3,
                                        BitmapLoadFrom arg4) {
                if (new File(path).length() > 3 * 1024 * 1024f) {
                    arg2 = Bimp.zoomImage(arg2, arg2.getWidth() / 2, arg2.getHeight() / 2);
                }

                mImageView.setImageBitmap(arg2);
            }

            @Override
            public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
                loadNetImage(StringUtil.getImgeUrl(url));
            }
        });
    }

    private void loadNetImage(String path) {
        BitmapHelp.getBitmapUtils().display(mImageView, path, new BitmapLoadCallBack<View>() {

            @Override
            public void onLoadCompleted(View arg0, String arg1, Bitmap arg2, BitmapDisplayConfig arg3,
                                        BitmapLoadFrom arg4) {
                mImageView.setImageBitmap(arg2);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
                String message = "图片无法显示";
                ToastUtil.show(message);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onLoading(View container, String uri, BitmapDisplayConfig config, long total, long current) {
                super.onLoading(container, uri, config, total, current);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("url", url);
        outState.putString("localPath", localPath);
    }

}