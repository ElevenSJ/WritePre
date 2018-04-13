package com.easier.writepre.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ScrollView;

/**
 * 自定义ScrollView，解决：ScrollView嵌套ViewPager，导致ViewPager不能滑动的问题
 */
public class CustomScrollView extends ScrollView {
    private boolean isScrollTop = true;
    private boolean isScrollBottom = false;
    private boolean isChildViewTop = true;

    private float xDistance, yDistance, lastX, lastY;

    public float getLastY() {
        return lastY;
    }

    public float getLastX() {
        return lastX;
    }

    private AbsListView listView;

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbsListView getListView() {
        return listView;
    }

    public void setListView(AbsListView listView) {
        this.listView = listView;
    }

    public void setChildViewOnScorllTop(boolean isTop) {
        isChildViewTop = isTop;
    }

    public void setEnableScroll(boolean canScroll)
    {
        if(canScroll) {
            isScrollTop = true;
            isScrollBottom = false;
        }else
        {
            isScrollTop = true;
            isScrollBottom = true;
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        View view = (View) getChildAt(getChildCount() - 1);
        int d = view.getBottom();
        d -= (getHeight() + getScrollY());
        if (d == 0) {
            isScrollBottom = true;
            isScrollTop = false;
        } else {
            if (getScrollY()<=0){
                isScrollTop = true;
            }else {
                isScrollTop = false;
            }
            isScrollBottom = false;
            super.onScrollChanged(l, t, oldl, oldt);
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                lastX = ev.getX();
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float minMove = 0; // 最小滑动距离
                final float curX = ev.getX();
                final float curY = ev.getY();
                xDistance += Math.abs(curX - lastX);
                yDistance += Math.abs(curY - lastY);
                if (lastX - curX > minMove&&xDistance>yDistance) { // 左滑
                    return false;
                } else if (curX - lastX > minMove&&xDistance>yDistance) { // 右滑
                    return false;
                } else if (lastY - curY > minMove&&xDistance<=yDistance) { // 上滑
                    if (isScrollBottom) {
                        return false;
                    } else
                        return true;
                }
                if (curY - lastY > minMove&&xDistance<=yDistance) { // 下滑
                    if (!isScrollTop) {
                        if (!isChildViewTop){
                            return false;
                        }else{
                            return true;
                        }
                    } else
                        return false;
                }
                lastX = curX;
                lastY = curY;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {

        return 0;
    }
}