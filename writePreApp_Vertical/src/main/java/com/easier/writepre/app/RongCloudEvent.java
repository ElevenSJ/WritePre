package com.easier.writepre.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.easier.writepre.http.RequestManager;
import com.easier.writepre.http.WritePreListener;
import com.easier.writepre.param.CircleGetDetailParams;
import com.easier.writepre.param.RongYunCheckFriendParams;
import com.easier.writepre.param.UserInfoParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CircleDetailResponse;
import com.easier.writepre.response.RongYunCheckFriendResponse;
import com.easier.writepre.response.UserInfoResponse;
import com.easier.writepre.rongyun.CheckFriendListener;
import com.easier.writepre.rongyun.ExtensionModule;
import com.easier.writepre.rongyun.GroupNoticeListener;
import com.easier.writepre.rongyun.UnReadCountListener;
import com.easier.writepre.rongyun.WPNoticeMessage;
import com.easier.writepre.rongyun.WPShortVideoMessage;
import com.easier.writepre.ui.UserInfoActivity;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;

import java.util.List;

import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.IExtensionModule;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongExtensionManager;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.model.UIConversation;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.location.message.RealTimeLocationStartMessage;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.CommandMessage;
import io.rong.message.ContactNotificationMessage;
import io.rong.message.DiscussionNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.LocationMessage;
import io.rong.message.PublicServiceMultiRichContentMessage;
import io.rong.message.PublicServiceRichContentMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;


//import io.rong.imkit.widget.provider.CameraInputProvider;
//import io.rong.imkit.widget.provider.VoIPInputProvider;

//import io.rong.imkit.widget.provider.VoIPInputProvider;

/**
 * @author zhoulu
 */

/**
 * 融云SDK事件监听处理。 把事件统一处理，开发者可直接复制到自己的项目中去使用。
 * <p/>
 * 该类包含的监听事件有： 1、消息接收器：OnReceiveMessageListener。
 * 2、发出消息接收器：OnSendMessageListener。 3、用户信息提供者：GetUserInfoProvider。
 * 4、好友信息提供者：GetFriendsProvider。 5、群组信息提供者：GetGroupInfoProvider。
 * 7、连接状态监听器，以获取连接相关状态：ConnectionStatusListener。 8、地理位置提供者：LocationProvider。
 * 9、自定义 push 通知： OnReceivePushMessageListener。
 * 10、会话列表界面操作的监听器：ConversationListBehaviorListener。
 */
public final class RongCloudEvent implements
        RongIMClient.OnReceiveMessageListener, RongIM.OnSendMessageListener,
        RongIM.UserInfoProvider, RongIM.GroupInfoProvider,
        RongIM.ConversationBehaviorListener,
        RongIMClient.ConnectionStatusListener, RongIM.LocationProvider,
        RongIM.ConversationListBehaviorListener, Handler.Callback,
        RongIM.GroupUserInfoProvider {

    private static RongCloudEvent mRongCloudInstance;

    private Context mContext;
    private Handler mHandler;
    private static UnReadCountListener mCountListener;

    public CheckFriendListener checkFriendListener;

    public GroupNoticeListener groupNoticeListener;

    public UnReadCountListener getmCountListener() {
        return mCountListener;
    }

    public void setmCountListener(UnReadCountListener mCountListener) {
        this.mCountListener = mCountListener;
    }


    public void setCheckFriendListener(CheckFriendListener checkFriendListener) {
        this.checkFriendListener = checkFriendListener;
    }

    public GroupNoticeListener getGroupNoticeListener() {
        return groupNoticeListener;
    }

    public void setGroupNoticeListener(GroupNoticeListener groupNoticeListener) {
        this.groupNoticeListener = groupNoticeListener;
    }

    /**
     * 初始化 RongCloud.
     *
     * @param context 上下文。
     */
    public static void init(Context context) {

        if (mRongCloudInstance == null) {

            synchronized (RongCloudEvent.class) {

                if (mRongCloudInstance == null) {
                    mRongCloudInstance = new RongCloudEvent(context);
                }
            }
        }
    }

    /**
     * 构造方法。
     *
     * @param context 上下文。
     */
    private RongCloudEvent(Context context) {
        mContext = context;
        initDefaultListener();
        mHandler = new Handler(this);
        setOtherListener();
    }

    @Override
    public boolean onConversationPortraitClick(Context context,
                                               Conversation.ConversationType conversationType, String targetId) {
        return false;
    }

    @Override
    public boolean onConversationPortraitLongClick(Context context,
                                                   Conversation.ConversationType conversationType, String targetId) {
        return false;
    }

    /**
     * 获取RongCloud 实例。
     *
     * @return RongCloud。
     */
    public static RongCloudEvent getInstance() {
        return mRongCloudInstance;
    }

    /**
     * RongIM.init(this) 后直接可注册的Listener。
     */
    private void initDefaultListener() {

        RongIM.setUserInfoProvider(this, true);// 设置用户信息提供者。
        RongIM.setGroupInfoProvider(this, true);// 设置群组信息提供者。
        RongIM.setConversationBehaviorListener(this);// 设置会话界面操作的监听器。
//        RongIM.setLocationProvider(this);// 设置地理位置提供者,不用位置的同学可以注掉此行代码
        RongIM.setConversationListBehaviorListener(this);// 会话列表界面操作的监听器
        RongIM.getInstance().setSendMessageListener(this);// 设置发出消息接收监听器.

        RongIM.setGroupUserInfoProvider(this, true);
        RongIM.setOnReceiveMessageListener(this);//自定义 push 通知。
        // 消息体内是否有 userinfo 这个属性
        RongIM.getInstance().setMessageAttachedUserInfo(true);
    }

    /**
     * 连接成功注册。
     * <p/>
     * 在RongIM-connect-onSuccess后调用。
     */
    public void setOtherListener() {

        RongIM.setOnReceiveMessageListener(this);// 设置消息接收监听器。
        List<IExtensionModule> moduleList = RongExtensionManager.getInstance().getExtensionModules();
        IExtensionModule defaultModule = null;
        if (moduleList != null) {
            for (IExtensionModule module : moduleList) {
                if (module instanceof DefaultExtensionModule) {
                    defaultModule = module;
                    break;
                }
            }
            if (defaultModule != null) {
                RongExtensionManager.getInstance().unregisterExtensionModule(defaultModule);
                RongExtensionManager.getInstance().registerExtensionModule(new ExtensionModule());
            }
        }

    }

    private Bitmap getAppIcon() {
        BitmapDrawable bitmapDrawable;
        Bitmap appIcon;
        bitmapDrawable = (BitmapDrawable) RongContext.getInstance()
                .getApplicationInfo()
                .loadIcon(RongContext.getInstance().getPackageManager());
        appIcon = bitmapDrawable.getBitmap();
        return appIcon;
    }

    /**
     * 接收消息的监听器：OnReceiveMessageListener 的回调方法，接收到消息后执行。
     *
     * @param message 接收到的消息的实体信息。
     * @param left    剩余未拉取消息数目。
     */
    @Override
    public boolean onReceived(Message message, int left) {
        if (message != null) {
            if (mCountListener != null && RongIM.getInstance().getRongIMClient() != null) {
//                ToastUtil.show("消息数："+RongIM.getInstance().getRongIMClient().getTotalUnreadCount());
                mCountListener.onCheckMsgCount();
            }
        }
        MessageContent messageContent = message.getContent();
        if (messageContent instanceof TextMessage) {// 文本消息
            TextMessage textMessage = (TextMessage) messageContent;
            LogUtils.d(this.getClass(),"onReceived-TextMessage:" + textMessage.getContent());
        } else if (messageContent instanceof ImageMessage) {// 图片消息
            ImageMessage imageMessage = (ImageMessage) messageContent;
            LogUtils.d( this.getClass(),"onReceived-ImageMessage:" + imageMessage.getRemoteUri());
        } else if (messageContent instanceof VoiceMessage) {// 语音消息
            VoiceMessage voiceMessage = (VoiceMessage) messageContent;
            LogUtils.d(this.getClass(),"onReceived-voiceMessage:"
                    + voiceMessage.getUri().toString());
        } else if (messageContent instanceof RichContentMessage) {// 图文消息
            RichContentMessage richContentMessage = (RichContentMessage) messageContent;
            LogUtils.d(this.getClass(),
                    "onReceived-RichContentMessage:"
                            + richContentMessage.getContent());
        } else if (messageContent instanceof InformationNotificationMessage) {// 小灰条消息
            InformationNotificationMessage informationNotificationMessage = (InformationNotificationMessage) messageContent;
        } else if (messageContent instanceof ContactNotificationMessage) {// 好友添加消息

        } else if (messageContent instanceof DiscussionNotificationMessage) {// 讨论组通知消息
            DiscussionNotificationMessage discussionNotificationMessage = (DiscussionNotificationMessage) messageContent;
            // LogUtils.d(this.getClass(), "onReceived-discussionNotificationMessage:getExtra;"
            // + discussionNotificationMessage.getOperator());
            // setDiscussionName(message.getTargetId());
        } else if (messageContent instanceof CommandMessage) {
            CommandMessage commandMessage = (CommandMessage) messageContent;
        } else if (messageContent instanceof WPNoticeMessage) {
            //公告消息
            if (groupNoticeListener != null) {
                groupNoticeListener.onNewNotice(((WPNoticeMessage) messageContent).getCircleID());
            }
        } else if (messageContent instanceof WPShortVideoMessage) {
            //视频消息
            LogUtils.e("视频消息");
        } else {
            LogUtils.d(this.getClass(), "onReceived-其他消息，自己来判断处理");
        }

        return false;

    }

    /**
     * 消息发送前监听器处理接口（是否发送成功可以从SentStatus属性获取）。
     *
     * @param message 发送的消息实例。
     * @return 处理后的消息实例。
     */
    @Override
    public Message onSend(Message message) {

        MessageContent messageContent = message.getContent();
//
//        if (messageContent instanceof TextMessage) {// 文本消息
//            TextMessage textMessage = (TextMessage) messageContent;
//            LogUtils.e("写字派--onSend:" + textMessage.getContent() + ", extra="
//                    + message.getExtra());
//        } else if (messageContent instanceof WPShortVideoMessage) {// 视频消息
//            WPShortVideoMessage wpShortVideoMessage = (WPShortVideoMessage) messageContent;
//            LogUtils.e("写字派--onSend:----》base64=" + wpShortVideoMessage.getImageContent());
//            LogUtils.e("url="
//                    + wpShortVideoMessage.getFileUri());
//        }
        if (messageContent instanceof WPNoticeMessage) {
            //公告消息
            if (groupNoticeListener != null) {
                groupNoticeListener.onNewNotice(((WPNoticeMessage) messageContent).getCircleID());
            }
        }
        return message;
    }

    /**
     * 消息在UI展示后执行/自己的消息发出后执行,无论成功或失败。
     *
     * @param message 消息。
     */
    @Override
    public boolean onSent(Message message,
                          RongIM.SentMessageErrorCode sentMessageErrorCode) {

        if (message.getSentStatus() == Message.SentStatus.FAILED) {

            if (sentMessageErrorCode == RongIM.SentMessageErrorCode.NOT_IN_CHATROOM) {// 不在聊天室

            } else if (sentMessageErrorCode == RongIM.SentMessageErrorCode.NOT_IN_DISCUSSION) {// 不在讨论组

            } else if (sentMessageErrorCode == RongIM.SentMessageErrorCode.NOT_IN_GROUP) {// 不在群组

            } else if (sentMessageErrorCode == RongIM.SentMessageErrorCode.REJECTED_BY_BLACKLIST) {// 你在他的黑名单中
                ToastUtil.show("你在对方的黑名单中");
            }
            // else if (sentMessageErrorCode ==
            // RongIM.SentMessageErrorCode.NOT_FOLLOWED) {//未关注此公众号
            // WinToast.toast(mContext, "未关注此公众号");
            // }
        }

        MessageContent messageContent = message.getContent();

        if (messageContent instanceof TextMessage) {// 文本消息
            TextMessage textMessage = (TextMessage) messageContent;
            LogUtils.e(this.getClass(), "onSent-TextMessage:" + textMessage.getContent());
        } else if (messageContent instanceof ImageMessage) {// 图片消息
            ImageMessage imageMessage = (ImageMessage) messageContent;
            LogUtils.d(this.getClass(), "onSent-ImageMessage:" + imageMessage.getRemoteUri());
        } else if (messageContent instanceof VoiceMessage) {// 语音消息
            VoiceMessage voiceMessage = (VoiceMessage) messageContent;
            LogUtils.d(this.getClass(), "onSent-voiceMessage:"
                    + voiceMessage.getUri().toString());
        } else if (messageContent instanceof RichContentMessage) {// 图文消息
            RichContentMessage richContentMessage = (RichContentMessage) messageContent;
            LogUtils.d(this.getClass(),
                    "onSent-RichContentMessage:"
                            + richContentMessage.getContent());
        } else if (messageContent instanceof WPNoticeMessage) {
            LogUtils.d(this.getClass(), "通知公告消息");
        }
        return false;
    }

    /**
     * 用户信息的提供者：GetUserInfoProvider 的回调方法，获取用户信息。
     *
     * @param userId 用户 Id。
     * @return 用户信息，（注：由开发者提供用户信息）。
     */
    @Override
    public UserInfo getUserInfo(String userId) {
        LogUtils.e("======获取用户信息======");
        loadUserInfoData(userId);
        return null;
    }

    /**
     * 群组信息的提供者：GetGroupInfoProvider 的回调方法， 获取群组信息。
     *
     * @param groupId 群组 Id.
     * @return 群组信息，（注：由开发者提供群组信息）。
     */
    @Override
    public Group getGroupInfo(String groupId) {
        loadGroupInfoData(groupId);
        return null;
    }

    /**
     * 会话界面操作的监听器：ConversationBehaviorListener 的回调方法，当点击用户头像后执行。
     *
     * @param context          应用当前上下文。
     * @param conversationType 会话类型。
     * @param user             被点击的用户的信息。
     * @return 返回True不执行后续SDK操作，返回False继续执行SDK操作。
     */
    @Override
    public boolean onUserPortraitClick(Context context,
                                       Conversation.ConversationType conversationType, UserInfo user) {

        if (user != null) {
            if (conversationType
                    .equals(Conversation.ConversationType.PUBLIC_SERVICE)
                    || conversationType
                    .equals(Conversation.ConversationType.APP_PUBLIC_SERVICE)) {
                RongIM.getInstance().startPublicServiceProfile(mContext,
                        conversationType, user.getUserId());
            } else {
                Intent in = new Intent(context, UserInfoActivity.class);
                in.putExtra("user_id", user.getUserId());
                context.startActivity(in);
            }
        }

        return false;
    }

    @Override
    public boolean onUserPortraitLongClick(Context context,
                                           Conversation.ConversationType conversationType, UserInfo userInfo) {
//        TextInputProvider textInputProvider= (TextInputProvider) RongContext.getInstance().getPrimaryInputProvider();
//        //重置文本框数据
//        textInputProvider.setEditTextContent("@"+userInfo.getName());
        return true;
    }

    /**
     * 会话界面操作的监听器：ConversationBehaviorListener 的回调方法，当点击消息时执行。
     *
     * @param context 应用当前上下文。
     * @param message 被点击的消息的实体信息。
     * @return 返回True不执行后续SDK操作，返回False继续执行SDK操作。
     */
    @Override
    public boolean onMessageClick(final Context context, final View view,
                                  final Message message) {
        LogUtils.e(this.getClass(), "----onMessageClick");

        // real-time location message begin
        if (message.getContent() instanceof RealTimeLocationStartMessage) {

            return true;
        }

        // real-time location message end
        /**
         * demo 代码 开发者需替换成自己的代码。
         */
        if (message.getContent() instanceof LocationMessage) {
            /**
             * 用户点击的消息为地理位置类型时 做相应的页面跳转展示
             */
        } else if (message.getContent() instanceof RichContentMessage) {
            RichContentMessage mRichContentMessage = (RichContentMessage) message
                    .getContent();
            LogUtils.d(this.getClass(), "extra:" + mRichContentMessage.getExtra());

        } else if (message.getContent() instanceof ImageMessage) {

        } else if (message.getContent() instanceof PublicServiceMultiRichContentMessage) {
            LogUtils.e(this.getClass(), "----PublicServiceMultiRichContentMessage-------");

        } else if (message.getContent() instanceof PublicServiceRichContentMessage) {
            LogUtils.e(this.getClass(), "----PublicServiceRichContentMessage-------");

        }

        LogUtils.d(this.getClass(),
                message.getObjectName() + ":" + message.getMessageId());

        return false;
    }

    private void startRealTimeLocation(Context context,
                                       Conversation.ConversationType conversationType, String targetId) {
        // RongIMClient.getInstance().startRealTimeLocation(conversationType,
        // targetId);
        //
        // Intent intent = new Intent(((FragmentActivity) context),
        // RealTimeLocationActivity.class);
        // intent.putExtra("conversationType", conversationType.getValue());
        // intent.putExtra("targetId", targetId);
        // context.startActivity(intent);
    }

    private void joinRealTimeLocation(Context context,
                                      Conversation.ConversationType conversationType, String targetId) {
        // RongIMClient.getInstance().joinRealTimeLocation(conversationType,
        // targetId);
        //
        // Intent intent = new Intent(((FragmentActivity) context),
        // RealTimeLocationActivity.class);
        // intent.putExtra("conversationType", conversationType.getValue());
        // intent.putExtra("targetId", targetId);
        // context.startActivity(intent);
    }

    /**
     * 当点击链接消息时执行。
     *
     * @param context 上下文。
     * @param link    被点击的链接。
     * @return 如果用户自己处理了点击后的逻辑处理，则返回 true， 否则返回 false, false 走融云默认处理方式。
     */
    @Override
    public boolean onMessageLinkClick(Context context, String link) {
        return false;
    }

    @Override
    public boolean onMessageLongClick(Context context, View view,
                                      Message message) {

        LogUtils.e(this.getClass(), "----onMessageLongClick");
        return false;
    }

    /**
     * 连接状态监听器，以获取连接相关状态:ConnectionStatusListener 的回调方法，网络状态变化时执行。
     *
     * @param status 网络状态。
     */
    @Override
    public void onChanged(ConnectionStatus status) {
        LogUtils.d(this.getClass(), "onChanged:" + status);
        if (status.getMessage().equals(
                ConnectionStatus.DISCONNECTED.getMessage())) {
        }
    }

    /**
     * 位置信息提供者:LocationProvider 的回调方法，打开第三方地图页面。
     *
     * @param context  上下文
     * @param callback 回调
     */
    @Override
    public void onStartLocation(Context context, LocationCallback callback) {
        /**
         * 跳转至自定义的地图定位页面
         */

    }

    /**
     * 点击会话列表 item 后执行。
     *
     * @param context      上下文。
     * @param view         触发点击的 View。
     * @param conversation 会话条目。
     * @return 返回 true 不再执行融云 SDK 逻辑，返回 false 先执行融云 SDK 逻辑再执行该方法。
     */
    @Override
    public boolean onConversationClick(Context context, View view,
                                       UIConversation conversation) {
        MessageContent messageContent = conversation.getMessageContent();

        LogUtils.e(this.getClass(), "--------onConversationClick-------");
        String targetId = conversation.getConversationTargetId();//发送者的ID

        if (messageContent instanceof TextMessage) {// 文本消息
            TextMessage textMessage = (TextMessage) messageContent;
            textMessage.getExtra();

        } else if (messageContent instanceof ContactNotificationMessage) {
            LogUtils.e(this.getClass(), "---onConversationClick--ContactNotificationMessage-");

            // context.startActivity(new Intent(context,
            // NewFriendListActivity.class));
            return true;
        }

        return false;
    }

    /**
     * 长按会话列表 item 后执行。
     *
     * @param context      上下文。
     * @param view         触发点击的 View。
     * @param conversation 长按会话条目。
     * @return 返回 true 不再执行融云 SDK 逻辑，返回 false 先执行融云 SDK 逻辑再执行该方法。
     */
    @Override
    public boolean onConversationLongClick(Context context, View view,
                                           UIConversation conversation) {
        return false;
    }

    @Override
    public boolean handleMessage(android.os.Message message) {
        return false;
    }

    @Override
    public GroupUserInfo getGroupUserInfo(String groupId, String userId) {

        return null;
    }

    //检查好友关系
    private void checkIsFriend(String userId) {
        RongYunCheckFriendParams params = new RongYunCheckFriendParams(userId);
        RequestManager.request(mContext, userId, params,
                RongYunCheckFriendResponse.class, new WritePreListener<BaseResponse>() {
                    @Override
                    public void onResponse(BaseResponse response) {

                    }

                    @Override
                    public void onResponse(String tag, BaseResponse response) {
                        if ("0".equals(response.getResCode())) {
                            RongYunCheckFriendResponse checkFriendResponse = (RongYunCheckFriendResponse) response;
                            if (!checkFriendResponse.getRepBody().getRes().equals("ok")) {
                                if (checkFriendListener != null) {
                                    LogUtils.e("=====发送好友关系回调=====");
                                    checkFriendListener.onIsFriend(false, tag);
                                }
                            }
                        }
                    }
                }, SPUtils.instance()
                        .getSocialPropEntity().getApp_socail_server());
    }

    //获取群组信息
    private void loadGroupInfoData(String group_id) {
        RequestManager.request(mContext, new CircleGetDetailParams(group_id),
                CircleDetailResponse.class, new WritePreListener<BaseResponse>() {
                    @Override
                    public void onResponse(BaseResponse response) {
                        if ("0".equals(response.getResCode())) {
                            if (response instanceof CircleDetailResponse) {
                                CircleDetailResponse circleDetailResponse = (CircleDetailResponse) response;
                                if (circleDetailResponse != null) {
                                    com.easier.writepre.entity.CircleDetail body = circleDetailResponse.getRepBody();
                                    String headImageUrl = StringUtil.getImgeUrl(body
                                            .getFace_url());
                                    if (RongIM.getInstance() != null) {
                                        RongIM.getInstance().refreshGroupInfoCache(new Group(body.get_id(), body.getName(), TextUtils.isEmpty(headImageUrl) ? null : Uri.parse(headImageUrl)));
                                    }
                                }

                            }
                        }
                    }

                    @Override
                    public void onResponse(String tag, BaseResponse response) {

                    }
                }, SPUtils.instance()
                        .getSocialPropEntity().getApp_socail_server());

    }

    private void loadUserInfoData(String user_id) {
        RequestManager.request(mContext, new UserInfoParams(user_id),
                UserInfoResponse.class, new WritePreListener<BaseResponse>() {
                    @Override
                    public void onResponse(BaseResponse response) {
                        if ("0".equals(response.getResCode())) {
                            if (response instanceof UserInfoResponse) {
                                UserInfoResponse userInfoResult = (UserInfoResponse) response;
                                if (userInfoResult != null) {
                                    com.easier.writepre.entity.UserInfo body = userInfoResult.getRepBody();
                                    String headImageUrl = StringUtil.getHeadUrl(body
                                            .getHead_img());
                                    if (RongIM.getInstance() != null) {
                                        RongIM.getInstance().refreshUserInfoCache(new UserInfo(body.get_id(), body.getUname(), TextUtils.isEmpty(headImageUrl) ? null : Uri.parse(headImageUrl)));
                                    }
                                }

                            }
                        }
                    }

                    @Override
                    public void onResponse(String tag, BaseResponse response) {

                    }
                }, SPUtils.instance()
                        .getSocialPropEntity().getApp_socail_server());
    }

}
