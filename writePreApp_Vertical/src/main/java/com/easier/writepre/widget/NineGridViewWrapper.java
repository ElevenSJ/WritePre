package com.easier.writepre.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.easier.writepre.R;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;

public class NineGridViewWrapper extends SimpleDraweeView {

    private int moreNum = 0;              //显示更多的数量
    private int maskColor = 0x88000000;   //默认的遮盖颜色
    private float textSize = 35;          //显示文字的大小单位sp
    private int textColor = 0xFFFFFFFF;   //显示文字的颜色

    private TextPaint textPaint;              //文字的画笔
    private String msg = "";                  //要绘制的文字

    private boolean isVideo = false;    //是否是视频
    private Paint videoPaint;              //视频播放按钮
    private Bitmap bitmap;
    private int videoDrawable = R.drawable.video_player_icon;//要绘制的图片

    public NineGridViewWrapper(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
        //转化单位
        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, getContext().getResources().getDisplayMetrics());

        textPaint = new TextPaint();
        textPaint.setTextAlign(Paint.Align.CENTER);  //文字居中对齐
        textPaint.setAntiAlias(true);                //抗锯齿
        textPaint.setTextSize(textSize);             //设置文字大小
        textPaint.setColor(textColor);               //设置文字颜色

    }

    public NineGridViewWrapper(Context context) {
        this(context, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (moreNum > 0) {
            canvas.drawColor(maskColor);
            float baseY = getHeight() / 2 - (textPaint.ascent() + textPaint.descent()) / 2;
            canvas.drawText(msg, getWidth() / 2, baseY, textPaint);
        }

        if (isVideo) {
            // 绘图
            canvas.drawBitmap(bitmap, getWidth() / 2-bitmap.getWidth()/2, getHeight()/2-bitmap.getHeight()/2, videoPaint);
        }
    }

    public int getMoreNum() {
        return moreNum;
    }
    public void setMoreNum(int moreNum) {
        this.moreNum = moreNum;
        msg = "+" + moreNum;
        invalidate();
    }

    public void setIsVideo(boolean isVideo) {
        this.isVideo = isVideo;
        if (isVideo){
            videoPaint = new Paint();
            bitmap = BitmapFactory.decodeResource(getResources(), videoDrawable);
        }
        invalidate();
    }

    public int getMaskColor() {
        return maskColor;
    }

    public void setMaskColor(int maskColor) {
        this.maskColor = maskColor;
        invalidate();
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        textPaint.setTextSize(textSize);
        invalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        textPaint.setColor(textColor);
        invalidate();
    }
}