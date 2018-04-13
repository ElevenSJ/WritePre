package com.easier.writepre.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;

/**
 * Created by SunJie on 17/2/14.
 */

public class CircleLevelProgress extends View {
    private int width;//设置宽
    private int height;//设置高

    private Bitmap bitmap;//定义Bitmap
    private Canvas bitmapCanvas;//定义Bitmap的画布

    private Path mPath;    //定义路径
    private Paint mPathPaint;//定义路径的画笔

    private Paint mPaintCircle;//定义圆形的画笔

    private Paint mPaintText; //定义绘制进度文字的画笔

    private Paint mPaintResultText; //定义绘制完成文字的画笔

    //设置进度
    private int maxProgress = 100;
    private int currentProgress = 0;

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
        invalidate();//实时更新进度
    }


    private int count = 0;
    private static final int NEED_INVALIDATE = 0X6666;
    //操作UI主线程
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NEED_INVALIDATE:
                    //更新时间
                    count += 5;
                    if (count > 80) {
                        count = 0;
                    }
                    invalidate();
                    sendEmptyMessageDelayed(NEED_INVALIDATE, 50);
                    break;
            }

        }
    };

    public CircleLevelProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        //初始化一个路径
        mPath = new Path();
        //初始化绘制路径的画笔
        mPathPaint = new Paint();
        mPathPaint.setAntiAlias(true);
        mPathPaint.setColor(getResources().getColor(R.color.social_red));
        mPathPaint.setStyle(Paint.Style.FILL);
        mPathPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        mPaintCircle = new Paint();
        mPaintCircle.setAntiAlias(true);
        mPaintCircle.setColor(Color.argb(0xff, 0xff, 0xec, 0xec));

        mPaintText = new Paint();
        mPaintText.setTypeface(Typeface.SANS_SERIF);
        mPaintText.setAntiAlias(true);
        mPaintText.setColor(getResources().getColor(R.color.white));
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setTextSize(36f);

        mPaintResultText = new Paint();
        mPaintResultText.setTypeface(Typeface.SANS_SERIF);
        mPaintResultText.setAntiAlias(true);
        mPaintResultText.setColor(getResources().getColor(R.color.social_red));
        mPaintResultText.setTextAlign(Paint.Align.CENTER);
        mPaintResultText.setTextSize(28f);

        handler.sendEmptyMessageDelayed(NEED_INVALIDATE, 50);
    }

    public CircleLevelProgress(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
//        height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec)
        width = WritePreApp.getApp().getWidth(1f);
        height = width;
        setMeasuredDimension(width, height);//设置宽和高

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        bitmapCanvas = new Canvas(bitmap);
        bitmapCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        bitmapCanvas.drawCircle(width / 2, height / 2, width / 6, mPaintCircle);

        mPath.reset();
        mPath.moveTo(width, (height / 2 + width / 6) - (currentProgress * (width / 3) / maxProgress));
        mPath.lineTo(width, height / 2 + width / 6);
        mPath.lineTo(count, height / 2 + width / 6);
        mPath.lineTo(count, (height / 2 + width / 6) - (currentProgress * (width / 3) / maxProgress));
        for (int i = 0; i <= 10; i++) {
            mPath.rQuadTo(20, 10, 40, 0);
            mPath.rQuadTo(20, -10, 40, 0);
        }
        mPath.close();
        bitmapCanvas.drawPath(mPath, mPathPaint);

        bitmapCanvas.drawText(currentProgress * 100f / maxProgress + "%", width / 2, height / 2 + 20, mPaintText);
        bitmapCanvas.drawText(currentProgress >= 100 ? "加载完成" : "", width / 2, height / 2 + width / 6 + 30, mPaintResultText);

        canvas.drawBitmap(bitmap, 0, 0, null);
    }
}
