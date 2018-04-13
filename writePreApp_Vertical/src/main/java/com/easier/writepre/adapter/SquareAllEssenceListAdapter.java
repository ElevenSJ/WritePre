package com.easier.writepre.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.entity.ActiveInfo;
import com.easier.writepre.entity.ContentBase;
import com.easier.writepre.entity.ContentInfo;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.http.WritePreListener;
import com.easier.writepre.param.GiveOkParams;
import com.easier.writepre.param.SquarePostDelParams;
import com.easier.writepre.param.SquareReadParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.GiveOkResponse;
import com.easier.writepre.response.SquarePostDelResponse;
import com.easier.writepre.ui.ActiveDetailActivity;
import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.ui.MediaActivity;
import com.easier.writepre.ui.NoSwipeBackActivity;
import com.easier.writepre.ui.SquareImageLookActivity;
import com.easier.writepre.ui.TopicDetailActivity;
import com.easier.writepre.ui.UserInfoActivity;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.NineGridView;
import com.easier.writepre.widget.NineGridViewAdapter;
import com.easier.writepre.widget.RoundImageView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sj.autolayout.utils.AutoUtils;
import com.sj.autolayout.utils.DateKit;

import java.util.ArrayList;
import java.util.List;

/**
 * 广场（全部精华填充内容适配器）
 * @author sunjie
 */
@SuppressLint("InflateParams")
public class SquareAllEssenceListAdapter extends BaseAdapter implements WritePreListener<BaseResponse> {

    private Context mContext;
    private ListView listView;

    private List<ContentBase> list = new ArrayList<ContentBase>();

    private boolean needShow = true;//是否需要展示活动名称 true 展示 false 不展示

    public boolean isNeedShow() {
        return needShow;
    }

    public void setNeedShow(boolean needShow) {
        this.needShow = needShow;
    }

    public SquareAllEssenceListAdapter(Context context, ListView listView) {
        mContext = context;
        this.listView = listView;
    }

    public synchronized void mergeData(final List<ContentInfo> data1, final List<ActiveInfo> data2) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                list.clear();
                if (data1 != null && data1.size() > 0) {
                    list.addAll(data1);
                    if (data2!=null) {
                        for (int i = 0; i < data2.size(); i++) {
                            if ((i * 2 + 2) <= list.size() - 1) {
                                list.add(i * 2 + 3, data2.get(i));
                            } else {
                                list.add(data2.get(i));
                            }
                        }
                    }
                } else {
                    if (data2 != null && data2.size() > 0) {
                        list.addAll(data2);
                    }
                }
                ((Activity) mContext).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    public List<ContentBase> getData() {
        return list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }


    @Override
    public ContentBase getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private int index = -1;

    public int getSelectedIndex() {
        return index;
    }

    public void setSelectedIndex(int position) {
        index = position;
    }

    public void remove(int index) {
        if (index < 0 || index >= getCount()) {
            return;
        }
        list.remove(index);
        notifyDataSetChanged();
    }

    public void replace(int index, ContentBase obj) {
        if (index < 0 || index >= getCount()) {
            return;
        }
        list.set(index, obj);
        updateSingleRow(obj.get_id());
    }


    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof ContentInfo) {
            return 0;
        } else if (getItem(position) instanceof ActiveInfo) {
            return 1;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final ActiveViewHolder activeHolder;
        int type = getItemViewType(position);
        switch (type) {
            case 0:
                if (convertView == null) {
                    holder = new ViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.square_all_essence_item, parent, false);
                    holder.square_iv_head = (SimpleDraweeView) convertView.findViewById(R.id.square_iv_head);
                    holder.img_teacher = (ImageView) convertView.findViewById(R.id.teacher_icon);
                    holder.square_iv_share = (ImageView) convertView.findViewById(R.id.square_iv_share);
                    holder.gridView = (NineGridView) convertView.findViewById(R.id.gridview);
                    holder.tv_uname = (TextView) convertView.findViewById(R.id.tv_uname);
                    holder.tv_ctime = (TextView) convertView.findViewById(R.id.tv_ctime);
                    holder.tv_city = (TextView) convertView.findViewById(R.id.tv_city);
                    holder.tv_title2 = (TextView) convertView.findViewById(R.id.tv_title2);
                    holder.tv_ok_num = (TextView) convertView.findViewById(R.id.tv_ok_num);
                    holder.tv_remark_num = (TextView) convertView.findViewById(R.id.tv_remark_num);
                    holder.tv_delete = (TextView) convertView.findViewById(R.id.tv_delete);
                    holder.tv_readCount = (TextView) convertView.findViewById(R.id.tv_readCount);
                    holder.setOnclick(holder.square_iv_head);
                    holder.setOnclick(holder.square_iv_share);
                    holder.setOnclick(holder.tv_uname);
                    holder.setOnclick(holder.tv_ok_num);
                    holder.setOnclick(holder.tv_remark_num);
                    holder.setOnclick(holder.tv_delete);
                    convertView.setTag(holder);
                    AutoUtils.autoSize(convertView);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                final ContentInfo contentInfo = (ContentInfo) getItem(position);
                holder.setPosition(position);

                holder.tv_uname.setText(TextUtils.isEmpty(contentInfo.getUname())?"_"+contentInfo.getUser_id():contentInfo.getUname()); // 昵称
                holder.tv_ctime.setText(DateKit.timeFormat(contentInfo.getCtime())); // 时间
                holder.tv_city.setVisibility(TextUtils.isEmpty(contentInfo.getCity()) ? View.GONE : View.VISIBLE);
                holder.tv_city.setText(contentInfo.getCity().replaceAll("\\s*", "")); // 城市

                if (TextUtils.isEmpty(contentInfo.getTitle())) {
                    if (isNeedShow() && !TextUtils.isEmpty(contentInfo.getTopic_title())) {
                        holder.tv_title2.setVisibility(View.VISIBLE);
                    } else {
                        holder.tv_title2.setVisibility(View.GONE);
                    }
                } else {
                    holder.tv_title2.setVisibility(View.VISIBLE);
                }
                if (isNeedShow() && !TextUtils.isEmpty(contentInfo.getTopic_title())) {
                    String contentStr = "#" + contentInfo.getTopic_title().trim() + "#" + " "
                            + (TextUtils.isEmpty(contentInfo.getTitle()) ? "" : contentInfo.getTitle().trim());
                    SpannableString spannableString = new SpannableString(contentStr);
                    spannableString.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            if (mContext instanceof ActiveDetailActivity) {
                                return;
                            }
                            // 增加阅读数
                            holder.tv_readCount.setTag(position);
                            requestReadAdd(contentInfo);
                            Intent intent = new Intent(mContext, ActiveDetailActivity.class);
                            intent.putExtra("id", contentInfo.getTopic_id());
                            mContext.startActivity(intent);
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setUnderlineText(false);
                            ds.clearShadowLayer();
                        }
                    }, 0, contentInfo.getTopic_title().trim().length() + 2, 0);
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#14A4BF")), 0,
                            contentInfo.getTopic_title().trim().length() + 2, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

                    holder.tv_title2.setText(spannableString);
                    holder.tv_title2.setMovementMethod(LinkMovementMethod.getInstance());
                } else {
                    holder.tv_title2.setText(contentInfo.getTitle().trim());
                }
                holder.tv_ok_num.setText("" + contentInfo.getOk_num()); // 赞数
                holder.tv_remark_num.setText("" + contentInfo.getRemark_num());// 评论数
                holder.tv_readCount.setText("" + contentInfo.getView_num());

                holder.tv_ok_num.setSelected(!TextUtils.isEmpty(contentInfo.getIs_ok()) && contentInfo.getIs_ok().equals("1"));

                // 删除帖子
                holder.tv_delete.setVisibility(SPUtils.instance().getLoginEntity().get_id().equals(contentInfo.getUser_id()) ? View.VISIBLE : View.GONE);
                loadImages(contentInfo, holder, position);
                break;
            case 1://活动
                if (convertView == null) {
                    activeHolder = new ActiveViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.active_banner_layout, parent, false);
                    activeHolder.activeIcon = (SimpleDraweeView) convertView.findViewById(R.id.active_banner);
                    //动态设置banner高度
                    LinearLayout.LayoutParams headerLayoutParams = new LinearLayout.LayoutParams(WritePreApp.getApp().getWidth(1f), WritePreApp.getApp().getWidth(0.2126f));
                    activeHolder.activeIcon.setLayoutParams(headerLayoutParams);
                    activeHolder.descTxt = (TextView) convertView.findViewById(R.id.desc);
                    activeHolder.titleTxt = (TextView) convertView.findViewById(R.id.title);
                    convertView.setTag(activeHolder);
                    AutoUtils.autoSize(convertView);
                } else {
                    activeHolder = (ActiveViewHolder) convertView.getTag();
                }
                final ActiveInfo active = (ActiveInfo) getItem(position);
                loadImages(active, activeHolder, position);
                activeHolder.titleTxt.setText(active.getTitle());
                activeHolder.descTxt.setText(active.getDesc());
                break;
        }
        return convertView;
    }

    /**
     * 播放视频
     *
     * @param vod_url
     */
    private void toPlayVideo(int position, String vod_url) {
        //友盟统计
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.TYPE_TWO));
        var.add("查看小视频");
        if (getItem(position) instanceof ContentInfo && !TextUtils.isEmpty(((ContentInfo) getItem(position)).getTopic_id())) {
            YouMengType.onEvent(mContext, var, 1, "活动");
        } else {
            YouMengType.onEvent(mContext, var, 1, "广场");
        }
        Intent intent = new Intent(mContext, MediaActivity.class);
        intent.putExtra(MediaActivity.URL, StringUtil.getImgeUrl(vod_url));
        mContext.startActivity(intent);
    }

    private void imageBrower(int position, String[] urls) {
        //友盟统计
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.TYPE_TWO));
        var.add("查看大图");
        if (getItem(position) instanceof ContentInfo && !TextUtils.isEmpty(((ContentInfo) getItem(position)).getTopic_id())) {
            YouMengType.onEvent(mContext, var, 1, "活动");
        } else {
            YouMengType.onEvent(mContext, var, 1, "广场");
        }
        Intent intent = new Intent(mContext, SquareImageLookActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_INDEX, position);
        mContext.startActivity(intent);
    }

    public static class BaseViewHolder {

    }

    public class ViewHolder extends BaseViewHolder {
        private int position;
        OnViewClickListener listener;
        private NineGridView gridView;
        private ImageView img_teacher,square_iv_share;
        public TextView tv_uname, tv_ctime, tv_city, tv_title2, tv_ok_num, tv_remark_num, tv_delete, tv_readCount;

        private SimpleDraweeView square_iv_head;

        ViewHolder() {
            listener = new OnViewClickListener();
        }

        public void setOnclick(View view) {
            view.setOnClickListener(listener);
        }

        public void setPosition(int position) {
            this.position = position;
        }

        private class OnViewClickListener implements OnClickListener {
            @Override
            public void onClick(View v) {
                index = position;
                final ContentInfo contentInfo = (ContentInfo) getItem(position);
                switch (v.getId()) {
                    case R.id.square_iv_head:
                    case R.id.tv_uname:
                        toUserDetail(contentInfo.getUser_id());
                        break;
                    case R.id.square_iv_share:
                        requestShare((View) v.getTag(), contentInfo.get_id(), contentInfo.getTitle());
                        break;
                    case R.id.tv_ok_num:
                        if (LoginUtil.checkLogin(mContext)) {
                            if (contentInfo.getIs_ok().equals("1")) {
                                contentInfo.setOk_num(contentInfo.getOk_num() - 1);
                                v.setSelected(false);
                                contentInfo.setIs_ok("0");
                            } else {
                                //友盟统计
                                List<String> var = new ArrayList<String>();
                                var.add(YouMengType.getName(MainActivity.TYPE_TWO));
                                var.add("点赞");
                                if (!TextUtils.isEmpty(contentInfo.getTopic_title())) {
                                    YouMengType.onEvent(mContext, var, 1, "活动");
                                } else {
                                    YouMengType.onEvent(mContext, var, 1, "广场");
                                }
                                v.setSelected(true);
                                contentInfo.setIs_ok("1");
                                contentInfo.setOk_num(contentInfo.getOk_num() + 1);
                            }
                            ((TextView) v).setText("" + contentInfo.getOk_num()); // 赞数
                            requestGiveOk(contentInfo.get_id());
                        }
                        break;
                    case R.id.tv_remark_num:
                        inTopicDetail(position, contentInfo);
                        break;
                    case R.id.tv_delete:
                        showDelDialog(mContext, contentInfo.get_id());
                        break;
                }

            }

        }
    }

    public static class ActiveViewHolder extends BaseViewHolder {
        private SimpleDraweeView activeIcon;
        private TextView descTxt;
        private TextView titleTxt;
    }

    private void toUserDetail(String user_id) {
        Intent intent = new Intent(mContext, UserInfoActivity.class);
        intent.putExtra("user_id", user_id);
        mContext.startActivity(intent);
    }

    public void requestShare(View view, String id, String txt) {
        ((NoSwipeBackActivity) mContext).sharedContentCustomize("#广场帖子#", txt,
                SPUtils.instance().getSocialPropEntity().getShare_baseurl() + Constant.SHARE_SQUARE_PATH + id,
                SPUtils.instance().getSocialPropEntity().getShare_baseurl() + Constant.SHARE_SQUARE_PATH + id, view);
    }

    /**
     * 广场贴增加阅读数请求
     *
     * @param contentInfo
     */
    public synchronized void requestReadAdd(ContentInfo contentInfo) {
        contentInfo.setView_num(contentInfo.getView_num() + 1);
        updateSingleRow(contentInfo.get_id());
        RequestManager.request(mContext, new SquareReadParams(contentInfo.get_id()), BaseResponse.class,
                new WritePreListener<BaseResponse>() {

                    @Override
                    public void onResponse(BaseResponse response) {
                        if ("0".equals(response.getResCode())) {

                        } else {
                            ToastUtil.show(response.getResMsg());
                        }
                    }

                    @Override
                    public void onResponse(String tag, BaseResponse response) {
                        // TODO Auto-generated method stub

                    }

                }, SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    /**
     * 赞接口请求
     */
    public void requestGiveOk(String post_id) {
        RequestManager.request(mContext, new GiveOkParams(post_id), GiveOkResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    /**
     * 赞、删除接口返回回调（暂不做处理、也可以不用实现该接口，暂时保留）
     */
    @Override
    public void onResponse(BaseResponse response) {
        if ("0".equals(response.getResCode())) {
            if (response instanceof SquarePostDelResponse) {

            } else if (response instanceof GiveOkResponse) {

            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
    }

    /**
     * 删帖接口请求
     */
    public void requestDelPost(String post_id) {
        RequestManager.request(mContext, new SquarePostDelParams(post_id), SquarePostDelResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    /**
     * 跳轉進入帖子詳情
     */
    public void inTopicDetail(int position, ContentInfo contentInfo) {
        index = position;
        Intent intent = new Intent(mContext, TopicDetailActivity.class);
        intent.putExtra(TopicDetailActivity.IS_SHOW_TOPIC, mContext instanceof ActiveDetailActivity);
        intent.putExtra("data", contentInfo);
        ((Activity) mContext).startActivityForResult(intent, TopicDetailActivity.DETAIL_CODE);
    }

    public void showDelDialog(Context context, final String post_id) {

        final Dialog dialog = new Dialog(context, R.style.loading_dialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_islogin, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_login_now);
        tv.setText("确定是否删除?");
        TextView confirm = (TextView) view.findViewById(R.id.tv_login);
        confirm.setText("确定");
        confirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                remove(index);
                requestDelPost(post_id);
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

    @Override
    public void onResponse(String tag, BaseResponse response) {
        // TODO Auto-generated method stub

    }

    private void loadImages(final ContentBase contentBase, final BaseViewHolder baseHolder, final int position) {
        if (contentBase instanceof ContentInfo) {
            final ContentInfo contentInfo = (ContentInfo) contentBase;
            final ViewHolder holder = (ViewHolder) baseHolder;
            holder.square_iv_head.setImageURI(StringUtil.getHeadUrl(contentInfo.getHead_img()));
            holder.img_teacher.setVisibility(TextUtils.equals("1",contentInfo.getIs_teacher())?View.VISIBLE:View.GONE);
            ArrayList<String> imageInfo = new ArrayList<>();
            for (int i = 0; i < contentInfo.getImg_url().length; i++) {
                imageInfo.add(StringUtil.getImgeUrl(contentInfo.getImg_url()[i]) + Constant.SMALL_IMAGE_SUFFIX);
            }
            NineGridViewClickAdapter adapter;
            if (holder.gridView.getTag(holder.gridView.getId()) != null) {
                adapter = (NineGridViewClickAdapter) holder.gridView.getTag(holder.gridView.getId());
                adapter.setImageInfoList(imageInfo);
            } else {
                adapter = new NineGridViewClickAdapter(mContext, imageInfo);
                holder.gridView.setTag(holder.gridView.getId(), adapter);
            }
            holder.gridView.setAdapter(adapter,!TextUtils.isEmpty(contentInfo.getVod_url()));
            holder.gridView.setTag(position);
            holder.square_iv_share.setTag(holder.gridView.getImageView(0));
        } else {
            ActiveInfo active = (ActiveInfo) contentBase;
            final ActiveViewHolder activeHolder = (ActiveViewHolder) baseHolder;
            activeHolder.activeIcon.setImageURI(StringUtil.getImgeUrl(active.getImg_url()));
        }
    }

    /**
     * @param id
     */

    private void updateSingleRow(String id) {
        if (listView != null) {
            int start = listView.getFirstVisiblePosition();
            for (int i = start, j = listView.getLastVisiblePosition(); i <= j; i++) {
                if (listView.getItemAtPosition(i) != null) {
                    View view = listView.getChildAt(i - start);
                    if (id.equals(((ContentBase) listView.getItemAtPosition(i)).get_id())) {
                        if (view.getTag() instanceof ViewHolder) {
                            ContentInfo contentInfo = (ContentInfo) listView.getItemAtPosition(i);
                            ViewHolder holder = (ViewHolder) view.getTag();
                            holder.tv_ok_num.setText("" + contentInfo.getOk_num()); // 赞数
                            holder.tv_remark_num.setText("" + contentInfo.getRemark_num());// 评论数
                            holder.tv_ok_num.setSelected(!TextUtils.isEmpty(contentInfo.getIs_ok()) && contentInfo.getIs_ok().equals("1"));
                            holder.tv_readCount.setText("" + contentInfo.getView_num());
                        }
                        break;
                    }
                }
            }
        }
    }

    class NineGridViewClickAdapter extends NineGridViewAdapter {

        public NineGridViewClickAdapter(Context context, List<String> imageInfo) {
            super(context, imageInfo);
        }

        @Override
        protected void onImageItemClick(Context context, NineGridView nineGridView, int index, List imageInfo) {
            super.onImageItemClick(context, nineGridView, index, imageInfo);
            ContentInfo contentInfo = (ContentInfo) getItem((int) nineGridView.getTag());
            requestReadAdd(contentInfo);
            if (TextUtils.isEmpty(contentInfo.getVod_url())) {
                imageBrower(index, contentInfo.getImg_url());
            } else {
                toPlayVideo(index, contentInfo.getVod_url());
            }
        }

    }

}
