package com.easier.writepre.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.entity.ContentInfo;
import com.easier.writepre.entity.TaskHomeWorkInfo;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.Constant;
import com.easier.writepre.ui.ActiveDetailActivity;
import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.ui.SquareImageLookActivity;
import com.easier.writepre.ui.TaskDetailActivity;
import com.easier.writepre.ui.TopicDetailActivity;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.widget.ChildViewPager;
import com.easier.writepre.widget.RoundImageView;
import com.easier.writepre.widget.SquareAllEssenceGridView;
import com.easier.writepre.widget.TaskChildViewPager;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.AutoUtils;
import com.sj.autolayout.utils.DateKit;

import java.util.ArrayList;
import java.util.List;

/**
 * 书法师作业适配器
 */
public class TaskHomeWorkAdapter extends BaseAdapter {

    private Context mContext;

    private List<TaskHomeWorkInfo> list = new ArrayList<TaskHomeWorkInfo>();

    public TaskHomeWorkAdapter(Context context) {
        mContext = context;
    }

    private boolean needShowImage;

    public void setNeedShowImage(boolean needShowImage) {
        this.needShowImage = needShowImage;
    }

    private boolean isTeacher;

    public boolean isTeacher() {
        return isTeacher;
    }

    public void setTeacher(boolean teacher) {
        isTeacher = teacher;
    }

    public void setData(List<TaskHomeWorkInfo> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(List<TaskHomeWorkInfo> data) {
        list.addAll(data);
        notifyDataSetChanged();
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

    public void updateData(TaskHomeWorkInfo taskHomeWorkInfo) {
        for (int i = 0; i < list.size(); i++) {
            if (TextUtils.equals(list.get(i).get_id(), taskHomeWorkInfo.get_id())) {
                list.set(i, taskHomeWorkInfo);
                break;
            }
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public TaskHomeWorkInfo getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_task, parent, false);
            holder.rel_images = (RelativeLayout) convertView.findViewById(R.id.image_layout);
            holder.iv_taskwork = (SimpleDraweeView) convertView.findViewById(R.id.iv_taskwork);
            holder.play_video = (ImageView) convertView.findViewById(R.id.play_video);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tv_tips = (TextView) convertView.findViewById(R.id.tv_tips);
            holder.tv_learn_time = (TextView) convertView.findViewById(R.id.tv_learn_time);
            holder.tv_uname = (TextView) convertView.findViewById(R.id.tv_uname);
            holder.tv_comment_num = (TextView) convertView.findViewById(R.id.tv_comment_num);
            holder.roundImageView = (RoundImageView) convertView.findViewById(R.id.iv_head);
            holder.tv_noconfirm = (TextView) convertView.findViewById(R.id.tv_noconfirm);
            holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            holder.mPager = (TaskChildViewPager) convertView.findViewById(R.id.pk_image_view_pager);
            holder.dot_containter = (LinearLayout) convertView.findViewById(R.id.pk_linear_indicator);

            convertView.setTag(holder);
            AutoUtils.autoSize(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final TaskHomeWorkInfo taskHomeWorkInfo = getItem(position);

        //是否需要展示图片视频信息
        if (needShowImage) {
            holder.rel_images.setVisibility(View.VISIBLE);
            if (taskHomeWorkInfo.getImg_url() != null && taskHomeWorkInfo.getImg_url().length > 0) {
                holder.iv_taskwork.setVisibility(View.VISIBLE);
                holder.iv_taskwork.setImageURI(Uri.parse(TextUtils.isEmpty(StringUtil.getImgeUrl(taskHomeWorkInfo.getImg_url()[0])) ? "" : StringUtil.getImgeUrl(taskHomeWorkInfo.getImg_url()[0])));
//                if (taskHomeWorkInfo.getImg_url().length == 1) {
//                    holder.dot_containter.setVisibility(View.GONE);
//                    holder.mPager.setVisibility(View.VISIBLE);
//                } else if (taskHomeWorkInfo.getImg_url().length > 1) {
//                    holder.dot_containter.setVisibility(View.VISIBLE);
//                    holder.mPager.setVisibility(View.VISIBLE);
//                }
//                TaskImageSlidePageAdapter mPagerAdapter = new TaskImageSlidePageAdapter(mContext,
//                        taskHomeWorkInfo.getImg_url());
//                holder.mPager.setAdapter(mPagerAdapter);
//                // 初始化小圆点数组
//                final ImageView[] indicator_imgs = initIndicator(holder.dot_containter, taskHomeWorkInfo.getImg_url().length);
//                holder.mPager.setCurrentItem(taskHomeWorkInfo.getImageSelectIndex());
//                holder.mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//
//                    @Override
//                    public void onPageSelected(int arg0) {
//                        LogUtils.e("选中第几个" + arg0 + "");
//                        setIndicatorSelected(indicator_imgs, arg0);
//                        taskHomeWorkInfo.setImageSelectIndex(arg0);
//                    }
//
//                    @Override
//                    public void onPageScrolled(int arg0, float arg1, int arg2) {
//
//                    }
//
//                    @Override
//                    public void onPageScrollStateChanged(int arg0) {
//
//                    }
//                });
            } else {
                holder.rel_images.setVisibility(View.GONE);
                holder.iv_taskwork.setVisibility(View.GONE);
            }

        } else {
            holder.rel_images.setVisibility(View.GONE);
            holder.iv_taskwork.setVisibility(View.GONE);
        }
        if (isTeacher()) {
            holder.tv_noconfirm.setVisibility(View.VISIBLE);
            int noConfirm = Integer.valueOf(taskHomeWorkInfo.getSubmit_cnt()) - Integer.valueOf(taskHomeWorkInfo.getConfirm_cnt());
            if (noConfirm == 0) {
                holder.tv_noconfirm.setVisibility(View.INVISIBLE);
            } else {
                holder.tv_noconfirm.setText(noConfirm + "次作业待查看");
            }
        } else {
            holder.tv_noconfirm.setVisibility(View.INVISIBLE);
        }
        holder.roundImageView.setImageView(StringUtil.getHeadUrl(taskHomeWorkInfo.getHead_img()));
        holder.tv_uname.setText(taskHomeWorkInfo.getUname());
        holder.tv_content.setText(taskHomeWorkInfo.getTitle());
        holder.tv_comment_num.setText(taskHomeWorkInfo.getSubmit_cnt());
        holder.tv_date.setText(DateKit.timeFormat(taskHomeWorkInfo.getCtime()));
        return convertView;
    }

    private void setIndicatorSelected(ImageView[] indicator_imgs, int selectedndex) {
        for (int i = 0; i < indicator_imgs.length; i++) { // null异常
            indicator_imgs[i].setImageResource(R.drawable.dot_white);
        }
        // 改变当前背景图片为：选中
        indicator_imgs[selectedndex].setImageResource(R.drawable.dot_red);
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

    public void updateItemView(View view, TaskHomeWorkInfo taskHomeWorkInfo) {
        if (view == null) {
            return;
        }
        //从view中取得holder
        TaskHomeWorkAdapter.ViewHolder holder = (TaskHomeWorkAdapter.ViewHolder) view.getTag();
        if (isTeacher()) {
            holder.tv_noconfirm.setVisibility(View.VISIBLE);
            int noConfirm = Integer.valueOf(taskHomeWorkInfo.getSubmit_cnt()) - Integer.valueOf(taskHomeWorkInfo.getConfirm_cnt());
            if (noConfirm <= 0) {
                holder.tv_noconfirm.setVisibility(View.INVISIBLE);
            } else {
                holder.tv_noconfirm.setText(noConfirm + "次作业待查看");
            }
        } else {
            holder.tv_noconfirm.setVisibility(View.INVISIBLE);
        }
    }


    public class ViewHolder {
        private TaskChildViewPager mPager;
        private LinearLayout dot_containter;
        private RelativeLayout rel_images;
        private ImageView play_video;
        private SimpleDraweeView iv_taskwork;
        private TextView tv_content;
        private TextView tv_tips;
        private TextView tv_learn_time;
        private RoundImageView roundImageView;
        private TextView tv_uname;
        private TextView tv_comment_num;
        private TextView tv_noconfirm;
        private TextView tv_date;
    }
}
