package com.easier.writepre.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.entity.UserInfo;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.http.WritePreListener;
import com.easier.writepre.manager.ActStackManager;
import com.easier.writepre.param.AttentionParams;
import com.easier.writepre.param.UnAttentionParams;
import com.easier.writepre.param.UserInfoParams;
import com.easier.writepre.response.AttentionResponse;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.RongYunCheckFriendResponse;
import com.easier.writepre.response.UnAttentionResponse;
import com.easier.writepre.response.UserInfoResponse;
import com.easier.writepre.rongyun.RongYunUtils;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.RoundImageView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.rong.imkit.RongIM;

/**
 * 用户信息查询(个人资料)
 *
 * @author kai.zhong
 */
public class UserInfoActivity extends BaseActivity {

    private UserInfo body;

    private int tYear; // 获取当前年份

    private RoundImageView img_head;

    private TextView tv_uname, tv_real_uname, tv_age, tv_city, tv_school;

    private TextView tv_square, tv_circle, tv_fans, tv_attention, tv_signature,
            tv_likepost, tv_begoodat;

    private Button btn_attention;
    private String user_id;
    private View rel_headlayout;

    private TextView tv_chat;

    @Override
    public void onTopLeftClick(View view) {
        Intent data = getIntent();
        data.putExtra("type",
                Integer.parseInt(btn_attention.getTag().toString()));
        setResult(RESULT_OK, data);
        super.onTopLeftClick(view);
    }

    @Override
    public void onTopLeftTextClick(View view) {
        super.onTopLeftTextClick(view);
        ActStackManager.getInstance().popAllActivityFromTag2Top();
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.user_info);
        user_id = getIntent().getStringExtra("user_id");
        init();
        // loadUserInfoData(user_id);
        if (ActStackManager.getInstance().getTagAct() == null) {
            ActStackManager.getInstance().setTagAct(this);
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (!TextUtils.isEmpty(user_id))
            loadUserInfoData(user_id);
    }

    private void loadUserInfoData(String user_id) {
        RequestManager.request(this, new UserInfoParams(user_id),
                UserInfoResponse.class, this, SPUtils.instance()
                        .getSocialPropEntity().getApp_socail_server());
    }

    /**
     * 取消关注弹窗
     */
    public void showDelDialog() {

        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_islogin,
                null);
        TextView tv = (TextView) view.findViewById(R.id.tv_login_now);
        tv.setText("确定取消关注?");
        TextView confirm = (TextView) view.findViewById(R.id.tv_login);
        confirm.setText("确定");
        confirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                umengCancleAttention();
                requestUnAttention();
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_cancel).setOnClickListener(
                new OnClickListener() {

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

    private void init() {
        setTopTitle("个人资料");
        rel_headlayout = (View) findViewById(R.id.headlayout);
        LayoutParams layoutParams = (LayoutParams) rel_headlayout
                .getLayoutParams();
        layoutParams.width = WritePreApp.getApp().getWidth(1f);
        layoutParams.height = WritePreApp.getApp().getWidth(0.62f);
        rel_headlayout.setLayoutParams(layoutParams);
        btn_attention = (Button) findViewById(R.id.cb_attention);
        tv_square = (TextView) findViewById(R.id.tv_square);
        tv_circle = (TextView) findViewById(R.id.tv_circle);
        tv_fans = (TextView) findViewById(R.id.tv_fans);
        tv_attention = (TextView) findViewById(R.id.tv_attention);
        tYear = Calendar.getInstance().get(Calendar.YEAR);
        img_head = (RoundImageView) findViewById(R.id.iv_img_head);
        tv_uname = (TextView) findViewById(R.id.tv_uname);
        tv_real_uname = (TextView) findViewById(R.id.tv_real_uname);
        tv_age = (TextView) findViewById(R.id.tv_age);
        tv_city = (TextView) findViewById(R.id.tv_city);
        tv_school = (TextView) findViewById(R.id.tv_school);
        tv_signature = (TextView) findViewById(R.id.tv_sign);
        tv_likepost = (TextView) findViewById(R.id.tv_likepost);
        tv_begoodat = (TextView) findViewById(R.id.tv_begoodat);

        tv_chat = ((TextView) findViewById(R.id.tv_chat));
        tv_chat.setOnClickListener(this);
        btn_attention.setTag("0");
        btn_attention.setOnClickListener(this);

        showCloseView();
        findViewById(R.id.rel_square).setOnClickListener(this);
        findViewById(R.id.rel_circle).setOnClickListener(this);
        findViewById(R.id.rel_fans).setOnClickListener(this);
        findViewById(R.id.rel_attention).setOnClickListener(this);
        findViewById(R.id.img_edit_sign).setVisibility(View.GONE);

        img_head.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!body.getHead_img().equals("")) {
                    imageBrower(0, new String[]{body.getHead_img()});
                } else {
                    ToastUtil.show("该用户没有上传头像");
                }

            }
        });
        if (user_id.equals(SPUtils.instance().getLoginEntity().get_id())) {
            btn_attention.setVisibility(View.GONE);
            tv_chat.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rel_square:
                // 广场帖
                Intent intent = new Intent(UserInfoActivity.this,
                        UserSquarePostActivity.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("user_name", tv_uname.getText());
                this.startActivity(intent);
                break;
            case R.id.rel_circle:
                // 圈子
                Intent intent2 = new Intent(UserInfoActivity.this,
                        UserCircleActivity.class);
                intent2.putExtra("user_id", user_id);
                intent2.putExtra("user_name", tv_uname.getText());
                this.startActivity(intent2);
                break;
            case R.id.rel_fans:
                // 粉丝
                Intent intent3 = new Intent(UserInfoActivity.this,
                        AttentionActivity.class);
                intent3.putExtra("id", user_id);
                intent3.putExtra("select_no", 1);
                intent3.putExtra("name", tv_uname.getText());
                this.startActivity(intent3);
                break;
            case R.id.rel_attention:
                // 关注
                Intent intent4 = new Intent(UserInfoActivity.this,
                        AttentionActivity.class);
                intent4.putExtra("id", user_id);
                intent4.putExtra("select_no", 0);
                intent4.putExtra("name", tv_uname.getText());
                this.startActivity(intent4);
                break;
            case R.id.cb_attention:
                // 关注TA 0 未关注 1 已关注
                String tagType = (String) btn_attention.getTag();
                if (TextUtils.equals(tagType, "0")) {
                    umengAttention();
                    requestAttention();
                } else {
                    showDelDialog();
                }
                break;
            case R.id.tv_chat:
                //私聊
                umengSingleIM();
                if (body != null) {
                    RongYunUtils.getInstances().requestCheckIsFriend(this, body.get_id(), new WritePreListener<BaseResponse>() {
                        @Override
                        public void onResponse(BaseResponse response) {
                            if ("0".equals(response.getResCode())) {
                                if (!TextUtils.equals((String) SPUtils.instance().get(SPUtils.RONGYUN_TOKEN, ""), "")) {
                                    RongYunCheckFriendResponse checkFriendResponse = (RongYunCheckFriendResponse) response;
                                    if (checkFriendResponse.getRepBody().getRes().equals("ok")) {
                                        if (RongIM.getInstance() != null) {
                                            RongIM.getInstance().startPrivateChat(
                                                    UserInfoActivity.this,
                                                    user_id,
                                                    tv_uname.getText().toString());
                                        }
                                    } else {
                                        ToastUtil.show("还不是相互关注的好友,不能发起私聊!");
                                    }
                                } else {
                                    Intent intentLogin = new Intent(UserInfoActivity.this, LoginActivity.class);
                                    UserInfoActivity.this.startActivity(intentLogin);
                                }
                            } else {
                                ToastUtil.show(response.getResMsg());
                            }
                        }


                        @Override
                        public void onResponse(String tag, BaseResponse response) {

                        }
                    });

                } else {
                    ToastUtil.show("无法获取用户信息!");
                }


                break;
            default:
                break;
        }
    }


    /**
     * 友盟统计关注
     */
    private void umengAttention() {
        //友盟统计
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.TYPE_FIV));
        var.add("关注");
        YouMengType.onEvent(this, var, 1, tv_uname.getText().toString());
    }

    /**
     * 友盟统计发起私聊
     */
    private void umengSingleIM() {
        //友盟统计
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.TYPE_FIV));
        var.add("发起私聊");
        YouMengType.onEvent(this, var, 1, tv_uname.getText().toString());
    }

    /**
     * 友盟统计取消关注
     */
    private void umengCancleAttention() {
        //友盟统计
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.TYPE_FIV));
        var.add("取消关注");
        YouMengType.onEvent(this, var, 1, tv_uname.getText().toString());
    }

    /**
     * 请求关注
     */
    private void requestAttention() {
        AttentionParams parms = new AttentionParams(user_id);
        RequestManager
                .request(this, parms, AttentionResponse.class, this, SPUtils
                        .instance().getSocialPropEntity()
                        .getApp_socail_server());
    }

    /**
     * 请求取消关注
     */
    private void requestUnAttention() {
        UnAttentionParams parms = new UnAttentionParams(user_id);
        RequestManager
                .request(this, parms, UnAttentionResponse.class, this, SPUtils
                        .instance().getSocialPropEntity()
                        .getApp_socail_server());
    }

    private void imageBrower(int position, String[] urls) {
        Intent intent = new Intent(UserInfoActivity.this,
                SquareImageLookActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_INDEX, position);
        intent.putExtra("flag", true);
        UserInfoActivity.this.startActivity(intent);
    }

    @Override
    public void onResponse(BaseResponse response) {
        if ("0".equals(response.getResCode())) {
            if (response instanceof UserInfoResponse) {
                UserInfoResponse userInfoResult = (UserInfoResponse) response;
                if (userInfoResult != null) {
                    body = userInfoResult.getRepBody();
                    tv_uname.setText(body.getUname());
                    tv_real_uname.setText(body.getReal_name());

                    if (!body.getBirth_day().equals("")) {
                        tv_age.setText((tYear - Integer.parseInt(body
                                .getBirth_day().split("-")[0])) + "");
                    } else {
                        tv_age.setText("");
                    }
                    if (!TextUtils.isEmpty(body.getSignature())) {
                        tv_signature.setText(body.getSignature());
                    } else {
                        tv_signature.setText("这家伙很懒，什么也没留下！");
                    }

                    tv_school.setText(body.getSchool_name());
                    tv_city.setText(body.getAddr());
                    tv_square.setText(body.getPost_cnt());
                    tv_circle.setText(body.getCircle_cnt());
                    tv_fans.setText(body.getCare_me_num());
                    tv_attention.setText(body.getMy_care_num());
                    if (TextUtils.equals(body.getIs_care(), "0")) {
                        btn_attention.setTag("0");
                        btn_attention.setText("关注TA");
                    } else {
                        btn_attention.setTag("1");
                        btn_attention.setText("取消关注");
                    }
                    tv_likepost.setText(body.getBei_tie());
                    tv_begoodat.setText(body.getGood_at());
                    img_head.setImageView(StringUtil.getHeadUrl(body
                            .getHead_img()));
                    img_head.setIconView(body.getIs_teacher());

                }
            } else if (response instanceof AttentionResponse) {
                // TODO 关注接口返回
                btn_attention.setTag("1");
                btn_attention.setText("取消关注");
                ToastUtil.show("已关注");
                loadUserInfoData(user_id);

            } else if (response instanceof UnAttentionResponse) {
                // TODO 取消关注接口返回
                btn_attention.setTag("0");
                btn_attention.setText("关注TA");
                ToastUtil.show("已取消关注");
                loadUserInfoData(user_id);

            }
        } else
            ToastUtil.show(response.getResMsg());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ActStackManager.getInstance().getTagAct() == this) {
            ActStackManager.getInstance().setTagAct(null);
        }
    }
}
