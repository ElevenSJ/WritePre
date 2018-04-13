package com.easier.writepre.rongyun;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.app.RongCloudEvent;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.entity.GroupNoticeInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.GroupNoticeAddPostParams;
import com.easier.writepre.param.GroupNoticePostNewParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.GroupNoticeAddPostResponse;
import com.easier.writepre.response.GroupNoticePostNewResponse;
import com.easier.writepre.response.RongYunCheckFriendResponse;
import com.easier.writepre.response.RongYunCheckInGroupResponse;
import com.easier.writepre.ui.BaseActivity;
import com.easier.writepre.ui.CircleDetailActivity;
import com.easier.writepre.ui.GroupNoticeListActivity;
import com.easier.writepre.utils.Bimp;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.utils.Utils;
import com.sj.autolayout.utils.DateKit;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.mention.IMentionedInputListener;
import io.rong.imkit.mention.RongMentionManager;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.MessageTag;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.TypingMessage.TypingStatus;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import io.rong.message.utils.BitmapUtil;

/**
 * 会话页面
 *
 * @author zhoulu
 */
public class RongYunConversationActivity extends BaseActivity implements CheckFriendListener, GroupNoticeListener {
    private String title;
    private String mTargetId;
    private String type;//0默认 1半透明
    private RelativeLayout ll_chat;//布局文件
    public static final int SET_TEXT_TYPING_TITLE = 0;
    public static final int SET_VOICE_TYPING_TITLE = 1;
    public static final int SET_TARGETID_TITLE = 2;
    public static final int SET_UPLOAD_OSS = 3;//上传到OSS
    public static final int SEND_VIDOEO_SUCCESS = 4;//视频发送成功
    public static final int GET_NOTICE_DATA = 5;//获取通知数据
    public static final int GET_NOTICE_SUCCESS = 6;//获取通知成功
    public static final int GET_NOTICE_FAIL = 7;//获取通知失败
    private ImageView flag_drawer;//下拉通知显示开关
    private RelativeLayout rel_topNotice;//下拉通知布局
    private RelativeLayout rel_topNoticeContent;//通知内容布局
    private RelativeLayout rel_loading;//公告加载提示
    private LinearLayout ll_images, ll_qiandao;
    private TextView tv_notice_title, tv_image_count, tv_time, tv_notice_null;
    private Button btn_daka;
    private boolean isLoadingNotice;
    private List<GroupNoticeInfo> groupNoticeInfoList;
    private GroupNoticeInfo currentGroupNoticeInfo;
    private boolean isFromPush;
    private ConversationFragmentEx fragment;
    /**
     * 刚刚创建完讨论组后获得讨论组的id 为targetIds，需要根据 为targetIds 获取 targetId
     */
    private String mTargetIds;
    /**
     * 会话类型
     */
    private Conversation.ConversationType mConversationType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        RongCloudEvent.getInstance().setGroupNoticeListener(this);
        ll_chat = (RelativeLayout) findViewById(R.id.ll_chat);
        rel_topNotice = (RelativeLayout) findViewById(R.id.rel_topNotice);
        rel_topNotice.setOnClickListener(this);
        rel_loading = (RelativeLayout) findViewById(R.id.rel_loading);
        tv_notice_null = (TextView) findViewById(R.id.tv_notice_null);
        rel_topNoticeContent = (RelativeLayout) findViewById(R.id.rel_topNoticeContent);
        ll_images = (LinearLayout) findViewById(R.id.ll_images);
        ll_qiandao = (LinearLayout) findViewById(R.id.ll_qiandao);
        tv_time = (TextView) findViewById(R.id.time);
        tv_notice_title = (TextView) findViewById(R.id.tv_notice_title);
        tv_image_count = (TextView) findViewById(R.id.tv_image_count);
        btn_daka = (Button) findViewById(R.id.btn_daka);
        ll_qiandao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentGroupNoticeInfo != null) {
                    requestLookNotice(currentGroupNoticeInfo.get_id(), currentGroupNoticeInfo.get_id());
                }
            }
        });
        flag_drawer = (ImageView) findViewById(R.id.flag_drawer);
        flag_drawer.setTag(rel_topNotice.getVisibility() == View.VISIBLE ? true : false);
        Intent intent = getIntent();
        getIntentDate(intent);
        isPush(intent);
        if (mConversationType == Conversation.ConversationType.GROUP) {
            flag_drawer.setVisibility(View.VISIBLE);
            flag_drawer.setOnClickListener(this);
            setTopRightTxt("详情");
            // 检查是否是群成员
            LogUtils.e("onCreate:" + mTargetIds);
            RongYunUtils.getInstances().requestCheckIsInGroup(
                    this,
                    mTargetId,
                    this);
            RongMentionManager.getInstance().setMentionedInputListener(new IMentionedInputListener() {
                @Override
                public boolean onMentionedInput(Conversation.ConversationType conversationType, String id) {
                    Intent tellSomeOne = new Intent(RongYunConversationActivity.this, RongYunFriendsListActivity.class);
                    tellSomeOne.putExtra("selectType", RongYunFriendsListActivity.SELECT_SOMEONE);
                    tellSomeOne.putExtra("groupId", id);
                    RongYunConversationActivity.this.startActivity(tellSomeOne);
                    return true;
                }
            });
        } else {
            flag_drawer.setVisibility(View.GONE);
            flag_drawer.setOnClickListener(null);
            RongCloudEvent.getInstance().setCheckFriendListener(this);
            getInputStauts();
            checkIsFriend();
            setTopRightTxt(null);
        }
        isNeedsShowNotice(mTargetId);
    }

    private boolean isTouchPointInView(RelativeLayout view, int x, int y) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        if (y >= top && y <= bottom && x >= left
                && x <= right) {
            return true;
        }
        return false;
    }

    private boolean isTouchPointInView(ImageView view, int x, int y) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        if (y >= top && y <= bottom && x >= left
                && x <= right) {
            return true;
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                if (!isTouchPointInView(rel_topNotice, (int) ev.getRawX(), (int) ev.getRawY()) && !isTouchPointInView(flag_drawer, (int) ev.getRawX(), (int) ev.getRawY())) {
                    goneNoticeView();
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    //隐藏公告栏
    private void goneNoticeView() {
        if ((boolean) flag_drawer.getTag()) {
            rel_topNotice.setVisibility(View.GONE);
            flag_drawer.setBackgroundResource(R.drawable.notice_downflag);
        }
        flag_drawer.setTag(rel_topNotice.getVisibility() == View.VISIBLE ? true : false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        RongCloudEvent.getInstance().setGroupNoticeListener(this);
        getIntentDate(intent);
        isPush(intent);
        if (mConversationType == Conversation.ConversationType.GROUP) {
            flag_drawer.setVisibility(View.VISIBLE);
            flag_drawer.setOnClickListener(this);
            setTopRightTxt("详情");
            // 检查是否是群成员
            LogUtils.e("onNewIntent");
            RongYunUtils.getInstances().requestCheckIsInGroup(
                    this,
                    mTargetId,
                    this);
        } else {
            flag_drawer.setVisibility(View.GONE);
            flag_drawer.setOnClickListener(null);
            RongCloudEvent.getInstance().setCheckFriendListener(this);
            getInputStauts();
            checkIsFriend();
            setTopRightTxt(null);
        }
        isNeedsShowNotice(mTargetId);
    }

    /**
     * 判断是否需要显示某个群的公告
     *
     * @param id
     */
    private void isNeedsShowNotice(final String id) {
        if (RongIM.getInstance() != null && !TextUtils.isEmpty(id)) {
            if (mConversationType == Conversation.ConversationType.GROUP) {
                RongIM.getInstance().getLatestMessages(Conversation.ConversationType.GROUP, id, 1, new RongIMClient.ResultCallback<List<io.rong.imlib.model.Message>>() {
                    @Override
                    public void onSuccess(List<io.rong.imlib.model.Message> messages) {
                        if (messages != null && !messages.isEmpty()) {
                            if (TextUtils.equals("WP:NtcNtf", messages.get(0).getObjectName())) {
                                if (!messages.get(0).getReceivedStatus().isDownload()) {
                                    mHandler.sendEmptyMessage(GET_NOTICE_DATA);
                                    io.rong.imlib.model.Message.ReceivedStatus receivedStatus = messages.get(0).getReceivedStatus();
                                    receivedStatus.setDownload();
                                    RongIM.getInstance().setMessageReceivedStatus(messages.get(0).getMessageId(), receivedStatus, null);
                                }
                            } else {
                                RongIM.getInstance().getHistoryMessages(Conversation.ConversationType.GROUP, "WP:NtcNtf", id, messages.get(0).getMessageId(), 1, new RongIMClient.ResultCallback<List<io.rong.imlib.model.Message>>() {
                                    @Override
                                    public void onSuccess(List<io.rong.imlib.model.Message> messages) {
                                        if (messages != null && !messages.isEmpty()) {
                                            if (TextUtils.equals("WP:NtcNtf", messages.get(0).getObjectName())) {
                                                if (!messages.get(0).getReceivedStatus().isDownload()) {
                                                    mHandler.sendEmptyMessage(GET_NOTICE_DATA);
                                                    io.rong.imlib.model.Message.ReceivedStatus receivedStatus = messages.get(0).getReceivedStatus();
                                                    receivedStatus.setDownload();
                                                    RongIM.getInstance().setMessageReceivedStatus(messages.get(0).getMessageId(), receivedStatus, null);
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode errorCode) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
            }
        }
    }

    //确认查看公告
    public void requestLookNotice(String id, String pub_news_id) {
        RequestManager.request(this, id, new GroupNoticeAddPostParams(pub_news_id), GroupNoticeAddPostResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    @Override
    public void onTopRightTxtClick(View view) {
        super.onTopRightTxtClick(view);
        Intent intent = new Intent(this, CircleDetailActivity.class);
        intent.putExtra("circle_id", mTargetId);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        flag_drawer.setTag(rel_topNotice.getVisibility() == View.VISIBLE ? true : false);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SET_TEXT_TYPING_TITLE:
                    setTopTitle("对方正在输入");
                    break;
                case SET_VOICE_TYPING_TITLE:
                    setTopTitle("对方正在讲话");
                    break;
                case SET_TARGETID_TITLE:
                    setTopTitle(title);
                    break;
                case SET_UPLOAD_OSS:
                    //上传视频到OSS
                    commitVideoOss((String) msg.obj, mHandler);
                    break;
                case COMMIT_VIDEO_OSS_SUCCESS:
                    //发送视频消息
                    Bundle bundle = msg.getData();
                    String videoFilePath = (String) bundle.get("filePath");
                    String videoOssPath = (String) bundle.get("ossPath");
                    mHandler.post(new MyFileSendRunnable(videoFilePath, videoOssPath));
                    break;
                case COMMIT_VIDEO_OSS_FAIL:
                    dlgLoad.dismissDialog();
                    ToastUtil.show("小视频发送失败!");
                    break;
                case SEND_VIDOEO_SUCCESS:
                    dlgLoad.dismissDialog();
                    break;
                case GET_NOTICE_DATA:
                    //新通知到来
                    if (mConversationType == Conversation.ConversationType.GROUP) {
                        if ((boolean) flag_drawer.getTag()) {
                            if (!isLoadingNotice) {
                                rel_loading.setVisibility(View.VISIBLE);
                                tv_notice_null.setVisibility(View.GONE);
                                rel_topNoticeContent.setVisibility(View.GONE);
                                getNoticeData();
                            }
                        } else {
                            flag_drawer.performClick();
                        }
                    }
                    break;
                case GET_NOTICE_SUCCESS:
                    break;
                case GET_NOTICE_FAIL:
                    break;
            }
        }
    };

    private void getInputStauts() {
        RongIMClient.setTypingStatusListener(new RongIMClient.TypingStatusListener() {
            @Override
            public void onTypingStatusChanged(Conversation.ConversationType type, String targetId, Collection<TypingStatus> typingStatusSet) {
                //当输入状态的会话类型和targetID与当前会话一致时，才需要显示
                if (type.equals(mConversationType) && targetId.equals(mTargetId)) {
                    //count表示当前会话中正在输入的用户数量，目前只支持单聊，所以判断大于0就可以给予显示了
                    int count = typingStatusSet.size();
                    if (count > 0) {
                        Iterator iterator = typingStatusSet.iterator();
                        TypingStatus status = (TypingStatus) iterator.next();
                        String objectName = status.getTypingContentType();

                        MessageTag textTag = TextMessage.class.getAnnotation(MessageTag.class);
                        MessageTag voiceTag = VoiceMessage.class.getAnnotation(MessageTag.class);
                        //匹配对方正在输入的是文本消息还是语音消息
                        if (objectName.equals(textTag.value())) {
                            //显示“对方正在输入”
                            mHandler.sendEmptyMessage(SET_TEXT_TYPING_TITLE);
                        } else if (objectName.equals(voiceTag.value())) {
                            //显示"对方正在讲话"
                            mHandler.sendEmptyMessage(SET_VOICE_TYPING_TITLE);
                        }
                    } else {
                        //当前会话没有用户正在输入，标题栏仍显示原来标题
                        mHandler.sendEmptyMessage(SET_TARGETID_TITLE);
                    }
                }
            }
        });
    }

    /**
     * 展示如何从 Intent 中得到 融云会话页面传递的 Uri
     */
    private void getIntentDate(Intent intent) {
        title = intent.getData().getQueryParameter("title");
        setTopTitle(title);
        type = intent.getData().getQueryParameter("type");
        mTargetId = intent.getData().getQueryParameter("targetId");
        mTargetIds = intent.getData().getQueryParameter("targetIds");
        // intent.getData().getLastPathSegment();//获得当前会话类型
        mConversationType = Conversation.ConversationType.valueOf(intent
                .getData().getLastPathSegment()
                .toUpperCase(Locale.getDefault()));

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.flag_drawer:
                if ((boolean) flag_drawer.getTag()) {
                    //隐藏公告栏
                    rel_topNotice.setVisibility(View.GONE);
                    flag_drawer.setBackgroundResource(R.drawable.notice_downflag);
                } else {
                    //显示公告栏
                    rel_topNotice.setVisibility(View.VISIBLE);
                    flag_drawer.setBackgroundResource(R.drawable.notice_upwardflag);
                    if (!isLoadingNotice) {
                        rel_loading.setVisibility(View.VISIBLE);
                        tv_notice_null.setVisibility(View.GONE);
                        rel_topNoticeContent.setVisibility(View.GONE);
                        getNoticeData();
                    }
                }
                flag_drawer.setTag(rel_topNotice.getVisibility() == View.VISIBLE ? true : false);
                break;
            case R.id.rel_topNotice:
                //点击查看群公告列表
                Intent intent = new Intent(this, GroupNoticeListActivity.class);
                intent.putExtra("mTargetId", mTargetId);
                startActivity(intent);
                //隐藏公告栏
                rel_topNotice.setVisibility(View.GONE);
                flag_drawer.setBackgroundResource(R.drawable.notice_downflag);
                flag_drawer.setTag(rel_topNotice.getVisibility() == View.VISIBLE ? true : false);
                break;
        }
    }

    private void checkIsFriend() {
        RongYunUtils.getInstances().requestCheckIsFriend(this, mTargetId, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RongCloudEvent.getInstance().setCheckFriendListener(null);
    }

    /**
     * 加载会话页面 ConversationFragment
     *
     * @param mConversationType
     * @param mTargetId
     */
    @SuppressLint("NewApi")
    private void enterFragment(Conversation.ConversationType mConversationType,
                               String mTargetId) {
        fragment = new ConversationFragmentEx();
        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();
        fragment.setUri(uri);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.rong_content, fragment);
        transaction.commitAllowingStateLoss();
        if (TextUtils.equals("1", type)) {
            ll_chat.setBackgroundResource(R.color.transparent2);
        } else {
            ll_chat.setBackgroundResource(R.color.rc_normal_bg);
        }

    }

    /**
     * 判断消息是否是 push 消息
     */
    private void isPush(Intent intent) {

        String token = null;

        token = SPUtils.instance().getToken();

        // push或通知过来
        if (intent != null && intent.getData() != null
                && intent.getData().getScheme().equals("rong")) {

            // 通过intent.getData().getQueryParameter("push") 为true，判断是否是push消息
            if (intent.getData().getQueryParameter("push") != null
                    && intent.getData().getQueryParameter("push")
                    .equals("true")) {
                isFromPush = true;
                LogUtils.e("PUSH 消息到来");
                reconnect(token);
            } else {
                // 程序切到后台，收到消息后点击进入,会执行这里
                if (RongIM.getInstance() == null
                        || RongIM.getInstance().getRongIMClient() == null) {
                    reconnect(token);
                } else {
                    enterFragment(mConversationType, mTargetId);
                }
            }
        }
    }

    /**
     * 重连
     *
     * @param token
     */
    @SuppressLint("NewApi")
    private void reconnect(String token) {

        if (getApplicationInfo().packageName.equals(Utils.getCurProcessName(getApplicationContext()))) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {

                }

                @Override
                public void onSuccess(String s) {

                    enterFragment(mConversationType, mTargetId);
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
    }

    @Override
    public void onIsFriend(boolean isFriend, String userId) {
        LogUtils.e("收到好友关系回调");
        if (!isFriend) {
            ToastUtil.show("对方已不是你互相关注的好友!");
            this.finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    String videoPath = data.getStringExtra("video_path");
                    if (TextUtils.isEmpty(videoPath)) {
                        ToastUtil.show("获取视频文件失败");
                        return;
                    }
                    //上传Oss
                    mHandler.obtainMessage(SET_UPLOAD_OSS, videoPath).sendToTarget();
                }

            }
        }
    }

    //获取通知公告数据
    private void getNoticeData() {
        isLoadingNotice = true;
        GroupNoticePostNewParams groupNoticePostNewParams = new GroupNoticePostNewParams(mTargetId, "9", "1");
        RequestManager.request(this, groupNoticePostNewParams,
                GroupNoticePostNewResponse.class, this,
                SPUtils.instance().getSocialPropEntity()
                        .getApp_socail_server());
    }

    @Override
    public void onResponse(BaseResponse response) {
        super.onResponse(response);
        if ("0".equals(response.getResCode())) {
            if (response instanceof RongYunCheckFriendResponse) {
                RongYunCheckFriendResponse checkFriendResponse = (RongYunCheckFriendResponse) response;
                if (!checkFriendResponse.getRepBody().getRes().equals("ok")) {
                    ToastUtil.show("还不是相互关注的好友,不能发起私聊!");
                    RongYunConversationActivity.this.finish();
                }
            } else if (response instanceof GroupNoticePostNewResponse) {
                GroupNoticePostNewResponse groupNoticePostNewResponse = (GroupNoticePostNewResponse) response;
                if (groupNoticePostNewResponse.getRepBody() != null) {
                    groupNoticeInfoList = groupNoticePostNewResponse.getRepBody().getList();
                    if (groupNoticeInfoList != null && !groupNoticeInfoList.isEmpty()) {
                        currentGroupNoticeInfo = groupNoticeInfoList.get(0);
                    } else {
                        currentGroupNoticeInfo = null;
                    }
                    upDateNotice();
                }
                isLoadingNotice = false;
            }

        } else {
            ToastUtil.show(response.getResMsg());
            isLoadingNotice = false;
            upDateNotice();
        }
    }

    @Override
    public void onResponse(String tag, BaseResponse response) {
        super.onResponse(tag, response);
        if ("0".equals(response.getResCode())) {
            if (response instanceof GroupNoticeAddPostResponse) {
                if (currentGroupNoticeInfo != null) {
                    if (TextUtils.equals(tag, currentGroupNoticeInfo.get_id())) {
                        currentGroupNoticeInfo.setIs_viewed("1");
                        currentGroupNoticeInfo.setView_num((Integer.parseInt(currentGroupNoticeInfo.getView_num()) + 1) + "");
                        upDateNotice();
                    }
                }
            } else if (response instanceof RongYunCheckInGroupResponse) {
                RongYunCheckInGroupResponse r = (RongYunCheckInGroupResponse) response;
                if (!TextUtils.equals("ok", r.getRepBody().getRes())) {
                    ToastUtil.show("很抱歉您已不在该圈子!");
                }
            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
    }

    //设置下拉通知
    private void upDateNotice() {
        rel_loading.setVisibility(View.GONE);
        if (currentGroupNoticeInfo == null) {
            rel_topNoticeContent.setVisibility(View.GONE);
            tv_notice_null.setVisibility(View.VISIBLE);
            return;
        } else {
            tv_notice_null.setVisibility(View.GONE);
            rel_topNoticeContent.setVisibility(View.VISIBLE);
        }
        tv_notice_title.setText(currentGroupNoticeInfo.getTitle());
        tv_time.setText(currentGroupNoticeInfo.getCtime());
        if (currentGroupNoticeInfo.getImg_url() != null && currentGroupNoticeInfo.getImg_url().length > 0) {
            ll_images.setVisibility(View.VISIBLE);
            tv_image_count.setText(currentGroupNoticeInfo.getImg_url().length + "");
        } else {
            ll_images.setVisibility(View.GONE);
        }
        tv_time.setText(DateKit.Toymd(currentGroupNoticeInfo.getCtime()));
        if (TextUtils.equals("1", currentGroupNoticeInfo.getIs_viewed())) {
            findViewById(R.id.iv_image_daka).setEnabled(false);
            btn_daka.setEnabled(false);
            ll_qiandao.setEnabled(false);
        } else {
            findViewById(R.id.iv_image_daka).setEnabled(false);
            btn_daka.setEnabled(true);
            ll_qiandao.setEnabled(true);
        }
        btn_daka.setText(currentGroupNoticeInfo.getView_num());
    }

    @Override
    public void onNewNotice(String id) {
        if (TextUtils.equals(id, mTargetId)) {
            mHandler.sendEmptyMessage(GET_NOTICE_DATA);
        }

    }

    class MyFileSendRunnable implements Runnable {

        String netViedoUrl;
        String filePath;

        public MyFileSendRunnable(String filePath, String netViedoUrl) {
            this.filePath = filePath;
            this.netViedoUrl = netViedoUrl;
        }

        @Override
        public void run() {
            Bitmap bitmap = null;
            String localPath = FileUtils.SD_IMAGES_PATH + filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length() - 3) + ".png";
            LogUtils.e(localPath + "++++++++++++++++");
            try {
                Uri uri = Uri.fromFile(Bimp.saveBitmap(Bimp.getVideoThumbnail(filePath), localPath));
                bitmap = BitmapUtil.getResizedBitmap(RongYunConversationActivity.this, uri, 240, 240);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap == null) {
                return;
            }

            WPShortVideoMessage wpShortVideoMessage = WPShortVideoMessage.obtain(BitmapUtil.getBase64FromBitmap(bitmap), netViedoUrl);
            io.rong.imlib.model.Message message = io.rong.imlib.model.Message.obtain(mTargetId, mConversationType, wpShortVideoMessage);

            if (RongIM.getInstance() != null)
                RongIM.getInstance().sendMessage(message, "视频消息", "", new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(io.rong.imlib.model.Message message) {
                        LogUtils.e("onAttached=====");
                    }

                    @Override
                    public void onSuccess(io.rong.imlib.model.Message message) {
                        LogUtils.e("onSuccess=====");
                        mHandler.sendEmptyMessage(SEND_VIDOEO_SUCCESS);
                    }

                    @Override
                    public void onError(io.rong.imlib.model.Message message, RongIMClient.ErrorCode errorCode) {
                        LogUtils.e("onError======");
                        mHandler.sendEmptyMessage(COMMIT_VIDEO_OSS_FAIL);
                    }
                });
        }
    }
}
