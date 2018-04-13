package com.easier.writepre.ui;

import java.io.File;

import com.easier.writepre.R;
import com.easier.writepre.utils.Bimp;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.PhotoViewAttacher;
import com.easier.writepre.widget.PhotoViewAttacher.OnPhotoTapListener;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 集字预览大图查看
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class WordPreviewBigPicActivity extends BaseActivity {

    private Bitmap bitmap;

    private Button btn_preview_big;

    private ImageView img_preview_big;

    private PhotoViewAttacher photoAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_preview_big_pic);
        init();
    }

    @SuppressWarnings("deprecation")
    private void init() {

        btn_preview_big = (Button) findViewById(R.id.btn_preview_big);

        img_preview_big = (ImageView) findViewById(R.id.img_preview_big);

        final LinearLayout lLayout = WordPreviewActivity.llBody;

        lLayout.setBackground(null); // 大图预览时去掉背景

        lLayout.setPadding(0, 0, 0, 0);

        bitmap = Bimp.createViewBitmap(lLayout);

        bitmap = Bimp.Watermark(
                bitmap,
                ((BitmapDrawable) getResources().getDrawable(
                        R.drawable.watermark)).getBitmap(), 100);

        img_preview_big.setImageBitmap(bitmap);

        btn_preview_big.setOnClickListener(this);

        photoAttacher = new PhotoViewAttacher(img_preview_big);

        photoAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View view, float x, float y) {
                finish();
            }
        });

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_preview_big:
                if (bitmap == null) {
                    ToastUtil.show("获取图片异常,请重试!");
                    return;
                }
                File file = FileUtils.saveBitmap(FileUtils.SD_IMAGES_PATH,
                        System.currentTimeMillis() + ".png", bitmap);
                if (file.exists()) {
                    FileUtils.savePhotoToAlbums(WordPreviewBigPicActivity.this, file);
                    ToastUtil.show("保存成功");
                } else
                    ToastUtil.show("保存失败");
                break;

            default:
                break;
        }
    }
}
