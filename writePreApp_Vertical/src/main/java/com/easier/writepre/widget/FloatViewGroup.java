package com.easier.writepre.widget;

import com.easier.writepre.R;
import com.easier.writepre.utils.Bimp;
import com.easier.writepre.utils.DiskCache;
import com.easier.writepre.utils.LogUtils;
import com.sj.autolayout.AutoFrameLayout;
import com.sj.autolayout.utils.AutoUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

@SuppressLint("ClickableViewAccessibility")
public class FloatViewGroup extends AutoFrameLayout implements OnSeekBarChangeListener, View.OnClickListener {

    private Context ctx;
    private int imgDisplayW;
    private int imgDisplayH;
    private int imgW;
    private int imgH;
    private FloatImageView floatImageView;
    private View seekView;
    private View topView;

    private ImageView imgShow;
    private ImageView imgCancle;

    private Bitmap imgBitmap;

    private int initAlpha = 127;// 初始化透明度
    private static int ImageAlpha = 255;// 透明度
    private int iniRotate = 360;// 初始化旋转角度
    private static int ImageRotate = 720;// 旋转度

    private boolean isShownDismissBt = false;

    private long touchTime = 0;

    public FloatViewGroup(Context context) {
        super(context);
        ctx = context;
        init();
    }

    public FloatViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
        init();
    }

    private void init() {
        initDisplay();
    }

    public FloatViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ctx = context;
        init();
    }

    private void initDisplay() {
        imgDisplayW = ((Activity) ctx).getWindowManager().getDefaultDisplay().getWidth();
        imgDisplayH = ((Activity) ctx).getWindowManager().getDefaultDisplay().getHeight();
    }

    float xDown, yDown, xUp, yUp;

    public void setImageRes(String filePath) {
        if (floatImageView == null) {
            floatImageView = new FloatImageView(ctx, imgDisplayW, imgDisplayH);
        }
        imgBitmap = DiskCache.getInstance().loadImageFromLocal(filePath, imgDisplayW / 3 * 2, imgDisplayH / 3 * 2);
        int degree = Bimp.readPictureDegree(filePath);
        if (degree != 0) {
            imgBitmap = Bimp.rotateToDegrees(
                    imgBitmap, degree);
        }
        addfloatImageView();
    }

    public void setImageRes(int resId) {
        if (floatImageView == null) {
            floatImageView = new FloatImageView(ctx, imgDisplayW, imgDisplayH);
        }
        imgBitmap = BitmapFactory.decodeResource(ctx.getResources(), resId);
        addfloatImageView();
    }

    public void addfloatImageView() {
        if (imgBitmap != null) {
            imgW = imgBitmap.getWidth();
            imgH = imgBitmap.getHeight();
            int layout_w = imgW > imgDisplayW ? imgDisplayW : imgW;
            int layout_h = imgH > imgDisplayH ? imgDisplayH : imgH;

            if (imgW >= imgH) {
                if (layout_w == imgDisplayW) {
                    layout_h = (int) (imgH * ((float) imgDisplayW / imgW));
                }
            } else {
                if (layout_h == imgDisplayH) {
                    layout_w = (int) (imgW * ((float) imgDisplayH / imgH));
                }
            }

            floatImageView.setImageBitmap(imgBitmap);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(layout_w, layout_h);
            params.gravity = Gravity.CENTER;
            floatImageView.setLayoutParams(params);
            if (!isExit(floatImageView)) {
                this.addView(floatImageView);
            } else {
                floatImageView.setVisibility(View.VISIBLE);
            }
            bringToFront();
            floatImageView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN){
                        touchTime = System.currentTimeMillis();
                        xDown = event.getX();
                        yDown = event.getY();
                    }else if (event.getAction() == MotionEvent.ACTION_UP){
                        LogUtils.v("MotionEvent.ACTION_UP");
                        xUp = event.getX();
                        yUp = event.getY();
                        if (System.currentTimeMillis()-touchTime<500&&Math.abs(xDown - xUp) == 0 && Math.abs(yDown - yUp) == 0) {
                            if (seekView != null) {
                                if (seekView.getVisibility() == View.VISIBLE) {
                                    seekView.setVisibility(View.INVISIBLE);
                                } else {
                                    seekView.setVisibility(View.VISIBLE);
                                }
                            }
                            if (topView != null) {
                                if (topView.getVisibility() == View.VISIBLE) {
                                    topView.setVisibility(View.INVISIBLE);
                                } else {
                                    topView.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        touchTime = 0;
                        return true;
                    }
                    return false;
                }
            });
        }

    }


    public void addSeekBar() {
        if (seekView == null) {
            seekView = LayoutInflater.from(ctx).inflate(R.layout.layout_seekbar, null);
            seekView.findViewById(R.id.seek_layout).getBackground().setAlpha(100);
            SeekBar seekbar = (SeekBar) seekView.findViewById(R.id.rotate_seek);
            seekbar.setMax(ImageRotate);
            seekbar.setProgress(iniRotate);

            SeekBar seekbar2 = (SeekBar) seekView.findViewById(R.id.alpha_seek);
            seekbar2.setMax(ImageAlpha);
            seekbar2.setProgress(initAlpha);

            setViewAlpha(initAlpha);
            setViewRotate(iniRotate);

            seekbar.setOnSeekBarChangeListener(this);
            seekbar2.setOnSeekBarChangeListener(this);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            seekView.setLayoutParams(params);// 这是自定义imageView的大小，也就是触摸范围
            AutoUtils.autoSize(seekView);
        }
        if (isExit(seekView)) {
            seekView.setVisibility(View.VISIBLE);
        } else {
            this.addView(seekView);
        }
        seekView.bringToFront();
    }

    public void addTopBar() {
        if (topView == null) {
            topView = LayoutInflater.from(ctx).inflate(R.layout.layout_topbar, null);
            imgShow = (ImageView) topView.findViewById(R.id.iv_show);
            imgShow.setOnClickListener(this);
            imgCancle = (ImageView) topView.findViewById(R.id.iv_cancle);
            imgCancle.setOnClickListener(this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
            topView.setLayoutParams(params);// 这是自定义imageView的大小，也就是触摸范围
            AutoUtils.autoSize(seekView);
        }
        if (isExit(topView)) {
            topView.setVisibility(View.VISIBLE);
        } else {
            this.addView(topView);
        }
        topView.bringToFront();
    }

    public void removeChildView(View view) {
        if (view != null) {
            if (isExit(view)) {
                removeView(view);
            }
        }
    }

    public boolean isExit(View view) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) == view) {
                return true;
            }
        }
        return false;
    }

    @SuppressLint("NewApi")
    public void setViewAlpha(int progress) {
        if (floatImageView != null) {
            floatImageView.setImageAlpha(progress);
        }
    }

    public void setViewRotate(int progress) {
        if (floatImageView != null) {
            floatImageView.setRotation(progress);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.rotate_seek:
                setViewRotate(progress);
                break;
            case R.id.alpha_seek:
                setViewAlpha(progress);
                break;
            default:
                break;
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    public void dismiss() {
        if (topView != null) {
            removeChildView(topView);
        }
        if (seekView != null) {
            removeChildView(seekView);
        }
        if (floatImageView != null) {
            removeChildView(floatImageView);
        }
        this.setVisibility(View.INVISIBLE);
        if (imgBitmap != null) {
            imgBitmap.recycle();
        }
        floatImageView = null;
        topView = null;
        seekView = null;
        setVisibility(View.GONE);
        System.gc();
    }

    public boolean isShowing(){
        return floatImageView != null;
    }

    private void showOrHide() {
        if (imgShow != null && imgShow.getRotation() == 0f) {
            imgShow.setRotation(180);
        } else {
            imgShow.setRotation(0);
        }
        if (seekView != null) {
            if (seekView.getVisibility() == View.VISIBLE) {
                seekView.setVisibility(View.INVISIBLE);
            } else {
                seekView.setVisibility(View.VISIBLE);
            }
        }
        if (imgCancle != null) {
            if (imgCancle.getVisibility() == View.VISIBLE) {
                imgCancle.setVisibility(View.INVISIBLE);
            } else {
                imgCancle.setVisibility(View.VISIBLE);
            }
        }
        if (floatImageView != null) {
            if (floatImageView.getVisibility() == View.VISIBLE) {
                floatImageView.setVisibility(View.INVISIBLE);
            } else {
                floatImageView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_show:
                showOrHide();
                break;
            case R.id.iv_cancle:
                dismiss();
                break;
        }
    }

}