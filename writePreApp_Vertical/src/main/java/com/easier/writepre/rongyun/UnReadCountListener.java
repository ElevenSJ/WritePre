package com.easier.writepre.rongyun;

/**
 * 消息数回调接口
 * Created by zhoulu on 16/9/12.
 */
public interface UnReadCountListener {
    public void onMessageIncreased(int count);
    public void onCheckMsgCount();
}
