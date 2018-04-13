package com.easier.writepre.rongyun;

/**
 * 好友关系检查回调
 * Created by zhoulu on 16/9/23.
 */

public interface CheckFriendListener {
    /**
     * 好友关系回调
     *
     * @param isFriend false 不是好友  true 是好友
     * @param userId   检查对象的ID
     */
    public void onIsFriend(boolean isFriend, String userId);
}
