package com.easier.writepre.fragment;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.PkInfoGridAdapter;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.PkWorksDetailParams;
import com.easier.writepre.param.PkWorksOkParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.GiveOkResponse;
import com.easier.writepre.response.PkWorksDetailResponse;
import com.easier.writepre.social.ui.youku.PlayerFullActivity;
import com.easier.writepre.ui.NoSwipeBackActivity;
import com.easier.writepre.ui.PkOnLineDetailActivity;
import com.easier.writepre.ui.PkTeacherStudentListActivity;
import com.easier.writepre.ui.PkWorksAccuseActivity;
import com.easier.writepre.ui.PkWorksRemarkActivity;
import com.easier.writepre.ui.SquareImageLookActivity;
import com.easier.writepre.ui.UserInfoActivity;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.MyGridView;
import com.easier.writepre.widget.RoundImageView;
import com.sj.autolayout.utils.DateKit;

/**
 * @author kai.zhong
 */
public class PkMsgDetailFragment extends BaseFragment {
    private String pk_type;

    private String works_id;

    private PopupWindow popupwindow;

    private ImageView iv_pk_ok_num_info, iv_pk_report_info, img_video, img_video_icon, iv_video_icon;

    private RoundImageView iv_ts_head_info;

    private String user_id;

    private MyGridView gv_pk_ts_status0_info, gv_pk_ts_status1_info, gv_pk_ts_status2_info;

    private TextView tv_ts_uname_info, tv_ts_status_info, tv_ts_cata_title_info, tv_ts_uptime_info, tv_ts_city_info,
            tv_ts_works_no_info, tv_ts_teacher_info, tv_pk_status0_info, tv_pk_status1_info, tv_pk_status2_info,
            tv_pk_remark_num_info, tv_pk_ok_num_info, tv_video_write;

    private RelativeLayout rl_pk_video_info, rl_pk_ok_num_info, rl_pk_remark_num_info, rl_pk_share_info;

    private WindowManager windowManager;

    private int width;

    private android.widget.RelativeLayout.LayoutParams layoutParams;// 添加视频按钮的样式布局

    public PkMsgDetailFragment newInstance(String _id,String pk_type) {
        final PkMsgDetailFragment f = new PkMsgDetailFragment();
        final Bundle args = new Bundle();
        args.putString("_id", _id);
        args.putString("pk_type", pk_type);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        works_id = getArguments() != null ? getArguments().getString("_id") : "";
        pk_type= getArguments() != null ? getArguments().getString("pk_type") : "";
    }

    @Override
    public int getContextView() {
        return R.layout.fragment_pk_teacher_student_info;
    }

    @Override
    protected void init() {
        iv_video_icon = (ImageView) findViewById(R.id.iv_video_icon);

        tv_video_write = (TextView) findViewById(R.id.tv_video_write);

        iv_pk_report_info = (ImageView) findViewById(R.id.iv_pk_report_info);

        rl_pk_video_info = (RelativeLayout) findViewById(R.id.rl_pk_video_info);

        rl_pk_ok_num_info = (RelativeLayout) findViewById(R.id.rl_pk_ok_num_info);

        rl_pk_remark_num_info = (RelativeLayout) findViewById(R.id.rl_pk_remark_num_info);

        rl_pk_share_info = (RelativeLayout) findViewById(R.id.rl_pk_share_info);

        iv_pk_ok_num_info = (ImageView) findViewById(R.id.iv_pk_ok_num_info);

        tv_pk_ok_num_info = (TextView) findViewById(R.id.tv_pk_ok_num_info);

        tv_pk_remark_num_info = (TextView) findViewById(R.id.tv_pk_remark_num_info);

        tv_pk_status0_info = (TextView) findViewById(R.id.tv_pk_status0_info);

        tv_pk_status1_info = (TextView) findViewById(R.id.tv_pk_status1_info);

        tv_pk_status2_info = (TextView) findViewById(R.id.tv_pk_status2_info);

        gv_pk_ts_status0_info = (MyGridView) findViewById(R.id.gv_pk_ts_status0_info);

        gv_pk_ts_status1_info = (MyGridView) findViewById(R.id.gv_pk_ts_status1_info);

        gv_pk_ts_status2_info = (MyGridView) findViewById(R.id.gv_pk_ts_status2_info);

        iv_ts_head_info = (RoundImageView) findViewById(R.id.iv_ts_head_info);

        tv_ts_uname_info = (TextView) findViewById(R.id.tv_ts_uname_info);

        tv_ts_status_info = (TextView) findViewById(R.id.tv_ts_status_info);

        tv_ts_cata_title_info = (TextView) findViewById(R.id.tv_ts_cata_title_info);

        tv_ts_uptime_info = (TextView) findViewById(R.id.tv_ts_uptime_info);

        tv_ts_city_info = (TextView) findViewById(R.id.tv_ts_city_info);

        tv_ts_works_no_info = (TextView) findViewById(R.id.tv_ts_works_no_info);

        tv_ts_teacher_info = (TextView) findViewById(R.id.tv_ts_teacher_info);

        img_video = (ImageView) findViewById(R.id.img_video);

        img_video_icon = (ImageView) findViewById(R.id.img_video_icon);

        tv_pk_status0_info.setOnClickListener(this);

        tv_pk_status1_info.setOnClickListener(this);

        tv_pk_status2_info.setOnClickListener(this);

        rl_pk_ok_num_info.setOnClickListener(this);

        rl_pk_remark_num_info.setOnClickListener(this);

        rl_pk_share_info.setOnClickListener(this);

        rl_pk_video_info.setOnClickListener(this);

        iv_pk_report_info.setOnClickListener(this);

        iv_ts_head_info.setOnClickListener(this);

        windowManager = getActivity().getWindowManager();
        width = windowManager.getDefaultDisplay().getWidth();
        layoutParams = (android.widget.RelativeLayout.LayoutParams) img_video.getLayoutParams();
        layoutParams.height = width / 4;
        layoutParams.width = layoutParams.height;
        img_video.setLayoutParams(layoutParams);

        if (pk_type != null && pk_type.equals("view_pk")) {
            findViewById(R.id.ll_status_layout).setVisibility(View.GONE);
            findViewById(R.id.txt_video_title).setVisibility(View.GONE);
            findViewById(R.id.rl_video_info).setVisibility(View.GONE);
            findViewById(R.id.rl_pk_video_info).setVisibility(View.GONE);
            tv_ts_status_info.setVisibility(View.GONE);
        }

    }

    private void loadData() {
        RequestManager.request(getActivity(), new PkWorksDetailParams(works_id), PkWorksDetailResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // TODO Auto-generated method stub
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        // wv_detail.reload();
        super.onPause();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
    }

    /**
     * 点击查看大图
     *
     * @param position
     * @param urls
     */
    private void imageBrower(int position, String[] urls) {
        Intent intent = new Intent(getActivity(), SquareImageLookActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_INDEX, position);
        getActivity().startActivity(intent);
    }

    PkWorksDetailResponse.Repbody body;

    int remarkNum;

    @Override
    public void onResponse(BaseResponse response) {
        if ("0".equals(response.getResCode())) {
            if (response instanceof PkWorksDetailResponse) {
                PkWorksDetailResponse pkwdResult = (PkWorksDetailResponse) response;
                if (pkwdResult != null) {
                    body = pkwdResult.getRepBody();

                    tv_ts_uname_info.setText(body.getUname());

                    tv_ts_cata_title_info.setText(body.getCata_title());
                    tv_ts_cata_title_info.setVisibility(TextUtils.isEmpty(body.getCata_title()) ? View.GONE : View.VISIBLE);
                    tv_ts_uptime_info.setVisibility(TextUtils.isEmpty(body.getUptime()) ? View.GONE : View.VISIBLE);
                    tv_ts_uptime_info.setText(DateKit.timeFormat(body.getUptime().split("-")[0]));
                    tv_ts_city_info.setVisibility(TextUtils.isEmpty(body.getCity()) ? View.GONE : View.VISIBLE);
                    tv_ts_city_info.setText(body.getCity());

                    tv_ts_works_no_info.setVisibility(TextUtils.isEmpty(body.getWorks_no()) ? View.GONE : View.VISIBLE);
                    tv_ts_works_no_info.setText("NO:" + body.getWorks_no());

                    tv_pk_ok_num_info.setText("点赞/" + body.getOk_num()); //投票

                    tv_pk_remark_num_info.setText("评论/" + body.getRemark_num());

                    remarkNum = Integer.parseInt(body.getRemark_num());

                    user_id = body.getUser_id();

                    if (body.getIs_ok().equals("1")) {
                        iv_pk_ok_num_info.setBackgroundResource(R.drawable.dianzan_xuanzhong);
                        rl_pk_ok_num_info.setEnabled(false);
                    }
                    tv_ts_teacher_info.setVisibility(TextUtils.isEmpty(body.getTeacher()) ? View.GONE : View.VISIBLE);
                    tv_ts_teacher_info.setText("指导老师:" + body.getTeacher());
                    if (pk_type != null && !pk_type.equals("view_pk")) {
                        if (body.getStatus().equals("0")) {
                            tv_ts_status_info.setText("初选");
                            tv_pk_status0_info.setBackgroundResource(R.drawable.state_bg);
                            tv_pk_status1_info.setBackgroundResource(R.drawable.state_bg_no);
                            tv_pk_status2_info.setBackgroundResource(R.drawable.state_bg_no);
                            gv_pk_ts_status0_info.setVisibility(View.VISIBLE);
                            gv_pk_ts_status1_info.setVisibility(View.GONE);
                            gv_pk_ts_status2_info.setVisibility(View.GONE);
                        } else if (body.getStatus().equals("1")) {
                            tv_ts_status_info.setText("复选");
                            if (body.getWorks_1().getImgs() != null) {
                                tv_pk_status0_info.setBackgroundResource(R.drawable.state_bg_no);
                                tv_pk_status1_info.setBackgroundResource(R.drawable.state_bg);
                                tv_pk_status2_info.setBackgroundResource(R.drawable.state_bg_no);
                                gv_pk_ts_status0_info.setVisibility(View.GONE);
                                gv_pk_ts_status1_info.setVisibility(View.VISIBLE);
                                gv_pk_ts_status2_info.setVisibility(View.GONE);
                            } else {
                                tv_pk_status0_info.setBackgroundResource(R.drawable.state_bg);
                                tv_pk_status1_info.setBackgroundResource(R.drawable.state_bg_no);
                                tv_pk_status2_info.setBackgroundResource(R.drawable.state_bg_no);
                                gv_pk_ts_status0_info.setVisibility(View.VISIBLE);
                                gv_pk_ts_status1_info.setVisibility(View.GONE);
                                gv_pk_ts_status2_info.setVisibility(View.GONE);
                            }
                        } else if (body.getStatus().equals("2")) {
                            tv_ts_status_info.setText("现场大会");
                            if (body.getWorks_2().getImgs() != null) {
                                tv_pk_status0_info.setBackgroundResource(R.drawable.state_bg_no);
                                tv_pk_status1_info.setBackgroundResource(R.drawable.state_bg_no);
                                tv_pk_status2_info.setBackgroundResource(R.drawable.state_bg);
                                gv_pk_ts_status0_info.setVisibility(View.GONE);
                                gv_pk_ts_status1_info.setVisibility(View.GONE);
                                gv_pk_ts_status2_info.setVisibility(View.VISIBLE);
                            } else {
                                tv_pk_status0_info.setBackgroundResource(R.drawable.state_bg_no);
                                tv_pk_status1_info.setBackgroundResource(R.drawable.state_bg);
                                tv_pk_status2_info.setBackgroundResource(R.drawable.state_bg_no);
                                gv_pk_ts_status0_info.setVisibility(View.GONE);
                                gv_pk_ts_status1_info.setVisibility(View.VISIBLE);
                                gv_pk_ts_status2_info.setVisibility(View.GONE);
                            }
                        }
                    }else{
                        if (TextUtils.isEmpty(body.getTeacher())) {
                            findViewById(R.id.iv_guide).setVisibility(View.GONE);
                        }
                    }

                    if (!TextUtils.isEmpty(body.getYk_video_id())) {
                        iv_video_icon.setBackgroundResource(R.drawable.video_icon_red);
                        // holder.rl_pk_video.setEnabled(true);
                        // tv_video_write.setTextColor(this.getResources()
                        // .getColor(R.color.grey));
                        if (body.getYk_video_state().equals("ok")) {
                            tv_video_write.setText("视频临写");
                        } else if (body.getYk_video_state().equals("fail")) {
                            tv_video_write.setText("审核失败");
                        } else
                            tv_video_write.setText("审核中");
                    } else {
                        iv_video_icon.setBackgroundResource(R.drawable.video_icon);
                        // holder.rl_pk_video.setEnabled(false);
                        // tv_video_write.setTextColor(this.getResources()
                        // .getColor(R.color.maroon));

                    }
                    // 获取视频截图
                    if (!TextUtils.isEmpty(body.getYk_thumbnail())) {
                        img_video.setVisibility(View.VISIBLE);
                        img_video_icon.setVisibility(View.VISIBLE);
                        img_video_icon.bringToFront();

                        BitmapHelp.getBitmapUtils().display(img_video, body.getYk_thumbnail());

                        img_video.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                if (!TextUtils.isEmpty(body.getYk_video_id())) {
                                    if (body.getYk_video_state().equals("ok")) {
                                        Intent i = new Intent(getActivity(),
                                                PlayerFullActivity.class);
                                        i.putExtra("vid", body.getYk_video_id());
                                        getActivity().startActivity(i);
                                    } else if (body.getYk_video_state().equals("fail")) {
                                        ToastUtil.show("视频审核失败");
                                    } else {
                                        ToastUtil.show("视频审核中,请稍后再试");
                                    }
                                } else
                                    ToastUtil.show("暂无视频");
                            }
                        });
                    }

                    if (!body.getHead_img().equals("")) {
                        // ImageLoader.getInstance()
                        // .displayImage(
                        // (body.getHead_img().startsWith(
                        // Constant.XZPOSS) ? ossPath
                        // : headPath)
                        // + body.getHead_img(),
                        // iv_ts_head_info);
                        //BitmapHelp.getBitmapUtils().display(iv_ts_head_info, StringUtil.getHeadUrl(body.getHead_img()));
                        iv_ts_head_info.setImageView(StringUtil.getHeadUrl(body.getHead_img()));
                        iv_ts_head_info.setIconView(body.getIs_teacher());
                    }

                    gv_pk_ts_status0_info.setAdapter(
                            new PkInfoGridAdapter(body.getWorks_0().getImgs(), getActivity()));

                    gv_pk_ts_status0_info.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            imageBrower(position, body.getWorks_0().getImgs());
                        }
                    });

                    gv_pk_ts_status1_info.setAdapter(
                            new PkInfoGridAdapter(body.getWorks_1().getImgs(), getActivity()));

                    gv_pk_ts_status1_info.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            imageBrower(position, body.getWorks_1().getImgs());
                        }
                    });

                    gv_pk_ts_status2_info.setAdapter(
                            new PkInfoGridAdapter(body.getWorks_2().getImgs(), getActivity()));

                    gv_pk_ts_status2_info.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            imageBrower(position, body.getWorks_2().getImgs());
                        }
                    });

                    if (body.getWorks_1().getImgs() != null) {
                        tv_pk_status1_info.setVisibility(View.VISIBLE);
                    }

                    if (body.getWorks_2().getImgs() != null) {
                        tv_pk_status2_info.setVisibility(View.VISIBLE);
                    }
                }
            } else if (response instanceof GiveOkResponse) {
                tv_pk_ok_num_info.setText("点赞/" + (Integer.parseInt(body.getOk_num()) + 1));                                    //投票
                rl_pk_ok_num_info.setEnabled(false);
                iv_pk_ok_num_info.setBackgroundResource(R.drawable.dianzan_xuanzhong);
                PkTeacherStudentListActivity.GIVE_OK = true;
                PkOnLineDetailActivity.GIVE_OK = true;

            }
        } else
            ToastUtil.show(response.getResMsg());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_pk_status0_info:
                tv_pk_status0_info.setBackgroundResource(R.drawable.state_bg);
                tv_pk_status1_info.setBackgroundResource(R.drawable.state_bg_no);
                tv_pk_status2_info.setBackgroundResource(R.drawable.state_bg_no);
                gv_pk_ts_status0_info.setVisibility(View.VISIBLE);
                gv_pk_ts_status1_info.setVisibility(View.GONE);
                gv_pk_ts_status2_info.setVisibility(View.GONE);
                break;

            case R.id.tv_pk_status1_info:
                tv_pk_status0_info.setBackgroundResource(R.drawable.state_bg_no);
                tv_pk_status1_info.setBackgroundResource(R.drawable.state_bg);
                tv_pk_status2_info.setBackgroundResource(R.drawable.state_bg_no);
                gv_pk_ts_status0_info.setVisibility(View.GONE);
                gv_pk_ts_status1_info.setVisibility(View.VISIBLE);
                gv_pk_ts_status2_info.setVisibility(View.GONE);
                break;

            case R.id.tv_pk_status2_info:
                tv_pk_status0_info.setBackgroundResource(R.drawable.state_bg_no);
                tv_pk_status1_info.setBackgroundResource(R.drawable.state_bg_no);
                tv_pk_status2_info.setBackgroundResource(R.drawable.state_bg);
                gv_pk_ts_status0_info.setVisibility(View.GONE);
                gv_pk_ts_status1_info.setVisibility(View.GONE);
                gv_pk_ts_status2_info.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_pk_ok_num_info:
                if (LoginUtil.checkLogin(getActivity())) {
                    RequestManager.request(getActivity(), new PkWorksOkParams(works_id), GiveOkResponse.class, this,
                            SPUtils.instance().getSocialPropEntity().getApp_socail_server());
                }
                break;
            case R.id.rl_pk_remark_num_info:
                Intent intent = new Intent(getActivity(), PkWorksRemarkActivity.class);
                intent.putExtra("works_id", works_id);
                // startActivity(intent);
                startActivityForResult(intent, PkTeacherStudentListActivity.REQUEST_UPDATE_CODE);
                break;

            case R.id.rl_pk_video_info:
                if (!TextUtils.isEmpty(body.getYk_video_id())) {
                    if (body.getYk_video_state().equals("ok")) {
                        Intent i = new Intent(getActivity(), PlayerFullActivity.class);
                        i.putExtra("vid", body.getYk_video_id()); // XNzgyODExNDY4
                        getActivity().startActivity(i);
                    } else if (body.getYk_video_state().equals("fail")) {
                        ToastUtil.show("视频审核失败");
                    } else {
                        ToastUtil.show("视频审核中,请稍后再试");
                    }
                } else
                    ToastUtil.show("暂无视频");
                break;

            case R.id.rl_pk_share_info:

                if (body.getWorks_0().getImgs() != null) {
                    if (body.getWorks_0().getImgs().length > 0) {
                        ((NoSwipeBackActivity) getActivity()).sharedContentCustomizePK("#大会作品#", null,
                                SPUtils.instance().getSocialPropEntity().getShare_baseurl() + Constant.SHARE_PK_PATH
                                        + works_id,
                                SPUtils.instance().getSocialPropEntity().getShare_baseurl() + Constant.SHARE_PK_PATH
                                        + works_id,
                                StringUtil.getImgeUrl(body.getWorks_0().getImgs()[0]) + Constant.MIDDLE_IMAGE_SUFFIX);
                    } else
                        ((NoSwipeBackActivity) getActivity()).sharedContentCustomizePK("#大会作品#", null,
                                SPUtils.instance().getSocialPropEntity().getShare_baseurl() + Constant.SHARE_PK_PATH
                                        + works_id,
                                SPUtils.instance().getSocialPropEntity().getShare_baseurl() + Constant.SHARE_PK_PATH
                                        + works_id,
                                "http://www.shufap.net/xzpsrv/icon/icon01.png");

                } else
                    ((NoSwipeBackActivity) getActivity()).sharedContentCustomizePK("#大会作品#", null,
                            SPUtils.instance().getSocialPropEntity().getShare_baseurl() + Constant.SHARE_PK_PATH + works_id,
                            SPUtils.instance().getSocialPropEntity().getShare_baseurl() + Constant.SHARE_PK_PATH + works_id,
                            "http://www.shufap.net/xzpsrv/icon/icon01.png"); // gv_pk_ts_status0_info

                break;

//		case R.id.img_back:
//			finish();
//			break;
            case R.id.iv_pk_report_info:
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    return;
                } else {
                    popView();
                    popupwindow.showAsDropDown(iv_pk_report_info, 0, 0);
                }
                break;
            case R.id.iv_ts_head_info:
                Intent iInfo = new Intent(getActivity(), UserInfoActivity.class);
                iInfo.putExtra("user_id", user_id);
                startActivity(iInfo);
                break;
            default:
                break;
        }

    }

    /**
     * 弹出詳情和舉報菜單
     */
    public void popView() {
        View customView = getActivity().getLayoutInflater().inflate(R.layout.pop_report_details, null, false);
        popupwindow = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        popupwindow.setAnimationStyle(R.style.AnimationFade);
        popupwindow.setFocusable(true);
        popupwindow.setOutsideTouchable(true); // 设置点击屏幕其它地方弹出框消失
        popupwindow.setBackgroundDrawable(new BitmapDrawable());

        TextView pop_tv_report = (TextView) customView.findViewById(R.id.pop_tv_report);

        TextView pop_tv_details = (TextView) customView.findViewById(R.id.pop_tv_details);

        pop_tv_report.setVisibility(View.GONE);

        pop_tv_details.setText("举报");

        pop_tv_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupwindow.dismiss();
                Intent intent = new Intent(getActivity(), PkWorksAccuseActivity.class);
                intent.putExtra("works_id", works_id); // 举报
                startActivity(intent);
            }
        });
    }


}
