package com.easier.writepre.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.entity.TaskHomeWorkSubmitInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.http.WritePreListener;
import com.easier.writepre.param.TaskWorkConfirmParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.TaskWorkConfirmResponse;
import com.easier.writepre.ui.SquareImageLookActivity;
import com.easier.writepre.ui.TaskDetailActivity;
import com.easier.writepre.ui.UserInfoActivity;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.RoundImageView;
import com.easier.writepre.widget.TaskChildViewPager;
import com.sj.autolayout.utils.AutoUtils;
import com.sj.autolayout.utils.DateKit;

import java.util.ArrayList;
import java.util.List;

/**
 * 书法师学生提交的作业适配器
 */
public class TaskHomeWorkStuSubAdapter extends BaseAdapter implements WritePreListener<BaseResponse> {

    private Context mContext;

    private List<TaskHomeWorkSubmitInfo> list = new ArrayList<TaskHomeWorkSubmitInfo>();

    public TaskHomeWorkStuSubAdapter(Context context) {
        mContext = context;
    }

    private boolean needShowImage, isTeacher;
    private Handler handler;

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setNeedShowImage(boolean needShowImage) {
        this.needShowImage = needShowImage;
    }

    public void setIsTeacher(boolean isTeacher) {
        this.isTeacher = isTeacher;
    }

    public void setData(List<TaskHomeWorkSubmitInfo> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(List<TaskHomeWorkSubmitInfo> data) {
        list.addAll(data);
        notifyDataSetChanged();
    }

    public List<TaskHomeWorkSubmitInfo> getData() {
        return list;
    }

    public String getLastId() {
        if (list.isEmpty()) {
            return "9";
        } else {
            return list.get(list.size() - 1).get_id();
        }
    }

    public void clearData() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public TaskHomeWorkSubmitInfo getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_taskstusub, parent, false);
            holder.rel_images = (RelativeLayout) convertView.findViewById(R.id.image_layout);
            holder.play_video = (ImageView) convertView.findViewById(R.id.play_video);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tv_tips = (TextView) convertView.findViewById(R.id.tv_tips);
            holder.tv_learn_time = (TextView) convertView.findViewById(R.id.tv_learn_time);
            holder.tv_uname = (TextView) convertView.findViewById(R.id.tv_uname);
            holder.btn_review = (Button) convertView.findViewById(R.id.btn_review);
            holder.roundImageView = (RoundImageView) convertView.findViewById(R.id.iv_head);
            holder.mPager = (TaskChildViewPager) convertView.findViewById(R.id.pk_image_view_pager);
            holder.dot_containter = (LinearLayout) convertView.findViewById(R.id.pk_linear_indicator);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
            AutoUtils.autoSize(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        setItemData(holder, position);
        return convertView;
    }

    /**
     * 确认作业
     *
     * @param task_id 提交的作业ID
     */
    private void requestConfirmTask(String tag, String task_id) {
        TaskWorkConfirmParams taskWorkConfirmParams = new TaskWorkConfirmParams(task_id);
        RequestManager.request(mContext, tag, taskWorkConfirmParams,
                TaskWorkConfirmResponse.class, this,
                SPUtils.instance().getSocialPropEntity()
                        .getApp_school_server());
    }

    private void imageBrower(int position, String[] urls) {
        //友盟统计
        //        List<String> var = new ArrayList<String>();
        //        var.add(YouMengType.getName(MainActivity.TYPE_TWO));
        //        var.add("查看大图");
        //        if (getItem(position) instanceof ContentInfo && !TextUtils.isEmpty(((ContentInfo) getItem(position)).getTopic_id())) {
        //            YouMengType.onEvent(mContext, var, 1, "活动");
        //        } else {
        //            YouMengType.onEvent(mContext, var, 1, "广场");
        //        }
        Intent intent = new Intent(mContext, SquareImageLookActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_INDEX, position);
        mContext.startActivity(intent);
    }

    public class ViewHolder {
        private TaskChildViewPager mPager;
        private LinearLayout dot_containter;
        private RelativeLayout rel_images;
        private ImageView play_video;
        private TextView tv_content;
        private TextView tv_tips;
        private TextView tv_learn_time;
        private TextView tv_time;
        private RoundImageView roundImageView;
        private TextView tv_uname;
        private Button btn_review;
    }

    @Override
    public void onResponse(BaseResponse response) {

    }

    @Override
    public void onResponse(String tag, BaseResponse response) {
        if ("0".equals(response.getResCode())) {
            if (response instanceof TaskWorkConfirmResponse) {
                for (int i = 0; i < getCount(); i++) {
                    if (getData().get(i).get_id().equals(tag)) {
                        getData().get(i).setIs_confirm("ok");
                        ((TaskDetailActivity) mContext).updateConfirmNo();
                        break;
                    }
                }
                this.handler.obtainMessage(3, tag).sendToTarget();
            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
    }

    /**
     * 设置viewHolder的数据
     *
     * @param holder
     * @param itemIndex
     */
    private void setItemData(ViewHolder holder, final int itemIndex) {
        final TaskHomeWorkSubmitInfo taskHomeWorkInfo = getItem(itemIndex);
        //是否需要展示图片视频信息
        if (needShowImage) {
            holder.rel_images.setVisibility(View.VISIBLE);
            if (taskHomeWorkInfo.getImg_url() != null && taskHomeWorkInfo.getImg_url().length > 0) {
                if (taskHomeWorkInfo.getImg_url().length == 1) {
                    holder.dot_containter.setVisibility(View.GONE);
                    holder.mPager.setVisibility(View.VISIBLE);
                } else if (taskHomeWorkInfo.getImg_url().length > 1) {
                    holder.dot_containter.setVisibility(View.VISIBLE);
                    holder.mPager.setVisibility(View.VISIBLE);
                }
                TaskImageSlidePageAdapter mPagerAdapter = new TaskImageSlidePageAdapter(mContext,
                        taskHomeWorkInfo.getImg_url());
                holder.mPager.setAdapter(mPagerAdapter);
                // 初始化小圆点数组
                final ImageView[] indicator_imgs = initIndicator(holder.dot_containter, taskHomeWorkInfo.getImg_url().length);
                holder.mPager.setCurrentItem(taskHomeWorkInfo.getImageSelectIndex());
                setIndicatorSelected(indicator_imgs, taskHomeWorkInfo.getImageSelectIndex());
                holder.mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                    @Override
                    public void onPageSelected(int arg0) {
                        LogUtils.e("选中第几个" + arg0 + "");
                        setIndicatorSelected(indicator_imgs, arg0);
                        taskHomeWorkInfo.setImageSelectIndex(arg0);
                    }

                    @Override
                    public void onPageScrolled(int arg0, float arg1, int arg2) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int arg0) {

                    }
                });
            }

        } else {
            holder.rel_images.setVisibility(View.GONE);
        }
//        if (!TextUtils.isEmpty(taskHomeWorkInfo.getTitle())) {
//            holder.tv_content.setVisibility(View.VISIBLE);
//            holder.tv_content.setText(taskHomeWorkInfo.getTitle());
//        } else {
//            holder.tv_content.setVisibility(View.GONE);
//        }
        holder.roundImageView.setImageView(StringUtil.getHeadUrl(taskHomeWorkInfo.getHead_img()));
        holder.roundImageView.setTag(taskHomeWorkInfo.getUser_id());
        holder.roundImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toUserDetail((String) view.getTag());
            }
        });
        holder.tv_time.setText(DateKit.timeFormat(taskHomeWorkInfo.getCtime()));
        holder.tv_uname.setText(taskHomeWorkInfo.getUname());
        if (isTeacher) {
            holder.btn_review.setVisibility(View.VISIBLE);
            if (TextUtils.equals("ok", taskHomeWorkInfo.getIs_confirm())) {
                holder.btn_review.setText("已审阅");
                holder.btn_review.setEnabled(false);
            } else {
                holder.btn_review.setText("审阅");
                holder.btn_review.setEnabled(true);
                holder.btn_review.setTag(taskHomeWorkInfo.get_id());
                holder.btn_review.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String task_submit_id = (String) view.getTag();
                        requestConfirmTask(task_submit_id, task_submit_id);
                    }
                });
            }

        } else {
            holder.btn_review.setVisibility(View.GONE);
        }
    }

    private synchronized void setIndicatorSelected(ImageView[] indicator_imgs, int selectedndex) {
        if (selectedndex < indicator_imgs.length) {
            for (int i = 0; i < indicator_imgs.length; i++) { // null异常
                indicator_imgs[i].setImageResource(R.drawable.dot_white);
            }
            if (selectedndex < indicator_imgs.length)
                // 改变当前背景图片为：选中
                indicator_imgs[selectedndex].setImageResource(R.drawable.dot_red);
        }
    }

    private ImageView[] initIndicator(LinearLayout pDotview_contair, int size) {
        ImageView imgView;
        pDotview_contair.removeAllViews();
        ImageView[] indicator_imgs = new ImageView[size];
        for (int i = 0; i < size; i++) {
            imgView = new ImageView(mContext);
            LinearLayout.LayoutParams params_linear = new LinearLayout.LayoutParams(20, 20); // 设置原点大小
            params_linear.setMargins(7, 10, 7, 10);
            imgView.setLayoutParams(params_linear);
            try {
                indicator_imgs[i] = imgView;
            } catch (Exception e) {
                return null;
            }
            if (i == 0) { // 初始化第一个为选中状态
                indicator_imgs[i].setImageResource(R.drawable.dot_red);  //setBackgroundResource
            } else {
                indicator_imgs[i].setImageResource(R.drawable.dot_white);
            }
            pDotview_contair.addView(indicator_imgs[i]);
        }
        return indicator_imgs;
    }

    /**
     * 查看用户详情
     */
    public void toUserDetail(String userId) {
        Intent intent = new Intent(mContext, UserInfoActivity.class);
        intent.putExtra("user_id", userId);
        mContext.startActivity(intent);
    }

    public void updateItemView(View view, int itemIndex) {
        if (view == null) {
            return;
        }
        //从view中取得holder
        ViewHolder holder = (ViewHolder) view.getTag();
        if (isTeacher) {
            holder.btn_review.setVisibility(View.VISIBLE);
            if (TextUtils.equals("ok", getItem(itemIndex).getIs_confirm())) {
                holder.btn_review.setText("已审阅");
                holder.btn_review.setEnabled(false);
            } else {
                holder.btn_review.setText("审阅");
                holder.btn_review.setEnabled(true);
                holder.btn_review.setTag(getItem(itemIndex).getTask_id());
                holder.btn_review.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String task_submit_id = (String) view.getTag();
                        requestConfirmTask(task_submit_id, task_submit_id);
                    }
                });
            }

        } else {
            holder.btn_review.setVisibility(View.GONE);
        }
    }
}
