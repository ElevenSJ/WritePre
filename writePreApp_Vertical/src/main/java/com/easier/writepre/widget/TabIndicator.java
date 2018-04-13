/*
 * Copyright (C) 2011 The Android Open Source Project
 * Copyright (C) 2011 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.easier.writepre.widget;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import java.util.ArrayList;

import com.easier.writepre.R;
import com.sj.autolayout.AutoHorizontalScrollView;
import com.sj.autolayout.utils.AutoUtils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * This widget implements the dynamic action bar tab behavior that can change
 * across different configurations or circumstances.
 */
public class TabIndicator extends AutoHorizontalScrollView implements ViewPager.OnPageChangeListener {
    private Runnable mTabSelector;
    private RadioGroup mTabLayout;
    private int mMaxTabWidth;
    private int mSelectedTabIndex = 0;
    private OnTabSelectedListener mTabSelectedListener;
    private int childCount;
    private String tabString;

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mListener;

    public interface OnTabSelectedListener {
        /**
         * Callback when the selected tab has been reselected.
         *
         * @param position Position of the current center item.
         */
        void onTabSelected(int position, String id, String name);
    }

    private final OnClickListener mTabClickListener = new OnClickListener() {
        public void onClick(View view) {
            TabView tabView = (TabView) view;
            final int newSelected = tabView.getIndex();
            if (newSelected != mSelectedTabIndex) {
                mSelectedTabIndex = newSelected;
                mTabLayout.check(mSelectedTabIndex);
                setCurrentItem(mSelectedTabIndex);
                if (mTabSelectedListener != null) {
                    mTabSelectedListener.onTabSelected(mSelectedTabIndex, tabView.getmId(), tabView.getmName());
                }
            }
        }
    };

    public TabIndicator(Context context) {
        this(context, null);
        init(context);
    }

    public void setOnTabSelectedListener(OnTabSelectedListener listener) {
        mTabSelectedListener = listener;
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mListener = listener;
    }

    private void init(Context context) {
        setHorizontalScrollBarEnabled(false);
        mTabLayout = new RadioGroup(context);
        mTabLayout.setOrientation(RadioGroup.HORIZONTAL);
        addView(mTabLayout, new ViewGroup.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mTabLayout.setOnCheckedChangeListener(listener);
    }

    public TabIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final boolean lockedExpanded = widthMode == MeasureSpec.EXACTLY;
        setFillViewport(lockedExpanded);

        final int childCount = this.childCount;// mTabLayout.getChildCount();
        if (childCount > 1 && (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST)) {
            int screenWidth = MeasureSpec.getSize(widthMeasureSpec);
            // 去除中间分割线宽度
//			screenWidth = screenWidth - (childCount - 1) * 2;
            if (childCount > 4) {
                mMaxTabWidth = (int) (screenWidth * 0.3f);
            } else {
                mMaxTabWidth = screenWidth / childCount;
            }
        } else {
            mMaxTabWidth = -1;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void animateToTab(final View tabView) {
        if (tabView == null) {
            return;
        }
        setTabString(((TabView) mTabLayout.getChildAt(mSelectedTabIndex)).getText().toString());
        for (int i = 0; i < mTabLayout.getChildCount(); i++) {
            if (i == mSelectedTabIndex) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                    ((TabView) mTabLayout.getChildAt(i)).setTextColor(getResources().getColor(R.color.social_red));
                 }
                mTabLayout.getChildAt(i).setSelected(true);
            } else {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                    ((TabView) mTabLayout.getChildAt(i)).setTextColor(getResources().getColor(R.color.black));
                }
                mTabLayout.getChildAt(i).setSelected(false);
            }
        }
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
        mTabSelector = new Runnable() {
            public void run() {
                final int scrollPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;
                smoothScrollTo(scrollPos, 0);
                mTabSelector = null;
            }
        };
        post(mTabSelector);
    }

    public void animateToTab(final int position) {
        mSelectedTabIndex = position;
        final TabView tabView = (TabView) mTabLayout.getChildAt(position);
        if (tabView == null) {
            return;
        }
        animateToTab(tabView);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mTabSelector != null) {
            post(mTabSelector);
        }
    }

    public void setTab(String[] tabs) {
        if (tabs != null) {
            mTabLayout.removeAllViews();
            mTabLayout.clearCheck();
            String id = "";
            String name = "";
            int size = tabs.length;
            childCount = size;
            for (int i = 0; i < tabs.length; i++) {
                id = i + "";
                name = tabs[i];
                addTab(i, id, name);
            }
            mSelectedTabIndex = 0;
            mTabLayout.check(mSelectedTabIndex);

            animateToTab(mSelectedTabIndex);
            if (mTabSelectedListener != null) {
                mTabSelectedListener.onTabSelected(mSelectedTabIndex, mSelectedTabIndex + "", tabs[mSelectedTabIndex]);
            }
            requestLayout();
        }
    }

    public void setTab(String[] tabs, int index) {
        if (tabs != null) {
            mTabLayout.removeAllViews();
            mTabLayout.clearCheck();
            String id = "";
            String name = "";
            int size = tabs.length;
            childCount = size;
            for (int i = 0; i < tabs.length; i++) {
                id = i + "";
                name = tabs[i];
                addTab(i, id, name);
            }
            mSelectedTabIndex = index;
            mTabLayout.check(mSelectedTabIndex);

            animateToTab(mSelectedTabIndex);
            if (mTabSelectedListener != null) {
                mTabSelectedListener.onTabSelected(mSelectedTabIndex, mSelectedTabIndex + "", tabs[mSelectedTabIndex]);
            }
            requestLayout();
        }
    }

    public String getTabString() {
        return tabString;
    }

    public void setTabString(String tabString) {
        this.tabString = tabString;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
    }

    private void addTab(int index, String id, String text) {
        TabView tabView = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tabView = new TabView(getContext(), R.style.FreeTabStyle);
        } else {
            tabView = new TabView(getContext());
            tabView.setBackgroundResource(R.drawable.social_main_tab);
            tabView.setTextColor(getResources().getColor(R.color.social_main_tab));
            tabView.setButtonDrawable(getResources().getDrawable(R.drawable.transparent));
            tabView.setSelected(false);
        }
        tabView.setTextSize(16f);
        tabView.mIndex = index;
        tabView.mId = id;
        tabView.mName = text;
        tabView.setId(index);
        tabView.setGravity(Gravity.CENTER);
        tabView.setFocusable(true);
        tabView.setOnClickListener(mTabClickListener);
        tabView.setText(text);
        AutoUtils.autoSize(tabView);
        mTabLayout.addView(tabView,
                new RadioGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1));
    }

    public class TabView extends RadioButton {
        private int mIndex;
        private String mId;
        private String mName;

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public TabView(Context context, int freetabstyle) {
            super(context, null, 0, freetabstyle);
        }

        public TabView(Context context) {
            super(context);
        }

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (mMaxTabWidth > 0 && getMeasuredWidth() < mMaxTabWidth) {
                super.onMeasure(MeasureSpec.makeMeasureSpec(mMaxTabWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
            }
        }

        public int getIndex() {
            return mIndex;
        }

        public String getmId() {
            return mId;
        }

        public String getmName() {
            return mName;
        }

    }

    public void setViewPage(ViewPager pager) {
        if (pager != null) {
            mViewPager = pager;
        } else {
            return;
        }
        final PagerAdapter adapter = mViewPager.getAdapter();
        if (adapter == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        mViewPager.setOnPageChangeListener(this);
        final int count = adapter.getCount();
        String[] tabs = new String[count];
        for (int i = 0; i < count; i++) {
            CharSequence title = adapter.getPageTitle(i);
            tabs[i] = title.toString();
        }
        setTab(tabs);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        if (mListener != null) {
            mListener.onPageScrollStateChanged(arg0);
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        if (mListener != null) {
            mListener.onPageScrolled(arg0, arg1, arg2);
        }
    }

    @Override
    public void onPageSelected(int arg0) {
        setCurrentItem(arg0);
        if (mListener != null) {
            mListener.onPageSelected(arg0);
        }
    }

    public void setCurrentItem(int item) {
        if (mViewPager == null) {
            return;
        }
        mSelectedTabIndex = item;
        mViewPager.setCurrentItem(item);
        animateToTab(item);
    }
}
