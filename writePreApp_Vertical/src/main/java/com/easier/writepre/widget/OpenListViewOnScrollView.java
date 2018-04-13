package com.easier.writepre.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 展开的ListView,解决与ScrollView冲突问题
 * Created by zhoulu on 2017/3/9.
 */

public class OpenListViewOnScrollView extends ListView {
    public OpenListViewOnScrollView(Context context) {
        super(context);
    }

    public OpenListViewOnScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OpenListViewOnScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //此处是代码的关键
        //MeasureSpec.AT_MOST的意思就是wrap_content
        //Integer.MAX_VALUE >> 2 是使用最大值的意思,也就表示的无边界模式
        //Integer.MAX_VALUE >> 2 此处表示是父布局能够给他提供的大小
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
