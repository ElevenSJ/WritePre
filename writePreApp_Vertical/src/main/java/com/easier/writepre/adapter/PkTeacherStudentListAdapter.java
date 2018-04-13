package com.easier.writepre.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.entity.PkContentInfo;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.http.WritePreListener;
import com.easier.writepre.param.PkWorksOkParams;
import com.easier.writepre.param.PkWorksRefreshParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.GiveOkResponse;
import com.easier.writepre.response.PkWorksRefreshResponse;
import com.easier.writepre.social.ui.youku.PlayerFullActivity;
import com.easier.writepre.ui.NoSwipeBackActivity;
import com.easier.writepre.ui.PkMsgDetailActivity;
import com.easier.writepre.ui.PkTeacherStudentListActivity;
import com.easier.writepre.ui.PkWorksAccuseActivity;
import com.easier.writepre.ui.PkWorksManageActivity;
import com.easier.writepre.ui.PkWorksRemarkActivity;
import com.easier.writepre.ui.UserInfoActivity;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.RoundImageView;
import com.sj.autolayout.utils.AutoUtils;
import com.sj.autolayout.utils.DateKit;

import java.util.ArrayList;

/**
 * @author kai.zhong
 */
@SuppressLint("InflateParams")
public class PkTeacherStudentListAdapter extends BaseAdapter implements WritePreListener<BaseResponse> {

    private String pk_id;

    private String pk_type;

    private int flag; // 2表示我的

    private Context mContext;

    private ArrayList<PkContentInfo> list;

    private PopupWindow popupwindow;

    private ArrayList<String> imgsUrl; // oss 图片路径

    private LayoutInflater mInflater;

    private ImageView[] indicator_imgs;// 存放引到图片数组

    private ArrayList<ImageView[]> pImageViews;

    public PkTeacherStudentListAdapter(Context context, String pk_id, String pk_type, ArrayList<PkContentInfo> list, ArrayList<ImageView[]> pImageViews,
                                       int flag) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        this.pk_id = pk_id;
        this.pk_type = pk_type;
        this.list = list;
        this.pImageViews = pImageViews;
        this.flag = flag;
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public PkContentInfo getItem(int position) {
        if ((list != null && list.size() > 0) && (position >= 0 && position < list.size())) {
            return list.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.pk_teacher_student_list_item, parent, false);

            holder = new ViewHolder();

            holder.iv_video_icon = (ImageView) convertView.findViewById(R.id.iv_video_icon);
            holder.iv_guide= (ImageView) convertView.findViewById(R.id.iv_guide);
            holder.tv_pk_city = (TextView) convertView.findViewById(R.id.tv_pk_city);
            holder.tv_video_write = (TextView) convertView.findViewById(R.id.tv_video_write);
            holder.mPager = (ViewPager) convertView.findViewById(R.id.pk_image_view_pager);
            //  holder.mPager.setNestedpParent(parent);
            holder.dot_containter = (LinearLayout) convertView.findViewById(R.id.pk_linear_indicator);

            holder.pop_iv_report_details = (ImageView) convertView.findViewById(R.id.pop_iv_report_details);

            holder.tv_pk_uname = (TextView) convertView.findViewById(R.id.tv_pk_uname);

            holder.tv_pk_status = (TextView) convertView.findViewById(R.id.tv_pk_status);

            holder.tv_pk_teacher = (TextView) convertView.findViewById(R.id.tv_pk_teacher);

            holder.tv_pk_cata_title = (TextView) convertView.findViewById(R.id.tv_pk_cata_title);

            holder.tv_pk_uptime = (TextView) convertView.findViewById(R.id.tv_pk_uptime);

            holder.tv_pk_works_no = (TextView) convertView.findViewById(R.id.tv_pk_works_no);

            holder.iv_pk_head_img = (RoundImageView) convertView.findViewById(R.id.iv_pk_head_img);

            holder.rl_pk_video = (RelativeLayout) convertView.findViewById(R.id.rl_pk_video);

            holder.rl_pk_ok_num = (RelativeLayout) convertView.findViewById(R.id.rl_pk_ok_num);

            holder.rl_pk_remark_num = (RelativeLayout) convertView.findViewById(R.id.rl_pk_remark_num);

            holder.rl_pk_share = (RelativeLayout) convertView.findViewById(R.id.rl_pk_share);

            holder.tv_pk_remark_num = (TextView) convertView.findViewById(R.id.tv_pk_remark_num);

            holder.tv_pk_ok_num = (TextView) convertView.findViewById(R.id.tv_pk_ok_num);

            holder.iv_pk_ok_num = (ImageView) convertView.findViewById(R.id.iv_pk_ok_num);

            convertView.setTag(holder);
            AutoUtils.autoSize(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final PkContentInfo cinfo = (PkContentInfo) getItem(position);

        holder.tv_pk_uname.setText(StringUtil.FormatStrLength(cinfo.getUname(), 4));

        if (pk_type!=null&&pk_type.equals("view_pk")) {
            holder.tv_pk_status.setVisibility(View.GONE);
            holder.iv_guide.setVisibility(TextUtils.isEmpty(cinfo.getTeacher()) ? View.GONE : View.VISIBLE);
            holder.rl_pk_video.setVisibility(View.GONE);
        } else {
            if (cinfo.getStatus().equals("0")) {
                holder.tv_pk_status.setText("初选");
            } else if (cinfo.getStatus().equals("1")) {
                holder.tv_pk_status.setText("复选");
            } else {
                holder.tv_pk_status.setText("现场大会");
            }
        }

        holder.tv_pk_teacher.setVisibility(TextUtils.isEmpty(cinfo.getTeacher()) ? View.GONE : View.VISIBLE);
        holder.tv_pk_teacher.setText("指导老师:" + cinfo.getTeacher());

        holder.tv_pk_cata_title.setVisibility(TextUtils.isEmpty(cinfo.getCata_title()) ? View.GONE : View.VISIBLE);
        holder.tv_pk_cata_title.setText(cinfo.getCata_title());

        holder.tv_pk_uptime.setText(DateKit.timeFormat(cinfo.getUptime().split("-")[0]));
        holder.tv_pk_uptime.setVisibility(TextUtils.isEmpty(cinfo.getUptime()) ? View.GONE : View.VISIBLE);

        holder.tv_pk_city.setText(cinfo.getCity());
        holder.tv_pk_city.setVisibility(TextUtils.isEmpty(cinfo.getCity()) ? View.GONE : View.VISIBLE);

        holder.tv_pk_works_no.setText("NO:" + cinfo.getWorks_no());

        holder.tv_pk_remark_num.setText("评论/" + cinfo.getRemark_num());

        holder.tv_pk_ok_num.setText("点赞/" + cinfo.getOk_num());   //投票

        if (cinfo.getIs_ok().equals("1")) {
            holder.rl_pk_ok_num.setEnabled(false);
            holder.iv_pk_ok_num.setBackgroundResource(R.drawable.dianzan_xuanzhong);
        } else {
            holder.rl_pk_ok_num.setEnabled(true);
            holder.iv_pk_ok_num.setBackgroundResource(R.drawable.dianzan);
        }

        holder.iv_pk_head_img.setImageView(StringUtil.getHeadUrl(cinfo.getHead_img()));
        holder.iv_pk_head_img.setIconView(cinfo.getIs_teacher());
        holder.iv_pk_head_img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserInfoActivity.class);
                intent.putExtra("user_id", cinfo.getUser_id());
                mContext.startActivity(intent);
            }
        });

        holder.pop_iv_report_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    return;
                } else {
                    popView(cinfo.get_id(), position);
                    popupwindow.showAsDropDown(holder.pop_iv_report_details, 0, 0);
                }
            }
        });

        holder.rl_pk_share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (pImageViews.get(position).length != 7) {
                    requestShare(holder.mPager, cinfo.get_id(), cinfo.getTitle());
                } else {
                    requestShare(null, cinfo.get_id(), cinfo.getTitle());
                }

            }
        });

        holder.rl_pk_remark_num.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PkWorksRemarkActivity.class);
                intent.putExtra("works_id", cinfo.get_id());
                PkTeacherStudentListActivity.LIST_INDEX = position;
                ((FragmentActivity) mContext).startActivityForResult(intent,
                        PkTeacherStudentListActivity.REQUEST_UPDATE_CODE);
                // mContext.startActivity(intent);
            }
        });

        holder.rl_pk_ok_num.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (LoginUtil.checkLogin(mContext)) {
                    requestGiveOk(cinfo.get_id());
                    holder.tv_pk_ok_num.setText("点赞/" + (Integer.parseInt(cinfo.getOk_num()) + 1)); //投票 理想状态下调用接口成功的
                    // 最好在接口正确返回加1
                    cinfo.setOk_num((Integer.parseInt(cinfo.getOk_num()) + 1) + "");
                    cinfo.setIs_ok("1");
                    holder.rl_pk_ok_num.setEnabled(false);
                    holder.iv_pk_ok_num.setBackgroundResource(R.drawable.dianzan_xuanzhong);
                    notifyDataSetChanged();
                }
            }
        });

        if (!TextUtils.isEmpty(cinfo.getYk_video_id())) {
            // holder.tv_video_write.setTextColor(mContext.getResources()
            // .getColor(R.color.grey));
            holder.iv_video_icon.setBackgroundResource(R.drawable.video_icon_red);
            if (cinfo.getYk_video_state().equals("ok")) {
                holder.tv_video_write.setText("视频临写");
            } else if (cinfo.getYk_video_state().equals("fail")) {
                holder.tv_video_write.setText("审核失败");
            } else
                holder.tv_video_write.setText("审核中");
        } else {
            // holder.rl_pk_video.setEnabled(false);
            // holder.tv_video_write.setTextColor(mContext.getResources()
            // .getColor(R.color.maroon));
            holder.iv_video_icon.setBackgroundResource(R.drawable.video_icon);
            holder.tv_video_write.setText("视频临写");
        }

        holder.rl_pk_video.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(cinfo.getYk_video_id())) {
                    if (cinfo.getYk_video_state().equals("ok")) {
                        Intent i = new Intent(mContext, PlayerFullActivity.class);
                        i.putExtra("vid", cinfo.getYk_video_id()); // XNzgyODExNDY4
                        mContext.startActivity(i);
                    } else if (cinfo.getYk_video_state().equals("fail")) {
                        ToastUtil.show("视频审核失败");
                    } else {
                        ToastUtil.show("视频审核中,请稍后再试");
                    }
                } else
                    ToastUtil.show("暂无视频");
            }
        });

        /**
         * 图片滑动
         */
        if (position > (9)) {
            indicator_imgs = pImageViews.get(0);
        } else {
            indicator_imgs = pImageViews.get(position);
        }


        if (indicator_imgs.length == 7) { // 没有图片处理
            holder.dot_containter.setVisibility(View.GONE);
            holder.mPager.setVisibility(View.GONE);
            return convertView;
        } else if (indicator_imgs.length == 1) {
            holder.dot_containter.setVisibility(View.GONE);
        } else {
            holder.dot_containter.setVisibility(View.VISIBLE);
            holder.mPager.setVisibility(View.VISIBLE);
        }

        // if (indicator_imgs.length == 1) {
        // holder.dot_containter.setVisibility(View.GONE);
        // } else {
        // holder.dot_containter.setVisibility(View.VISIBLE);
        // }

        // LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
        // 800); // 图片高度
        //
        // holder.mPager.setLayoutParams(params);

        imgsUrl = new ArrayList<String>();

        if (cinfo.getWorks().getImgs() != null) {
            for (int i = 0; i < cinfo.getWorks().getImgs().length; i++) {
                imgsUrl.add(StringUtil.getImgeUrl(cinfo.getWorks().getImgs()[i]));
            }
        } else {
            return convertView;
        }

        PkImageSlidePageAdapter mPagerAdapter = new PkImageSlidePageAdapter(mContext, imgsUrl,
                cinfo.getWorks().getImgs());
        holder.mPager.setAdapter(mPagerAdapter);
        holder.mPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                LogUtils.e("选中第几个" + arg0 + "");
                indicator_imgs = pImageViews.get(position);
                for (int i = 0; i < indicator_imgs.length; i++) { // null异常
                    indicator_imgs[i].setImageResource(R.drawable.dot_white);
                }
                // 改变当前背景图片为：选中
                indicator_imgs[arg0].setImageResource(R.drawable.dot_red);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        // 初始化小圆点数组
        // indicator_imgs = pImageViews.get(position);
        initIndicator(holder.dot_containter, imgsUrl.size());
        return convertView;
    }

    private static class ViewHolder {
        ViewPager mPager;
        LinearLayout dot_containter;
        RoundImageView iv_pk_head_img;
        ImageView pop_iv_report_details, iv_pk_ok_num; // 弹出举报详情菜单
        ImageView iv_video_icon;
        ImageView iv_guide;
        // RelativeLayout pop_rl_report_details; // 弹出举报详情菜单
        RelativeLayout rl_pk_video, rl_pk_ok_num, rl_pk_remark_num, rl_pk_share;
        TextView tv_pk_uname, tv_pk_status, tv_pk_teacher, tv_pk_cata_title, tv_pk_city, tv_pk_uptime, tv_pk_works_no,
                tv_pk_remark_num, tv_pk_ok_num, tv_video_write;
    }

    public void requestShare(View view, String id, String txt) {
        ((NoSwipeBackActivity) mContext).sharedContentCustomize("#大会作品#", txt,
                SPUtils.instance().getSocialPropEntity().getShare_baseurl() + Constant.SHARE_PK_PATH + id,
                SPUtils.instance().getSocialPropEntity().getShare_baseurl() + Constant.SHARE_PK_PATH + id, view);
    }

    private void initIndicator(LinearLayout pDotview_contair, int size) {
        ImageView imgView;
        pDotview_contair.removeAllViews();
        for (int i = 0; i < size; i++) {
            imgView = new ImageView(mContext);
            LinearLayout.LayoutParams params_linear = new LinearLayout.LayoutParams(20, 20); // 设置原点大小
            params_linear.setMargins(7, 10, 7, 10);
            imgView.setLayoutParams(params_linear);
            if (i >= size) {
                return;
            }

            try {
                indicator_imgs[i] = imgView;
            } catch (Exception e) {
                return;
            }
            if (i == 0) { // 初始化第一个为选中状态
                indicator_imgs[i].setImageResource(R.drawable.dot_red);  //setBackgroundResource
            } else {
                indicator_imgs[i].setImageResource(R.drawable.dot_white);
            }
            pDotview_contair.addView(indicator_imgs[i]);
        }
    }

    /**
     * 点赞
     *
     * @param works_id
     */
    public void requestGiveOk(String works_id) {
        RequestManager.request(mContext, new PkWorksOkParams(works_id), GiveOkResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    /**
     * 作品刷新
     *
     * @param works_id
     */
    public void requestWorksRefresh(String works_id) {
        RequestManager.request(mContext, new PkWorksRefreshParams(works_id), PkWorksRefreshResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    /**
     * 弹出詳情和舉報菜單
     */
    public void popView(final String id, final int position) {
        View customView = mInflater.inflate(R.layout.pop_report_details, null, false);
        popupwindow = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        popupwindow.setAnimationStyle(R.style.AnimationFade);
        popupwindow.setFocusable(true);
        popupwindow.setOutsideTouchable(true); // 设置点击屏幕其它地方弹出框消失
        popupwindow.setBackgroundDrawable(new BitmapDrawable());
        TextView tv_report = (TextView) customView.findViewById(R.id.pop_tv_report);
        TextView tv_details = (TextView) customView.findViewById(R.id.pop_tv_details);

        if (flag == 2) {
            tv_details.setText("管理");
            tv_report.setText("刷新");
        } else {
            tv_details.setText("详情");
            tv_report.setText("举报");
        }

        tv_report.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (flag == 2) { // 刷新
                    requestWorksRefresh(id);
                } else {// 举报
                    Intent intent = new Intent(mContext, PkWorksAccuseActivity.class);
                    intent.putExtra("pk_id", pk_id);
                    intent.putExtra("works_id", id);
                    mContext.startActivity(intent);
                }
                popupwindow.dismiss();
            }
        });

        tv_details.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent;
                if (flag == 2) { // 管理
                    intent = new Intent(mContext, PkWorksManageActivity.class);
                    intent.putExtra("pk_id", pk_id);
                    intent.putExtra("pk_type", pk_type);
                    intent.putExtra("manage_works_id", id);
                    ((FragmentActivity) mContext).startActivityForResult(intent,
                            PkTeacherStudentListActivity.REQUEST_UPDATE_CODE);
                } else { // 详情
                    intent = new Intent(mContext, PkMsgDetailActivity.class);
                    intent.putExtra("pk_id", pk_id);
                    intent.putExtra("pk_type", pk_type);
                    intent.putExtra("data", list);
                    intent.putExtra("position", position);
                    intent.putExtra("status", list.get(position).getStatus());
                    intent.putExtra("role", list.get(position).getRole());
                    intent.putExtra("pk_cata_id", list.get(position).getPk_cata_id());
                    intent.putExtra("city", list.get(position).getCity());
                    intent.putExtra("flag", PkTeacherStudentListActivity.flag);
                    PkTeacherStudentListActivity.LIST_INDEX = position;
                    PkTeacherStudentListActivity.GIVE_OK = false;
                    PkTeacherStudentListActivity.COMMENT = false;
                    ((FragmentActivity) mContext).startActivityForResult(intent,
                            PkTeacherStudentListActivity.REQUEST_ZAN_UPDATE_CODE);
                }
                // mContext.startActivity(intent);
                popupwindow.dismiss();
            }
        });

    }

    @Override
    public void onResponse(BaseResponse response) {
        if ("0".equals(response.getResCode())) {
            if (response instanceof PkWorksRefreshResponse) {
                ToastUtil.show("刷新成功");
                ((PkTeacherStudentListActivity) mContext).clear();
                ((PkTeacherStudentListActivity) mContext).loadMyData();
            }
        }
    }

    @Override
    public void onResponse(String tag, BaseResponse response) {
        // TODO Auto-generated method stub

    }
}
