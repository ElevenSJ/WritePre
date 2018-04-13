package com.easier.writepre.rongyun;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongIM.LocationProvider.LocationCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;

import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.http.WritePreListener;
import com.easier.writepre.param.RongYunCheckFriendParams;
import com.easier.writepre.param.RongYunCheckInGroupParams;
import com.easier.writepre.param.RongYunJointAttentionListParams;
import com.easier.writepre.param.RongYunTokenParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.RongYunCheckFriendResponse;
import com.easier.writepre.response.RongYunCheckInGroupResponse;
import com.easier.writepre.response.RongYunJointAttentionListResponse;
import com.easier.writepre.response.RongYunTokenResponse;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.Utils;

/**
 * @author zhoulu
 * @version 创建时间：2016-8-31 上午10:22:28 类说明 :封装一些需要和后台交互的请求接口
 */
public class RongYunUtils {
    private static RongYunUtils rongYunUtils = null;
    public Handler handler;

    private RongYunUtils() {
    }

    /**
     * 建立与融云服务器的连接
     *
     * @param token
     */
    public void connect(Context context, String token,
                        RongIMClient.ConnectCallback callback) {

        if (context.getApplicationInfo().packageName.equals(Utils.getCurProcessName(context.getApplicationContext()))) {

            /**
             * IMKit SDK调用第二步,建立与服务器的连接
             */
            RongIM.getInstance().connect(token, callback);

        }
    }

    /**
     * 实例化
     *
     * @return 返回一个RongYunUtils的对象
     */
    public static synchronized RongYunUtils getInstances() {
        if (rongYunUtils == null) {
            rongYunUtils = new RongYunUtils();
        }
        return rongYunUtils;
    }

    /**
     * 获取融云token
     */
    public void requestRongYunToken(Context context,
                                    WritePreListener<? extends BaseResponse> listener) {
        RongYunTokenParams params = new RongYunTokenParams();
        RequestManager.request(context, params, RongYunTokenResponse.class,
                listener, SPUtils.instance().getSocialPropEntity()
                        .getApp_socail_server());
    }

    /**
     * 相关关注的好友列表
     */
    public void requestJointAttentionList(Context context,
                                          WritePreListener<? extends BaseResponse> listener) {
        RongYunJointAttentionListParams params = new RongYunJointAttentionListParams();
        RequestManager.request(context, params,
                RongYunJointAttentionListResponse.class, listener, SPUtils
                        .instance().getSocialPropEntity()
                        .getApp_socail_server());
    }

    /**
     * 好友关系检查
     */
    public void requestCheckIsFriend(Context context, String user_id,
                                     WritePreListener<? extends BaseResponse> listener) {
        RongYunCheckFriendParams params = new RongYunCheckFriendParams(user_id);
        RequestManager.request(context, params,
                RongYunCheckFriendResponse.class, listener, SPUtils.instance()
                        .getSocialPropEntity().getApp_socail_server());
    }

    /**
     * 群组关系检查
     */
    public void requestCheckIsInGroup(Context context, String circle_id,
                                      WritePreListener<? extends BaseResponse> listener) {
        RongYunCheckInGroupParams params = new RongYunCheckInGroupParams(circle_id);
        RequestManager.request(context, params,
                RongYunCheckInGroupResponse.class, listener, SPUtils.instance()
                        .getSocialPropEntity().getApp_socail_server());
    }

    /**
     * 启动群聊,带自定义参数
     *
     * @param context
     * @param targetGroupId 群ID
     * @param title         标签
     * @param type          类型 0:默认 1:背景半透明
     */
    public void startGroupChat(Context context, String targetGroupId, String title, String type) {
        if (context != null && !TextUtils.isEmpty(targetGroupId)) {
            if (RongContext.getInstance() == null) {
                throw new ExceptionInInitializerError("RongCloud SDK not init");
            } else {
                Uri uri = Uri.parse("rong://" + context.getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(Conversation.ConversationType.GROUP.getName().toLowerCase()).appendQueryParameter("targetId", targetGroupId).appendQueryParameter("title", title).appendQueryParameter("type", type).build();
                context.startActivity(new Intent("android.intent.action.VIEW", uri));
            }
        } else {
            throw new IllegalArgumentException();
        }
    }
}
