package com.easier.writepre.mainview;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.MyListAdapter;
import com.easier.writepre.app.RongCloudEvent;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.db.DBPushMessageHelper;
import com.easier.writepre.entity.LoginEntity;
import com.easier.writepre.entity.MyListData;
import com.easier.writepre.entity.SocialPropEntity;
import com.easier.writepre.entity.UserInfo;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.http.WritePreListener;
import com.easier.writepre.param.EditInfoParam;
import com.easier.writepre.param.MessageCommParams;
import com.easier.writepre.param.MessageFeedBackIdParams;
import com.easier.writepre.param.MessageUserParams;
import com.easier.writepre.param.RongYunTokenParams;
import com.easier.writepre.param.UserInfoParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.EditMyInfoResponse;
import com.easier.writepre.response.NoticeFeedbackResponse;
import com.easier.writepre.response.RongYunTokenResponse;
import com.easier.writepre.response.SysPushMessageResponse;
import com.easier.writepre.response.UserInfoResponse;
import com.easier.writepre.response.UserPushMessageResponse;
import com.easier.writepre.rongyun.RongYunConversationListActivity;
import com.easier.writepre.rongyun.UnReadCountListener;
import com.easier.writepre.ui.ApplyForCertificationActvity;
import com.easier.writepre.ui.AttentionActivity;
import com.easier.writepre.ui.FoundBeiTieCalligrapherActivity;
import com.easier.writepre.ui.LoginActivity;
import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.ui.MyInfoActivity;
import com.easier.writepre.ui.MyPostActivity;
import com.easier.writepre.ui.NoSwipeBackActivity;
import com.easier.writepre.ui.PushMessageActivity;
import com.easier.writepre.ui.SearchFragmentActivity;
import com.easier.writepre.ui.SettingActivity;
import com.easier.writepre.ui.UserCircleActivity;
import com.easier.writepre.ui.UserFeedbackActivity;
import com.easier.writepre.ui.myinfo.EditBriefActivity;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.RoundImageView;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * 我的
 *
 * @author zhaomaohan
 */
public class MyMainView extends BaseView implements UnReadCountListener {
    private ListView lv_list;
    public static int left_image[] = new int[]{R.drawable.comment2x, R.drawable.my_search, R.drawable.my_message, R.drawable.renzheng, R.drawable.my_download,
            R.drawable.my_feedback, R.drawable.my_recommed, R.drawable.my_evaluate};
    //    public static String left_text[] = new String[]{"消息列表", "找朋友/老师", "通知", "我的申请认证", "离线碑帖", "用户反馈", "把写字派推荐给好友", "评价写字派"};
    public static String left_text[] = new String[]{"找朋友/老师", "聊天", "通知", "离线碑帖", "我的申请认证", "把写字派推荐给好友", "用户反馈", "评价写字派"};
    public static String right_button_text[] = new String[]{null, null, null, null, null, null, null, null};
    public static int right_button_background[] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1};
    public static int right_button_text_color[] = new int[]{-1, -1, -1, -1, -1, -1, -1, -1};

    /**
     * listview头布局
     */
    private RoundImageView imgHead;
    private TextView tvName;
    private TextView tvSign;
    private ImageView img_edit_sign;
    private LinearLayout layout_setting;
    private LinearLayout layoutUserInfo;

    private TextView tv_square, tv_circle, tv_fans, tv_attention;
    private String commLastId = "";
    private String userLastId = "";

    MyListAdapter adapter;

    private View headView;

    private UserInfo userInfoBody;

    public MyMainView(Context ctx) {
        super(ctx);
    }


    @Override
    public int getContextView() {
        return R.layout.my_main_view;
    }

    @Override
    public void initView() {

        lv_list = (ListView) findViewById(R.id.lv_list);
        // 注:Android4.3以下版本，使用addHeaderView()需要在setAdapter()之前，然后对Header做显示和隐藏处理。要注意这一点。
        addHeader();
        adapter = new MyListAdapter(mCtx, getData());
        lv_list.setAdapter(adapter);

        lv_list.setOnItemClickListener(this);
        getSystemNotice();
        getUserNotice();
        RongCloudEvent.getInstance().setmCountListener(this);
        getUserMsgCount();

    }

    private void addHeader() {
        headView = View.inflate(mCtx, R.layout.view_user_headlayout, null);
        lv_list.addHeaderView(headView);
        tv_square = (TextView) headView.findViewById(R.id.tv_square);
        tv_circle = (TextView) headView.findViewById(R.id.tv_circle);
        tv_fans = (TextView) headView.findViewById(R.id.tv_fans);
        tv_attention = (TextView) headView.findViewById(R.id.tv_attention);
        imgHead = (RoundImageView) headView.findViewById(R.id.iv_img_head);
        tvName = (TextView) headView.findViewById(R.id.tv_uname);
        tvSign = (TextView) headView.findViewById(R.id.tv_sign);
        img_edit_sign = (ImageView) headView.findViewById(R.id.img_edit_sign);
        layout_setting = (LinearLayout) headView.findViewById(R.id.setting_layout);
        layoutUserInfo = (LinearLayout) headView.findViewById(R.id.user_info_layout);

        findViewById(R.id.rel_square).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_post)).setText("帖子");
        findViewById(R.id.rel_circle).setOnClickListener(this);
        findViewById(R.id.rel_fans).setOnClickListener(this);
        findViewById(R.id.rel_attention).setOnClickListener(this);
        findViewById(R.id.cb_attention).setVisibility(View.INVISIBLE);
        layout_setting.setVisibility(View.VISIBLE);
        layout_setting.setOnClickListener(this);
        layoutUserInfo.setOnClickListener(this);
        img_edit_sign.setOnClickListener(this);
        LayoutParams layoutParams = new AbsListView.LayoutParams(WritePreApp.getApp().getWidth(1f),
                WritePreApp.getApp().getWidth(0.53f));
        headView.setLayoutParams(layoutParams);
    }

    /**
     * 判断是否登录，然后显示相应的界面
     */
    private void initLoginState() {

        if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
            if (userInfoBody != null) {
                img_edit_sign.setVisibility(View.VISIBLE);
                imgHead.setImageView(StringUtil.getHeadUrl(userInfoBody.getHead_img()));
                imgHead.setIconView(userInfoBody.getIs_teacher());
                tvName.setText(userInfoBody.getUname());
                if (!TextUtils.isEmpty(userInfoBody.getSignature())) {
                    tvSign.setText(userInfoBody.getSignature());
                } else {
                    tvSign.setText("这家伙很懒，什么也没留下！");
                }
                tv_square.setText(userInfoBody.getPost_cnt());
                tv_circle.setText(userInfoBody.getCircle_cnt());
                tv_fans.setText(userInfoBody.getCare_me_num());
                tv_attention.setText(userInfoBody.getMy_care_num());
            } else {
                LoginEntity loginEntity = SPUtils.instance().getLoginEntity();
                imgHead.setImageView(StringUtil.getHeadUrl(loginEntity.getHead_img()));
                imgHead.setIconView(loginEntity.getIs_teacher());
                tvName.setText(loginEntity.getUname());
                if (!TextUtils.isEmpty(loginEntity.getSignature())) {
                    tvSign.setText(loginEntity.getSignature());
                } else {
                    tvSign.setText("这家伙很懒，什么也没留下！");
                }
            }
            tvName.setTextAppearance(mCtx, R.style.SocialNormalTxtWhite);
        } else {
            imgHead.setImageResource(R.drawable.empty_head);
            tvName.setText("登录/注册");
            tvSign.setText(null);
            tvName.setTextAppearance(mCtx, R.style.SocialNormalTxtRed);
            tv_square.setText("0");
            tv_circle.setText("0");
            tv_fans.setText("0");
            tv_attention.setText("0");
            img_edit_sign.setVisibility(View.GONE);
        }
    }

    /**
     * 获取动态数组数据 可以由其他地方传来(json等)
     */
    private List<MyListData> getData() {
        List<MyListData> list = new ArrayList<MyListData>();
        for (int i = 0; i < left_text.length; i++) {
            MyListData myListData = new MyListData(left_image[i], left_text[i], -1, null, null, right_button_text[i],
                    right_button_background[i], right_button_text_color[i]);
            list.add(myListData);
        }
        return list;
    }

    @Override
    public void showView(Intent intent) {
        initLoginState();
        if (isShowView) {
            // 非首次显示
            LogUtils.e("非首次显示");
            getSystemNotice();
            getUserNotice();
        } else {
            // 首次显示
            LogUtils.e("首次显示");
            updateNotify();
        }
        if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
            if (userInfoBody == null) {
                loadUserInfoData();
            }
        }
        super.showView(intent);
    }


    /**
     * 更新消息数
     */
    private void updateMsgCount(int count) {
        String user_id = "";
        LoginEntity loginEntity = SPUtils.instance().getLoginEntity();
        if (loginEntity != null) {
            user_id = loginEntity.get_id();
        }
        int num = DBPushMessageHelper.queryAllNoRead(user_id);
        if (count > 0) {
            if (count < 100) {
                right_button_text[1] = "" + count;
            } else {
                right_button_text[1] = count + "+";
            }
            right_button_background[1] = R.drawable.circle_red_bg;
            right_button_text_color[1] = R.color.white;
        } else {
            right_button_text[1] = null;
        }
        adapter.setData(getData());
        ((MainActivity) mCtx).updateNotice(num + count);
    }

    /**
     * 更新通知
     */
    private void updateNotify() {
        String user_id = "";
        LoginEntity loginEntity = SPUtils.instance().getLoginEntity();
        if (loginEntity != null) {
            user_id = loginEntity.get_id();
        }
        int num = DBPushMessageHelper.queryAllNoRead(user_id);
        if (num != 0) {
            right_button_text[2] = "" + DBPushMessageHelper.queryAllNoRead(user_id);
            right_button_background[2] = R.drawable.circle_red_bg;
            right_button_text_color[2] = R.color.white;
            // findViewById(R.id.notify_icon).setVisibility(View.VISIBLE);

        } else {
            right_button_text[2] = null;
        }
        adapter.setData(getData());

        int imMsgCount;
        try {
            imMsgCount = Integer.parseInt(right_button_text[1]);
        } catch (Exception e) {
            imMsgCount = 0;
        }
        ((MainActivity) mCtx).updateNotice(num + imMsgCount);
    }

    /**
     * 获取个人消息
     */
    public void getUserNotice() {
        SocialPropEntity propEntity = SPUtils.instance().getSocialPropEntity();
        if (propEntity != null) {
            if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
                LoginEntity loginEntity = SPUtils.instance().getLoginEntity();
                if (loginEntity != null) {
                    LogUtils.e("获取个人消息");
                    userLastId = SPUtils.instance().get(loginEntity.get_id(), "").toString();
                    RequestManager.request(mCtx, new MessageUserParams(userLastId), UserPushMessageResponse.class, this,
                            propEntity.getApp_socail_server());
                }
            }
        }
    }

    /**
     * 获取通知消息
     */
    public void getSystemNotice() {
        SocialPropEntity propEntity = SPUtils.instance().getSocialPropEntity();
        if (propEntity != null) {
            LogUtils.e("获取通知消息");
            commLastId = SPUtils.instance().get(SPUtils.COMM_MESSAGE_LASTID, "").toString();
            RequestManager.request(mCtx, new MessageCommParams(commLastId), SysPushMessageResponse.class, this,
                    propEntity.getApp_socail_server());
        }
    }

    @Override
    public void onResume() {
        if (!isShowView) {
            return;
        }
        getSystemNotice();
        if (!(boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
            userInfoBody = null;
            initLoginState();
            handler.obtainMessage(0, 0).sendToTarget();
        } else {
            loadUserInfoData();
            getUserNotice();
            getUserMsgCount();
        }
    }


    /**
     * 获取用户消息数
     */
    private void getUserMsgCount() {
        if (RongIM.getInstance() != null) {
            RongIM.getInstance().getTotalUnreadCount(new RongIMClient.ResultCallback<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    handler.obtainMessage(0, (int) integer).sendToTarget();
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
    }

    /**
     * 重连融云
     */
    private void reconnect() {
        String token = (String) SPUtils.instance().get(SPUtils.RONGYUN_TOKEN, "");
        if (!TextUtils.equals((String) SPUtils.instance().get(SPUtils.RONGYUN_TOKEN, ""), "")) {
            LogUtils.e("token:" + token);
            RongIM.getInstance().connect(token,
                    new RongIMClient.ConnectCallback() {

                        /**
                         * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App
                         * Server 重新请求一个新的 Token
                         */
                        @Override
                        public void onTokenIncorrect() {
                            ToastUtil.show("Token失效了!");
                            dlgLoad.dismissDialog();
                        }

                        /**
                         * 连接融云成功
                         *
                         * @param userid
                         *            当前 token
                         */
                        @Override
                        public void onSuccess(String userid) {
                            LogUtils.e("连接融云 --成功 ! userid= "
                                    + userid);
                            dlgLoad.dismissDialog();
                            // 将最新的个人信息同步给融云
                            if (RongIM.getInstance() != null) {
                                String headurl = StringUtil
                                        .getHeadUrl(SPUtils
                                                .instance()
                                                .getLoginEntity()
                                                .getHead_img());
                                io.rong.imlib.model.UserInfo userInfo = new io.rong.imlib.model.UserInfo(
                                        userid,
                                        SPUtils.instance()
                                                .getLoginEntity()
                                                .getUname(),
                                        TextUtils.isEmpty(headurl) ? null : Uri.parse(headurl));
                                RongIM.getInstance()
                                        .setCurrentUserInfo(
                                                userInfo);
                                RongIM.getInstance()
                                        .setMessageAttachedUserInfo(
                                                true);
                                Intent intentChat = new Intent(mCtx, RongYunConversationListActivity.class);
                                mCtx.startActivity(intentChat);

                            }
                        }

                        /**
                         * 连接融云失败
                         *
                         * @param errorCode
                         *            错误码，可到官网 查看错误码对应的注释
                         */
                        @Override
                        public void onError(
                                RongIMClient.ErrorCode errorCode) {
                            LogUtils.e("连接融云 --连接失败 ! errorCode= "
                                    + errorCode);
                            dlgLoad.dismissDialog();
                        }
                    });
        } else {
            dlgLoad.dismissDialog();
        }
    }

    @Override
    public void onStop() {
    }

    @Override
    public void destory() {
        super.destory();
    }

    /**
     * 向接口请求当前用户的个人信息
     */
    private void loadUserInfoData() {
        if (TextUtils.isEmpty(SPUtils.instance().getLoginEntity().get_id())) {
            return;
        }
        RequestManager.request((MainActivity) mCtx, new UserInfoParams(SPUtils.instance().getLoginEntity().get_id()),
                UserInfoResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.user_info_layout:
                Intent intent = new Intent();
                if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
                    intent.setClass(mCtx, MyInfoActivity.class);
                } else {
                    intent.setClass(mCtx, LoginActivity.class);
                }
                mCtx.startActivity(intent);
                break;
            case R.id.setting_layout:
                Intent intent6 = new Intent(mCtx, SettingActivity.class);
                mCtx.startActivity(intent6);
                break;
            case R.id.img_edit_sign:
                Intent intentSign = new Intent(mCtx, EditBriefActivity.class);
                intentSign.putExtra("OLD_BRIEF", tvSign.getText().toString());
                mCtx.startActivity(intentSign);
                break;
            case R.id.rel_square:
                // 广场帖
                if (LoginUtil.checkLogin(mCtx)) {
                    Intent intent2 = new Intent(mCtx, MyPostActivity.class);
                    mCtx.startActivity(intent2);
                }
                break;
            case R.id.rel_circle:
                // 圈子
                if (LoginUtil.checkLogin(mCtx)) {
                    Intent intentCircle = new Intent(mCtx, UserCircleActivity.class);
                    intentCircle.putExtra("user_id", SPUtils.instance().getLoginEntity().get_id());
                    intentCircle.putExtra("user_name", SPUtils.instance().getLoginEntity().getUname());
                    mCtx.startActivity(intentCircle);
                }
                break;
            case R.id.rel_fans:
                // 粉丝
                if (LoginUtil.checkLogin(mCtx)) {
                    Intent intentFans = new Intent(mCtx, AttentionActivity.class);
                    intentFans.putExtra("id", SPUtils.instance().getLoginEntity().get_id());
                    intentFans.putExtra("name", SPUtils.instance().getLoginEntity().getUname());
                    intentFans.putExtra("select_no", 1);
                    mCtx.startActivity(intentFans);
                }
                break;
            case R.id.rel_attention:
                if (LoginUtil.checkLogin(mCtx)) {
                    // 关注
                    Intent intentAttention = new Intent(mCtx, AttentionActivity.class);
                    intentAttention.putExtra("id", SPUtils.instance().getLoginEntity().get_id());
                    intentAttention.putExtra("name", SPUtils.instance().getLoginEntity().getUname());
                    intentAttention.putExtra("select_no", 0);
                    mCtx.startActivity(intentAttention);
                }
                break;

            default:
                break;
        }
    }


    public void updateInfo_City(Context context, String addr) {

        LoginEntity body = SPUtils.instance().getLoginEntity();

        RequestManager.request(context, new EditInfoParam(body.getUname(), body.getBirth_day(), body.getAge(),
                body.getSex(), addr, body.getFav(), body.getInterest(), body.getQq(), body.getBei_tie(), body.getFav_font(), body.getStu_time(),
                body.getSchool(), body.getCompany(), body.getProfession(), body.getSignature(), body.getEmail0(), body.getReal_name(),
                body.getCoord(), body.getGood_at()), EditMyInfoResponse.class, null, Constant.URL);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
                    Intent intent0 = new Intent(mCtx, MyInfoActivity.class);
                    mCtx.startActivity(intent0);
                } else {
                    Intent intentLogin = new Intent(mCtx, LoginActivity.class);
                    mCtx.startActivity(intentLogin);
                }
                break;
            case 2:
                //私聊
                dlgLoad.loading();
                //判断token 是否存在
                if (TextUtils.equals((String) SPUtils.instance().getToken(), "")) {
                    //判断用户信息是否存在
                    if (SPUtils.instance().getLoginEntity().getUname().trim().equals("")) {
                        //重新登陆
                        dlgLoad.dismissDialog();
                        LoginUtil.showLoginDialog(mCtx);
                    } else {
                        //获取token
                        getToken();
                    }
                } else {
                    //token存在连接融云
                    if (RongIM.getInstance() != null) {
                        Intent intentStartConverstaion = new Intent(mCtx, RongYunConversationListActivity.class);
                        mCtx.startActivity(intentStartConverstaion);
                    }
                    dlgLoad.dismissDialog();
                }
                break;
            case 1:
                mCtx.startActivity(new Intent(mCtx, SearchFragmentActivity.class));
                break;
            case 3:
                Intent intent3 = new Intent(mCtx, PushMessageActivity.class);
                mCtx.startActivity(intent3);
                break;
            case 5:
                Intent intent7 = new Intent(mCtx, ApplyForCertificationActvity.class);
                mCtx.startActivity(intent7);

                break;
            case 4:
                // ToastUtil.show("离线碑帖");
                Intent intent1 = new Intent(mCtx, FoundBeiTieCalligrapherActivity.class);
                intent1.putExtra("link_url", "");
                intent1.putExtra("penmen_id", "");
                intent1.putExtra("name", ((MyListData) parent.getAdapter().getItem(position)).getLeft_text());
                mCtx.startActivity(intent1);

                break;
            case 7:
                Intent intent4 = new Intent(mCtx, UserFeedbackActivity.class);
                mCtx.startActivity(intent4);
                break;
            case 6:
                ((NoSwipeBackActivity) mCtx).sharedContentCustomize("【写字派】", "写字一派邀您一起加入",
                        "http://www.xiezipai.com/download/version.html?", "", null);
                break;
            case 8:
                gotoRate();

                break;

            default:
                break;
        }
    }

    /**
     * 获取融云Token
     */
    private void getToken() {
        RongYunTokenParams params = new RongYunTokenParams();
        RequestManager.request(mCtx, params, RongYunTokenResponse.class,
                new WritePreListener<BaseResponse>() {
                    @Override
                    public void onResponse(BaseResponse response) {
                        if ("0".equals(response.getResCode())) {
                            if (response instanceof RongYunTokenResponse) {
                                RongYunTokenResponse rongYunTokenResponse = (RongYunTokenResponse) response;
                                String token = rongYunTokenResponse.getRepBody().getToken();
                                SPUtils.instance().put(SPUtils.RONGYUN_TOKEN, token);
                                LogUtils.e("token:" + token);
                                reconnect();
                            }
                        } else {
                            dlgLoad.dismissDialog();
                            ToastUtil.show(response.getResMsg());
                        }
                    }

                    @Override
                    public void onResponse(String tag, BaseResponse response) {

                    }
                }, SPUtils.instance().getSocialPropEntity()
                        .getApp_socail_server());
    }

    /**
     * 获取消息回调
     */
    private void feedBackLastId() {
        SocialPropEntity propEntity = SPUtils.instance().getSocialPropEntity();
        if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
            RequestManager.request(mCtx, new MessageFeedBackIdParams(userLastId, commLastId),
                    NoticeFeedbackResponse.class, this, propEntity.getApp_socail_server());
        }
    }

    /**
     * 应用市场评价
     */
    private void gotoRate() {
        Uri uri = Uri.parse("market://details?id=" + mCtx.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            mCtx.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            ToastUtil.show("尚未安装应用市场，无法评分");
        }
    }

    @Override
    public void onResponse(BaseResponse response) {
        // TODO Auto-generated method stub
        super.onResponse(response);
        if (response instanceof SysPushMessageResponse) {
            if ("0".equals(response.getResCode())) {
                SysPushMessageResponse gscrResult = (SysPushMessageResponse) response;
                if (gscrResult != null) {
                    SysPushMessageResponse.Repbody rBody = gscrResult.getRepBody();
                    if (rBody != null) {
                        if (rBody.getList() != null) {
                            for (int i = 0; i < rBody.getList().size(); i++) {
                                DBPushMessageHelper.insert(rBody.getList().get(i));
                                if (i == rBody.getList().size() - 1) {
                                    commLastId = rBody.getList().get(i).get_id();
                                    SPUtils.instance().put(SPUtils.COMM_MESSAGE_LASTID, commLastId);
                                }
                            }
                            updateNotify();
                        }
                    }

                }
            }
        } else if (response instanceof UserPushMessageResponse) {
            if ("0".equals(response.getResCode())) {
                UserPushMessageResponse gscrResult = (UserPushMessageResponse) response;
                if (gscrResult != null) {
                    UserPushMessageResponse.Repbody rBody = gscrResult.getRepBody();
                    if (rBody != null) {
                        if (rBody.getList() != null) {
                            for (int i = 0; i < rBody.getList().size(); i++) {
                                DBPushMessageHelper.insert(rBody.getList().get(i));
                                if (i == rBody.getList().size() - 1) {
                                    userLastId = rBody.getList().get(i).get_id();
                                    SPUtils.instance().put(SPUtils.instance().getLoginEntity().get_id(), userLastId);
                                    feedBackLastId();
                                }
                            }
                            updateNotify();
                        }
                    }

                }
            }
        } else if (response instanceof UserInfoResponse) {
            if ("0".equals(response.getResCode())) {
                UserInfoResponse userInfoResult = (UserInfoResponse) response;
                if (userInfoResult != null) {
//                    if (userInfoBody!=null&&userInfoBody.get_id().equals(userInfoResult.getRepBody().get_id())){
//                       return;
//                    }else{
                    userInfoBody = userInfoResult.getRepBody();
                    initLoginState();
//                    }
                }
            }
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                updateMsgCount((int) msg.obj);
            }
        }
    };

    @Override
    public void onMessageIncreased(int count) {

    }

    @Override
    public void onCheckMsgCount() {
        getUserMsgCount();
    }

}
