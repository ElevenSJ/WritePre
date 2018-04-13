package com.easier.writepre.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.CircleMemberAdapter;
import com.easier.writepre.entity.CircleApplyMember;
import com.easier.writepre.entity.CircleDetail;
import com.easier.writepre.entity.CircleMember;
import com.easier.writepre.entity.LoginEntity;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.mainview.SocialMainView;
import com.easier.writepre.param.CircleApplyMemberParams;
import com.easier.writepre.param.CircleGetDetailParams;
import com.easier.writepre.param.CircleJoinParams;
import com.easier.writepre.param.CircleMemberParams;
import com.easier.writepre.param.CircleMemberRegByCodeParams;
import com.easier.writepre.param.CircleQuitParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CircleApplyMemberResponse;
import com.easier.writepre.response.CircleDetailResponse;
import com.easier.writepre.response.CircleJoinResponse;
import com.easier.writepre.response.CircleMemberRegByCodeResponse;
import com.easier.writepre.response.CircleMemberResponse;
import com.easier.writepre.response.CircleQuitResponse;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshBase.Mode;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnRefreshListener2;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.CirclePopupWindow;
import com.easier.writepre.widget.ConfirmHintDialog;
import com.easier.writepre.widget.ConfirmHintDialog.ConfirmHintDialogListener;
import com.easier.writepre.widget.MyGridLayout;
import com.easier.writepre.widget.RoundImageView;
import com.sj.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * 圈子介绍
 */
public class CircleDetailActivity extends BaseActivity {

    private final int REQUEST_DETAIL_SUCCESS = 0;
    private final int REQUEST_ERROR = -1;

    private final int REQUEST_CIRCLE_MEMBER_SUCCESS = 1;

    private final int REQUEST_JOIN_CIRCLE_SUCCESS = 2;

    private final int REQUEST_APPLY_MEMBER_SUCCESS = 3;

    private final int REQUEST_QUIT_CIRCLE_SUCCESS = 4;

    private final int REQUEST_MEMBER_REG_BY_CODE_SUCCESS = 5;

    private final int UPDATE_MESSAGE_COUNT = 6;
    private String circle_id;

    private CircleDetail mCircleBody;

    private ImageView imgFace;
    private TextView tvName;
    private TextView tvtag;
    private TextView tvNum;
    private TextView tvPostNum;
    private TextView tvIsOpen;
    private TextView tvDesc;
    private TextView tvMemberNum;
    private MyGridLayout layoutMark;
    private ImageView imgDrop;

    private LinearLayout layoutMembers;
    private Button btJoin;

    private PullToRefreshListView applyListView;
    private LinearLayout layoutJoin;
    private LinearLayout layoutApplyJoin;

    private PopupWindow popupWindow;

    private CircleMemberAdapter adapter;
    private Dialog codeDialog;

    // 圈子介绍详情收起标识
    private boolean flag = true;

    private TextView tv_set_circle_code;//设置入圈口令
    private TextView tv_circle_code;//入圈口令
    /**
     * 圈子口令的整体布局，以方便设置整体点击事件
     * 以及整体的显示与隐藏
     */
    private LinearLayout ll_circle_code;
    private LinearLayout ll_add_news;//发布公告
    private LinearLayout ll_my_circle_name;//我的圈子昵称整体布局，方便设置点击
    private TextView tv_my_circle_name;//我的圈子昵称
    private LinearLayout ll_clear_chat_record;//清除聊天记录

    public static String CIRCLE_ID = "circle_id";
    public static String CIRCLE_CODE = "circle_code";
    public static String OLD_CODE = "old_code";
    public static String MY_CIRCLE_NAME = "name";
    public static String OLD_NAME = "old_name";
    public final static int CIRCLE_REQUEST_CODE = 999;
    public final static int NAME_REQUEST = 998;
    private CirclePopupWindow popWindow;

    private List<CircleApplyMember> list = new ArrayList<CircleApplyMember>();

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REQUEST_DETAIL_SUCCESS:
                    CircleDetailResponse detailResponse = (CircleDetailResponse) msg.obj;
                    mCircleBody = detailResponse.getRepBody();
                    updateCircleInfo(mCircleBody);
                    getCircleMember();
                    break;
                case REQUEST_CIRCLE_MEMBER_SUCCESS:
                    dlgLoad.dismissDialog();
                    CircleMemberResponse member = (CircleMemberResponse) msg.obj;
                    if (member != null) {
                        updateMember(member.getRepBody().getList());
                    }
                    break;
                case REQUEST_ERROR:
                    dlgLoad.dismissDialog();
                    ToastUtil.show(((BaseResponse) msg.obj).getResMsg());
                    break;
                case REQUEST_JOIN_CIRCLE_SUCCESS:
                    dlgLoad.dismissDialog();
                    btJoin.setText(getResources().getString(
                            R.string.wait_conform_txt));
                    btJoin.setEnabled(false);
                    break;
                case REQUEST_APPLY_MEMBER_SUCCESS:
                    CircleApplyMemberResponse applyMemberResponse = (CircleApplyMemberResponse) msg.obj;
                    if (applyMemberResponse != null) {
                        if (applyMemberResponse.getRepBody() != null) {
                            if (applyMemberResponse.getRepBody().getList() != null) {
                                list.addAll(applyMemberResponse.getRepBody()
                                        .getList());
                            }
                            adapter.setData(list);
                            if (list == null || list.size() == 0) {
                                applyListView.setMode(Mode.PULL_FROM_START);
                            }
                        }
                    }
                    if (adapter.getCount() == 0) {
                        layoutApplyJoin.setVisibility(View.GONE);
                    } else {
                        layoutApplyJoin.setVisibility(View.VISIBLE);
                    }
                    break;
                case REQUEST_QUIT_CIRCLE_SUCCESS:
                    toMyCircleList();
                    break;
                case REQUEST_MEMBER_REG_BY_CODE_SUCCESS:
                    codeDialog.dismiss();
                    mCircleBody.setRole("1");
                    btJoin.setVisibility(View.GONE);
                    break;
                case UPDATE_MESSAGE_COUNT:
                    int messageCount = (int) msg.obj;
                    String msgCount = "0";
                    if (messageCount > 0) {
                        if (messageCount > 99) {
                            msgCount = "99+";
                        } else {
                            msgCount = messageCount + "";
                        }
                        ((TextView) findViewById(R.id.tv_chat_record_count)).setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.tv_chat_record_count)).setText(msgCount);
                    } else {
                        ((TextView) findViewById(R.id.tv_chat_record_count)).setVisibility(View.GONE);
                    }

                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_detail);
        circle_id = getIntent().getStringExtra("circle_id");
        initView();
        getCircleDetail();
        getHistoryMessageCount();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        setIntent(intent);
        getCircleDetail();
        getHistoryMessageCount();
    }

    protected void toMyCircleList() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.MAIN_TAB_INDEX, MainActivity.TYPE_TWO);
        intent.putExtra(SocialMainView.TAB_INDEX, SocialMainView.TAB_CIRCLE);
        intent.putExtra(SocialMainView.ITEM_CIRCLE_INDEX, SocialMainView.ITEM_2);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    protected void updateMember(List<CircleMember> list) {
        if (list == null) {
            return;

        }
        layoutMembers.removeAllViews();
        // 动态载入圈子成员
        for (int i = 0; i < list.size(); i++) {
            if (i > 5) {
                return;
            }
            View memberView = getLayoutInflater().inflate(
                    R.layout.circle_member_icon_item, null);
            RoundImageView imgHead = (RoundImageView) memberView
                    .findViewById(R.id.imgHead);
            TextView circleUser = (TextView) memberView
                    .findViewById(R.id.tv_circle_user);
            imgHead.setImageView(StringUtil.getHeadUrl(list.get(i).getHead_img()));
            imgHead.setIconView(list.get(i).getIs_teacher());
            if (list.get(i).getRole().equals("0")) {
                circleUser.setVisibility(View.VISIBLE);
                if (list.get(i).getOwner_id().equals(list.get(i).getUser_id())) {
                    circleUser.setText("圈主");
                } else {
                    circleUser.setText("管理员");
                }
            } else {
                circleUser.setVisibility(View.GONE);
            }
            AutoUtils.autoSize(memberView);
            layoutMembers.addView(memberView);
        }
    }

    protected void updateCircleInfo(CircleDetail mCircleBody) {
        if (mCircleBody == null) {
            ToastUtil.show("未获取到圈子详情");
            return;
        }
        // if (mCircleBody.getRole().equals("0")) {
        // setTopRight(R.drawable.circle_set_up);
        // } else {
        setTopRight(R.drawable.list);
        // }
        BitmapHelp.getBitmapUtils().display(imgFace,
                StringUtil.getImgeUrl(mCircleBody.getFace_url()));
        tvName.setText(mCircleBody.getName());
        if (mCircleBody.getType().equals("0")) {
            tvtag.setText("学校");
        } else {
            tvtag.setText("普通");
        }
        // String numStr = "<font color='#ff0000'><b>" + mCircleBody.getNum() +
        // " /</b></font> 人";
        // tvNum.setText(Html.fromHtml(numStr));
        tvNum.setText(mCircleBody.getNum() + "");
        tvMemberNum.setText(mCircleBody.getNum() + "");
        //
        // String postStr = "<font color='#ff0000'><b>" +
        // mCircleBody.getPost_num() + " /</b></font> 帖";
        // tvPostNum.setText(Html.fromHtml(postStr));
        tvPostNum.setText(mCircleBody.getPost_num() + "");
        //
        String openStr = mCircleBody.getIs_open().equals("0") ? "" : "公开";
        // tvIsOpen.setText(Html.fromHtml("<font color='#ff0000' size=14><b>" +
        // openStr + " /</b></font> 状态"));
        tvIsOpen.setText(openStr);

        String descStr = "<font color='#ff0000'><b>圈子简介：</b></font>"
                + mCircleBody.getDesc();
        tvDesc.setText(Html.fromHtml(descStr));
        // 动态载入圈子标签
        for (int i = 0; i < mCircleBody.getMark_no().size(); i++) {
            View markView = getLayoutInflater().inflate(
                    R.layout.circle_tag_txt_item, null);
            TextView name = (TextView) markView.findViewById(R.id.item_name);
            AutoUtils.autoSize(markView);
            layoutMark.addView(markView);
            name.setBackgroundColor(Color.parseColor("#"
                    + mCircleBody.getMark_no().get(i).getBgcolor()));
            name.setText(mCircleBody.getMark_no().get(i).getTitle());
        }
        imgDrop.setBackgroundResource(R.drawable.drop_down);

        if (mCircleBody.getRole().equals("0")) {
            btJoin.setVisibility(View.VISIBLE);
            btJoin.setEnabled(true);
            btJoin.setText(getResources().getString(R.string.apply_join_txt));
            layoutJoin.setVisibility(View.GONE);
            applyListView.setMode(Mode.BOTH);
            getCircleApplyMember("9");
        } else if (mCircleBody.getRole().equals("1")) {
//            btJoin.setText(getResources().getString(R.string.join_quit_txt));
//            btJoin.setEnabled(true);
            btJoin.setVisibility(View.GONE);
            layoutApplyJoin.setVisibility(View.GONE);
        } else if (mCircleBody.getRole().equals("10")) {
            btJoin.setVisibility(View.VISIBLE);
            btJoin.setText(getResources().getString(R.string.join_txt));
            btJoin.setEnabled(true);
            layoutApplyJoin.setVisibility(View.GONE);
        } else if (mCircleBody.getRole().equals("11")) {
            btJoin.setVisibility(View.VISIBLE);
            btJoin.setText(getResources().getString(R.string.wait_conform_txt));
            btJoin.setEnabled(false);
            layoutApplyJoin.setVisibility(View.GONE);
        }
        if (mCircleBody.getRole().equals("0")) {
            ll_circle_code.setVisibility(View.VISIBLE);
            ll_add_news.setVisibility(View.VISIBLE);
            tv_circle_code.setText(mCircleBody.getCode());
            ll_my_circle_name.setVisibility(View.VISIBLE);
            ll_clear_chat_record.setVisibility(View.VISIBLE);

        } else {
            ll_circle_code.setVisibility(View.GONE);
//            ll_add_news.setVisibility(View.GONE);
            if (mCircleBody.getRole().equals("1")) {
                ll_add_news.setVisibility(View.VISIBLE);
                ll_my_circle_name.setVisibility(View.VISIBLE);
                ll_clear_chat_record.setVisibility(View.VISIBLE);
            } else {
                ll_add_news.setVisibility(View.GONE);
                ll_my_circle_name.setVisibility(View.GONE);
                ll_clear_chat_record.setVisibility(View.GONE);
            }
        }
        tv_my_circle_name.setText(mCircleBody.getCircle_uname());

    }

    /**
     * 获取圈子详情
     */
    private void getCircleDetail() {
        dlgLoad.loading();
        RequestManager.request(this, new CircleGetDetailParams(circle_id),
                CircleDetailResponse.class, this, SPUtils.instance()
                        .getSocialPropEntity().getApp_socail_server());
    }

    private void getCircleMember() {
        RequestManager.request(this,
                new CircleMemberParams(circle_id, "9", 20),
                CircleMemberResponse.class, this, SPUtils.instance()
                        .getSocialPropEntity().getApp_socail_server());

    }

    private void getCircleApplyMember(String lastId) {
        RequestManager.request(this, new CircleApplyMemberParams(circle_id,
                lastId, 20), CircleApplyMemberResponse.class, this, SPUtils
                .instance().getSocialPropEntity().getApp_socail_server());

    }

    private void initView() {
        setTopTitle("圈子介绍");
        popWindow = new CirclePopupWindow(this, itemsOnClick);
        applyListView = (PullToRefreshListView) findViewById(R.id.apply_listView);
        applyListView.setMode(Mode.DISABLED);

        View listViewHead = getLayoutInflater().inflate(
                R.layout.circle_detail_head, null);
        AutoUtils.autoSize(listViewHead);
        applyListView.getRefreshableView().addHeaderView(listViewHead);

        adapter = new CircleMemberAdapter(this, 0);
        applyListView.setAdapter(adapter);

        imgFace = (ImageView) listViewHead.findViewById(R.id.img_icon);
        tvName = (TextView) listViewHead.findViewById(R.id.tv_name);
        tvtag = (TextView) listViewHead.findViewById(R.id.tv_tag);
        tvNum = (TextView) listViewHead.findViewById(R.id.tv_num);
        tvPostNum = (TextView) listViewHead.findViewById(R.id.tv_post_num);
        tvIsOpen = (TextView) listViewHead.findViewById(R.id.tv_is_open);
        tvDesc = (TextView) listViewHead.findViewById(R.id.circle_desc);
        layoutMark = (MyGridLayout) listViewHead
                .findViewById(R.id.circle_tag_grid);
        layoutMembers = (LinearLayout) listViewHead
                .findViewById(R.id.circle_join_layout);
        imgDrop = (ImageView) listViewHead.findViewById(R.id.drop_img);
        btJoin = (Button) listViewHead.findViewById(R.id.join_bt);

        tvMemberNum = (TextView) listViewHead.findViewById(R.id.txt_member_num);

        layoutJoin = (LinearLayout) listViewHead.findViewById(R.id.layout_join);
        layoutApplyJoin = (LinearLayout) listViewHead
                .findViewById(R.id.layout_apply_join);

        tv_set_circle_code = (TextView) findViewById(R.id.tv_set_circle_code);
        tv_circle_code = (TextView) findViewById(R.id.tv_circle_code);
        ll_circle_code = (LinearLayout) findViewById(R.id.ll_circle_code);

        ll_add_news = (LinearLayout) findViewById(R.id.ll_add_news);
        ll_my_circle_name = (LinearLayout) findViewById(R.id.ll_my_circle_name);
        tv_my_circle_name = (TextView) findViewById(R.id.tv_my_circle_name);
        ll_clear_chat_record = (LinearLayout) findViewById(R.id.ll_clear_chat_record);

        ll_add_news.setOnClickListener(this);
        ll_my_circle_name.setOnClickListener(this);
        ll_clear_chat_record.setOnClickListener(this);
        ll_circle_code.setOnClickListener(this);
        imgDrop.setOnClickListener(this);
        btJoin.setOnClickListener(this);
        listViewHead.findViewById(R.id.circle_join_layout).setOnClickListener(this);
        listViewHead.findViewById(R.id.txt_member_num).setOnClickListener(this);

        applyListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh( // 顶部下拉刷新
                                             PullToRefreshBase<ListView> refreshView) {
                loadNews();
            }

            @Override
            public void onPullUpToRefresh( // 底部加载更多
                                           PullToRefreshBase<ListView> refreshView) {
                loadOlds();
            }

        });
    }

    /**
     * 下拉刷新数据
     */
    private void loadNews() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                applyListView.onRefreshComplete();
                list.clear();
                getCircleApplyMember("9");
            }
        }, 300);
    }

    /**
     * 加载更多
     */
    protected void loadOlds() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                applyListView.onRefreshComplete();
                if (list != null && list.size() > 0) {
                    getCircleApplyMember(list.get(list.size() - 1).get_id());
                }
            }
        }, 300);
    }

    @Override
    public void onTopRightClick(View v) {
        showPopupWindow(v);// 举报窗口
    }

    private void showPopupWindow(View v) {
        View convertView1 = LayoutInflater.from(this).inflate(
                R.layout.circle_report_pop, null);
        popupWindow = new PopupWindow(convertView1,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        ColorDrawable cd = new ColorDrawable(0x000000);
        popupWindow.setBackgroundDrawable(cd);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(v, 10, 0);
        popupWindow.update();

        LinearLayout itemLayout2 = (LinearLayout) convertView1
                .findViewById(R.id.layout_item2);
        Button bt1 = (Button) convertView1.findViewById(R.id.bt1);
        Button bt2 = (Button) convertView1.findViewById(R.id.bt2);
        LinearLayout itemLayout3 = (LinearLayout) convertView1
                .findViewById(R.id.layout_item3);
        Button bt3 = (Button) convertView1.findViewById(R.id.bt3);
        bt1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                if (LoginUtil.checkLogin(CircleDetailActivity.this)) {
                    if (mCircleBody == null) {
                        return;
                    }
                    if (mCircleBody.getRole().equals("0")) {
                        toModifyCircle();
                    } else {
                        Intent intent = new Intent(CircleDetailActivity.this,
                                ReportCircleMsgActivity.class);
                        intent.putExtra("type", 1);
                        intent.putExtra("circle_id", circle_id);
                        startActivity(intent);
                    }
                }
            }
        });
        bt2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                if (mCircleBody == null) {
                    return;
                }
                if (mCircleBody.getRole().equals("0")) {
                    toMember();
                } else {

                }
            }
        });
        bt3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });
        /**
         * 先判断是否是管理员
         *         是，再判断我是不是圈主
         *                      是圈主，退出登录按钮隐藏；
         *                      不是，只是普通管理员，显示退出登录按钮
         *         不是管理员，是普通成员，则显示举报按钮和退出登录按钮
         *                     不是圈子成员，则只显示举报
         */
        if (mCircleBody.getRole().equals("0")) {
            itemLayout2.setVisibility(View.VISIBLE);
            bt1.setText(getResources().getString(R.string.modify_circle_txt));
            bt2.setText(getResources().getString(R.string.manager_circle_txt));
            if (mCircleBody.getUser_id().equals(SPUtils.instance().getLoginEntity().get_id())) {
                itemLayout3.setVisibility(View.GONE);
            } else {
                itemLayout3.setVisibility(View.VISIBLE);
                bt3.setText(getResources().getString(R.string.join_quit_txt));
            }
        } else if (mCircleBody.getRole().equals("1")) {
            itemLayout2.setVisibility(View.GONE);
            itemLayout3.setVisibility(View.VISIBLE);
            bt1.setText(getResources().getString(R.string.report_txt));
            bt3.setText(getResources().getString(R.string.join_quit_txt));
        } else {
            itemLayout2.setVisibility(View.GONE);
            itemLayout3.setVisibility(View.GONE);
            bt1.setText(getResources().getString(R.string.report_txt));
        }

    }

    protected void toModifyCircle() {
        Intent intent = new Intent(this, CircleCreatActivity.class);
        intent.putExtra("circle_body", mCircleBody);
        startActivityForResult(intent,CircleCreatActivity.MODIFY);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.join_bt:
                if (LoginUtil.checkLogin(this)) {
                    joinClick(v);
                }
                break;
            case R.id.drop_img:
                if (flag) {
                    flag = false;
                    tvDesc.setEllipsize(null); // 展开
                    tvDesc.setSingleLine(flag);
                    imgDrop.setBackgroundResource(R.drawable.drop_up);
                } else {
                    flag = true;
                    tvDesc.setEllipsize(TextUtils.TruncateAt.END); // 收缩
                    tvDesc.setSingleLine(flag);
                    imgDrop.setBackgroundResource(R.drawable.drop_down);
                }
                break;
            case R.id.circle_join_layout:
            case R.id.txt_member_num:
                toMember();
                break;
            case R.id.ll_circle_code:
                Intent codeIntent = new Intent(CircleDetailActivity.this, CircleMemberSetCodeActivity.class);
                codeIntent.putExtra(CIRCLE_ID, circle_id);
                codeIntent.putExtra(OLD_CODE, tv_circle_code.getText().toString());
                startActivityForResult(codeIntent, CIRCLE_REQUEST_CODE);
                break;
            case R.id.ll_add_news://发布公告
                //点击查看群公告列表
                Intent intent = new Intent(this, GroupNoticeListActivity.class);
                intent.putExtra("mTargetId", circle_id);
                startActivity(intent);
                break;
            case R.id.ll_my_circle_name:
                Intent nameIntent = new Intent(CircleDetailActivity.this, CircleMemberEditNameActivity.class);
                nameIntent.putExtra(OLD_NAME, tv_my_circle_name.getText());
                nameIntent.putExtra("user_id", SPUtils.instance().getLoginEntity().get_id());
                nameIntent.putExtra(CIRCLE_ID, mCircleBody.get_id());
                startActivityForResult(nameIntent, NAME_REQUEST);
                break;
            case R.id.ll_clear_chat_record:
                showClearDataDialog();
                break;
            default:
                break;
        }

    }

    private void clearData() {
        if (RongIM.getInstance() != null) {
            if (!TextUtils.isEmpty(circle_id)) {
                RongIM.getInstance().clearMessages(Conversation.ConversationType.GROUP, circle_id, new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        handler.obtainMessage(UPDATE_MESSAGE_COUNT, 0).sendToTarget();
                        ToastUtil.show("聊天记录清除成功");
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        ToastUtil.show("聊天记录清除失败");
                    }
                });
            }
        }

    }

    public void showClearDataDialog() {

        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_islogin, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_login_now);
        tv.setText("确定是否清除?");
        TextView confirm = (TextView) view.findViewById(R.id.tv_login);
        confirm.setText("清除");
        confirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                clearData();
            }
        });
        view.findViewById(R.id.tv_cancel).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.dismiss();
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    /**
     * 退出圈子
     */
    private void quitCirlce() {
        dlgLoad.loading();
        RequestManager.request(this, new CircleQuitParams(circle_id),
                CircleQuitResponse.class, this, SPUtils.instance()
                        .getSocialPropEntity().getApp_socail_server());

    }

    /**
     * 查看成员
     */
    private void toMember() {
        Intent intent = new Intent(this, CircleMemberActivity.class);
        intent.putExtra("circle_id", circle_id);
        intent.putExtra("role", mCircleBody.getRole());
        startActivity(intent);
    }

    private void joinClick(View v) {
        if (mCircleBody == null) {
            return;
        }
        if (mCircleBody.getRole().equals("0")) {
            ToastUtil.show("查看申请列表");
        } else if (mCircleBody.getRole().equals("1")) {
            showConfirmDialog();
        } else if (mCircleBody.getRole().equals("10")) {
            if(TextUtils.isEmpty(mCircleBody.getCode())){
                joinCircle();
            }else{
                popWindow.showAtLocation(v, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }

        } else if (mCircleBody.getRole().equals("11")) {
            btJoin.setText(getResources().getString(R.string.wait_conform_txt));
            btJoin.setEnabled(false);
        }

    }

    private void showConfirmDialog() {
        ConfirmHintDialog dialog = new ConfirmHintDialog(this,
                R.style.loading_dialog, "确定退出",
                new ConfirmHintDialogListener() {

                    @Override
                    public void OnClick(View view) {
                        switch (view.getId()) {
                            case R.id.tv_confirm:
                                quitCirlce();
                                // ToastUtil.show("退出成功");
                                break;
                            case R.id.tv_cancel:
                                break;

                            default:
                                break;
                        }
                    }
                });
        dialog.show();
    }

    private void joinCircle() {
        dlgLoad.loading();
        RequestManager.request(this, new CircleJoinParams(circle_id),
                CircleJoinResponse.class, this, SPUtils.instance()
                        .getSocialPropEntity().getApp_socail_server());

    }

    @Override
    public void onResponse(BaseResponse response) {
        if ("0".equals(response.getResCode())) {
            if (response instanceof CircleDetailResponse) {
                Message message = new Message();
                message.what = REQUEST_DETAIL_SUCCESS;
                message.obj = response;
                handler.sendMessage(message);
            }
            if (response instanceof CircleMemberResponse) {
                Message message = new Message();
                message.what = REQUEST_CIRCLE_MEMBER_SUCCESS;
                message.obj = response;
                handler.sendMessage(message);
            }
            if (response instanceof CircleJoinResponse) {
                Message message = new Message();
                message.what = REQUEST_JOIN_CIRCLE_SUCCESS;
                message.obj = response;
                handler.sendMessage(message);
            }
            if (response instanceof CircleQuitResponse) {
                Message message = new Message();
                message.what = REQUEST_QUIT_CIRCLE_SUCCESS;
                message.obj = response;
                handler.sendMessage(message);
            }
            if (response instanceof CircleApplyMemberResponse) {
                Message message = new Message();
                message.what = REQUEST_APPLY_MEMBER_SUCCESS;
                message.obj = response;
                handler.sendMessage(message);
            } else if (response instanceof CircleMemberRegByCodeResponse) {
                Message message = new Message();
                message.what = REQUEST_MEMBER_REG_BY_CODE_SUCCESS;
                message.obj = response;
                handler.sendMessage(message);
            }
        } else {
            Message message = new Message();
            message.what = REQUEST_ERROR;
            message.obj = response;
            handler.sendMessage(message);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CIRCLE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    tv_circle_code.setText(data.getStringExtra(CIRCLE_CODE));
                }
                break;
            case NAME_REQUEST:
                if (resultCode == RESULT_OK) {
                    if (TextUtils.isEmpty(data.getStringExtra(MY_CIRCLE_NAME))) {
                        LoginEntity loginEntity = SPUtils.instance().getLoginEntity();
                        tv_my_circle_name.setText(TextUtils.isEmpty(loginEntity.getUname()) ? (String) SPUtils.instance()
                                .get(SPUtils.LOGIN_NAME, "") : loginEntity.getUname());
                    } else {
                        tv_my_circle_name.setText(data.getStringExtra(MY_CIRCLE_NAME));
                    }
                }
                break;
            case CircleCreatActivity.MODIFY:
                if (resultCode == RESULT_OK) {
                    getCircleDetail();
                }
                break;
        }
    }

    // 为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            popWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_2:
                    codeDialog = new Dialog(CircleDetailActivity.this, R.style.loading_dialog);
                    codeDialog.setContentView(R.layout.dialog_circle_code);
                    final EditText et_code = (EditText) codeDialog.findViewById(R.id.et_code);
                    TextView tv_confirm = (TextView) codeDialog.findViewById(R.id.tv_ok);
                    TextView tv_cancel = (TextView) codeDialog.findViewById(R.id.tv_cancel);
                    codeDialog.show();
                    tv_confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (TextUtils.isEmpty(et_code.getText())) {
                                ToastUtil.show("口令不能为空");
                            } else {
                                RequestManager.request(CircleDetailActivity.this, new CircleMemberRegByCodeParams(circle_id, et_code.getText().toString()),
                                        CircleMemberRegByCodeResponse.class, CircleDetailActivity.this, SPUtils
                                                .instance().getSocialPropEntity().getApp_socail_server());
                            }
                        }
                    });
                    tv_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            codeDialog.dismiss();
                        }
                    });
                    break;
                case R.id.btn_1:
//                    ToastUtil.show("直接加入");
                    joinCircle();
                    break;
                default:
                    break;
            }

        }

    };

    /**
     * 获取历史消息记录条目数
     */
    private void getHistoryMessageCount() {
        RongIM.getInstance().getLatestMessages(Conversation.ConversationType.GROUP, circle_id, 100, new RongIMClient.ResultCallback<List<io.rong.imlib.model.Message>>() {
            @Override
            public void onSuccess(List<io.rong.imlib.model.Message> messages) {
                handler.obtainMessage(UPDATE_MESSAGE_COUNT, messages.size()).sendToTarget();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                handler.obtainMessage(UPDATE_MESSAGE_COUNT, 0).sendToTarget();
            }
        });
    }
}