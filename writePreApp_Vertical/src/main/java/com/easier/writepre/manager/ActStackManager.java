package com.easier.writepre.manager;

import android.app.Activity;

import com.easier.writepre.utils.LogUtils;

import java.util.Stack;

/**
 * Activity 栈管理类
 * Created by zhoulu on 2016/11/24.
 */

public class ActStackManager {
    private static Stack<Activity> activityStack;
    private static ActStackManager instance;
    private Activity tagAct = null;

    private ActStackManager() {
    }

    public synchronized static ActStackManager getInstance() {
        if (instance == null) {
            instance = new ActStackManager();
        }
        return instance;
    }

    public void popActivity() {
        Activity activity = activityStack.lastElement();
        if (activity != null) {
            activity.finish();
        }
    }

    public Stack<Activity> getActivityStack() {
       return activityStack;
    }

    public void popRemoveActivity() {
        Activity activity = activityStack.lastElement();
        if (activity != null) {
            activityStack.remove(activity);
        }
    }

    public void popRemoveAllActivity() {
        if (activityStack != null) {
            activityStack.clear();
        }
        setTagAct(null);
    }

    public void popActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            activityStack.remove(activity);
        }
    }

    public Activity currentActivity() {
        if (activityStack == null || activityStack.size() == 0) {
            return null;
        }
        Activity activity = activityStack.lastElement();

        return activity;
    }

    public void pushActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    public void removeActivity(Activity activity) {
        if (activityStack == null) {
            return;
        }
        activityStack.remove(activity);
    }

    /**
     * 弹出页面
     *
     * @param cls 指定的页面 不弹出
     */
    public void popAllActivityExceptOne(Class cls) {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            if (activity.getClass().equals(cls)) {
                break;
            }
            popActivity(activity);
        }
    }

    public void allFinish() {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            activity.finish();
            removeActivity(activity);
        }
    }

    /**
     * 弹出指定页面到最顶层的页面
     *
     * @param
     */
    public void popAllActivityFromTag2Top() {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            if (tagAct != null && tagAct == activity) {
                popActivity(activity);
                setTagAct(null);
                return;
            }
            popActivity(activity);
        }
    }

    public Activity getTagAct() {
        return tagAct;
    }

    public void setTagAct(Activity tagAct) {
        this.tagAct = tagAct;
    }
}

