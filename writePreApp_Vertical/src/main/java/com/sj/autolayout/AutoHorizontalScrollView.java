package com.sj.autolayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

import com.sj.autolayout.utils.AutoLayoutHelper;

/**
 * Created by SunJie on 16/12/29.
 */

public class AutoHorizontalScrollView extends HorizontalScrollView{
    private final AutoLayoutHelper mHelper = new AutoLayoutHelper(this);

    public AutoHorizontalScrollView(Context context)
    {
        super(context);
    }

    public AutoHorizontalScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AutoHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AutoHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public AutoFrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs)
    {
        return new AutoFrameLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        if (!isInEditMode())
        {
            mHelper.adjustChildren();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
    }

}
