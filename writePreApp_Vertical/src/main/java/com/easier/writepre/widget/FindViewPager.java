package com.easier.writepre.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 发现(经典碑帖，集字，知识库)  处理知识库弹出后向右侧滑事件冲突(拦截事件)
 */
public class FindViewPager extends ViewPager {

    private boolean noScroll = false;

    public void setNoScroll(boolean noScroll) {
        this.noScroll = noScroll;
    }

    public FindViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FindViewPager(Context context) {
        this(context, null);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (noScroll)
            return false;
        else
            return super.onInterceptTouchEvent(event);
    }


}
