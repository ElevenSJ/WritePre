package com.easier.writepre.mainview;

import java.util.HashMap;
import java.util.Map;

import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.widget.NoScrollViewPager;

import android.content.Context;
import android.content.Intent;
import android.view.View;

public class ChildViewManager {

    private int currentIndex = 0;

    private Context mCtx;


    private HashMap<Integer, BaseView> baseViews = new HashMap<Integer, BaseView>();
    private Map<Integer, String> content;

    public ChildViewManager(Context ctx, Map<Integer, String> content) {
        this.mCtx = ctx;
        this.content = content;
    }

    public View getView(int type) {
        View view = null;
        if (baseViews.get(type) == null) {
            initBaseView(type);
        }
        view = baseViews.get(type).getView();
        return view;
    }

    public String getViewTitle(int type) {
        return content.get(type);
    }

    public void initBaseView(int type) {
        switch (type) {
            case MainActivity.TYPE_ONE:
                baseViews.put(type, new CourseMainView(mCtx));
                break;
            case MainActivity.TYPE_TWO:
                baseViews.put(type, new SocialMainView(mCtx));
                break;
            case MainActivity.TYPE_THR:
                baseViews.put(type, new CollegeMainView(mCtx));
                break;
            case MainActivity.TYPE_FOR:
                baseViews.put(type, new FoundMainView(mCtx));
                break;
            case MainActivity.TYPE_FIV:
                baseViews.put(type, new MyMainView(mCtx));
                break;
        }
    }

    public void showView(int position,Intent intent) {
        if (position == -1) {
            return;
        }
        if (currentIndex != position) {
            if (baseViews.get(position).isShowView) {
                baseViews.get(position).hideView();
            }
        }
        currentIndex = position;
        baseViews.get(position).showView(intent);
//		ToastUtil.show(getViewTitle(position));
    }

    public void destoryView(int position) {
        if (position < 0 || baseViews.get(position) == null) {
            return;
        }
        baseViews.get(position).destory();
    }

    public void onStop(int position) {
        if (position < 0 || baseViews.get(position) == null) {
            return;
        }
        baseViews.get(position).onStop();
    }

    public void onResume(int position) {
        if (position < 0 || baseViews.get(position) == null) {
            return;
        }
        baseViews.get(position).onResume();
    }

    public void onNewIntent(int position, Intent intent) {
        if (position < 0 || baseViews.get(position) == null) {
            return;
        }
        baseViews.get(position).onNewIntent(intent);
        ;
    }

    public void destoryAllView() {
        for (int i = 0; i < baseViews.size(); i++) {
            baseViews.get(i).destory();
        }
    }

    public void onPause(int position) {
        if (position < 0 || baseViews.get(position) == null) {
            return;
        }
        baseViews.get(position).onPause();
    }
}
