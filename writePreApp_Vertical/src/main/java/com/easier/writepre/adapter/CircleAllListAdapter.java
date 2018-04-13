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
import com.easier.writepre.entity.ActiveInfo;
import com.easier.writepre.entity.CircleBase;
import com.easier.writepre.entity.CircleInfo;
import com.easier.writepre.entity.CircleMsgInfo;
import com.easier.writepre.entity.ContentBase;
import com.easier.writepre.entity.ContentInfo;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.http.WritePreListener;
import com.easier.writepre.mainview.SocialMainView;
import com.easier.writepre.param.CircleGiveOkParams;
import com.easier.writepre.param.CirclePostDelParams;
import com.easier.writepre.param.CircleReadParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CirclePostDelResponse;
import com.easier.writepre.response.GiveOkResponse;
import com.easier.writepre.ui.CircleDetailActivity;
import com.easier.writepre.ui.CircleMsgDetailActivity;
import com.easier.writepre.ui.CircleMsgListActivity;
import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.ui.MediaActivity;
import com.easier.writepre.ui.NoSwipeBackActivity;
import com.easier.writepre.ui.SquareImageLookActivity;
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
 * 圈子（全部动态填充内容适配器）
 *
 * @author kai.zhong
 */
@SuppressLint("InflateParams")
public class CircleAllListAdapter extends BaseAdapter implements WritePreListener<BaseResponse> {

    private Context mContext;

    private ListView listView;

    private List<CircleMsgInfo> msgList = new ArrayList<CircleMsgInfo>();

    private List<CircleInfo> circleList = new ArrayList<CircleInfo>();

    private LayoutInflater mInflater;

    private int flag = 0;

    public CircleAllListAdapter(Context context, ListView listView) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        this.listView = listView;
    }

    public void setMsgData(int flag, List<CircleMsgInfo> data) {
        if (data == null) {
            return;
        }
        this.flag = flag;
        msgList.clear();
        msgList.addAll(data);
        notifyDataSetChanged();
    }

    public void setCircleData(int flag, List<CircleInfo> data) {
        if (data == null) {
            return;
        }
        this.flag = flag;
        circleList.clear();
        circleList.addAll(data);
        notifyDataSetChanged();
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public List<CircleMsgInfo> getMsgData() {
        return msgList;
    }

    public List<CircleInfo> getCircleData() {
        return circleList;
    }

    @Override
    public int getCount() {
        return flag == 0 ? msgList.size() : circleList.size();
    }

    @Override
    public CircleBase getItem(int position) {
        return flag == 0 ? msgList.get(position) : circleList.get(position);
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
        if (flag == 0) {
            msgList.remove(index);
        } else {
            circleList.remove(index);
        }
        notifyDataSetChanged();
    }

    public void replace(int index, Object obj) {
        if (index < 0 || index >= getCount()) {
            return;
        }
        if (flag == 0) {
            msgList.set(index, (CircleMsgInfo) obj);
        } else {
            circleList.set(index, (CircleInfo) obj);
        }
        updateSingleRow(((CircleBase) obj).get_id());

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MsgViewHolder holder;
        final CircleViewHolder circleHolder;
        int type = getItemViewType(position);
        switch (type) {
            case 0:
                if (convertView == null) {
                    holder = new MsgViewHolder();
                    convertView = mInflater.inflate(R.layout.square_all_essence_item, parent, false);
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
                    holder = (MsgViewHolder) convertView.getTag();
                }
                final CircleMsgInfo MsgInfo = (CircleMsgInfo) getItem(position);
                holder.setPosition(position);

                holder.tv_uname.setText(TextUtils.isEmpty(MsgInfo.getUname())?"_"+MsgInfo.getUser_id():MsgInfo.getUname()); // 昵称
                holder.tv_ctime.setText(DateKit.timeFormat(MsgInfo.getCtime())); // 时间
                holder.tv_city.setVisibility(TextUtils.isEmpty(MsgInfo.getCity()) ? View.GONE : View.VISIBLE);
                holder.tv_city.setText(MsgInfo.getCity().replaceAll("\\s*", "")); // 城市

                if (TextUtils.isEmpty(MsgInfo.getCircle_name()) && TextUtils.isEmpty(MsgInfo.getTitle())) {
                    holder.tv_title2.setVisibility(View.GONE);
                } else {
                    holder.tv_title2.setVisibility(View.VISIBLE);
                    if (TextUtils.isEmpty(MsgInfo.getCircle_name())) {
                        holder.tv_title2.setText(MsgInfo.getTitle().trim());
                    } else {
                        String contentStr = "#" + MsgInfo.getCircle_name().trim() + "#" + " "
                                + (TextUtils.isEmpty(MsgInfo.getTitle()) ? "" : MsgInfo.getTitle().trim());
                        SpannableString spannableString = new SpannableString(contentStr);

                        spannableString.setSpan(new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {
                                Intent intent = new Intent(mContext, CircleMsgListActivity.class);
                                intent.putExtra("circle_id", MsgInfo.getCircle_id()); // 圈子id
                                intent.putExtra("circle_name", MsgInfo.getCircle_name()); // 圈子名称
                                mContext.startActivity(intent);
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                /** set textColor **/
                                ds.setColor(ds.linkColor);
                                /** Remove the underline **/
                                ds.setUnderlineText(false);
                            }
                        }, 0, MsgInfo.getCircle_name().trim().length() + 2, 0);
                        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#14A4BF")), 0,
                                MsgInfo.getCircle_name().trim().length() + 2, 0);
                        holder.tv_title2.setText(spannableString);
                        holder.tv_title2.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                }
                holder.tv_ok_num.setText("" + MsgInfo.getOk_num()); // 赞数
                holder.tv_remark_num.setText("" + MsgInfo.getRemark_num());// 评论数
                holder.tv_readCount.setText("" + MsgInfo.getView_num());
                holder.tv_ok_num.setSelected(!TextUtils.isEmpty(MsgInfo.getIs_ok()) && MsgInfo.getIs_ok().equals("1"));

                // 删除帖子
                holder.tv_delete.setVisibility(SPUtils.instance().getLoginEntity().get_id().equals(MsgInfo.getUser_id()) ? View.VISIBLE : View.GONE);
                loadImages(MsgInfo, holder, position);
                break;
            case 1:
                if (convertView == null) {
                    circleHolder = new CircleViewHolder();
                    convertView = mInflater.inflate(R.layout.circle_of_my_item, parent, false);
                    circleHolder.ImgIcon = (SimpleDraweeView) convertView.findViewById(R.id.img_icon);
                    circleHolder.TvName = (TextView) convertView.findViewById(R.id.tv_name);
                    circleHolder.Tvtag = (TextView) convertView.findViewById(R.id.tv_tag);
                    circleHolder.Tvtype = (ImageView) convertView.findViewById(R.id.tv_type);
                    circleHolder.LayoutNotifyNum = (LinearLayout) convertView.findViewById(R.id.layout_notify_num);
                    circleHolder.TvNotifyNum = (TextView) convertView.findViewById(R.id.tv_notify_num);
                    circleHolder.TvNum = (TextView) convertView.findViewById(R.id.tv_num);
                    circleHolder.TvPostNum = (TextView) convertView.findViewById(R.id.tv_post_num);
                    circleHolder.TvIsOpen = (TextView) convertView.findViewById(R.id.tv_is_open);
                    convertView.setTag(circleHolder);
                    circleHolder.setOnclick(circleHolder.ImgIcon);
                    AutoUtils.autoSize(convertView);
                } else {
                    circleHolder = (CircleViewHolder) convertView.getTag();
                }
                circleHolder.setPosition(position);
                final CircleInfo circleInfo = (CircleInfo) getItem(position);
                circleHolder.ImgIcon.setImageURI(StringUtil.getImgeUrl(circleInfo.getFace_url()));

                if (circleInfo.getUser_id().equals(SPUtils.instance().getLoginEntity().get_id())) {
                    circleHolder.Tvtype.setVisibility(View.VISIBLE);
                } else {
                    circleHolder.Tvtype.setVisibility(View.GONE);
                }
                if (circleInfo.getType().equals("0")) {
                    circleHolder.Tvtag.setText("学习");
                } else if (circleInfo.getType().equals("2")) {
                    circleHolder.Tvtag.setText("班级");
                } else {
                    circleHolder.Tvtag.setText("交友");
                }
                if (circleInfo.getHas_req() == 0) {
                    circleHolder.LayoutNotifyNum.setVisibility(View.GONE);
                } else {
                    circleHolder.LayoutNotifyNum.setVisibility(View.VISIBLE);
                    circleHolder.TvNotifyNum.setText(circleInfo.getHas_req() + "");
                }
                circleHolder.TvName.setText(circleInfo.getName());
                circleHolder.TvNum.setText("(" + circleInfo.getNum() + "人)");
                circleHolder.TvPostNum.setText(circleInfo.getPost_num() + "");
                String openStr = circleInfo.getIs_open().equals("0") ? "私密" : "公开";
                circleHolder.TvIsOpen.setText(openStr);
                break;
            default:
                break;
        }

        return convertView;
    }

    private void loadImages(final CircleMsgInfo contentInfo, final MsgViewHolder holder, final int position) {
            holder.square_iv_head.setImageURI(StringUtil.getHeadUrl(contentInfo.getHead_img()));
            holder.img_teacher.setVisibility(TextUtils.equals("1", contentInfo.getIs_teacher()) ? View.VISIBLE : View.GONE);
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
            holder.gridView.setAdapter(adapter, !TextUtils.isEmpty(contentInfo.getVod_url()));
            holder.gridView.setTag(position);
            holder.square_iv_share.setTag(holder.gridView.getImageView(0));
    }

    @Override
    public int getItemViewType(int position) {
        return flag;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
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
        YouMengType.onEvent(mContext, var, 1, SocialMainView.CONTENT[SocialMainView.TAB_CIRCLE]);
        Intent intent = new Intent(mContext, MediaActivity.class);
        intent.putExtra(MediaActivity.URL, StringUtil.getImgeUrl(vod_url));
        mContext.startActivity(intent);
    }

    private void imageBrower(int position, String[] urls) {
        //友盟统计
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.TYPE_TWO));
        var.add("查看大图");
        YouMengType.onEvent(mContext, var, 1, SocialMainView.CONTENT[SocialMainView.TAB_CIRCLE]);

        Intent intent = new Intent(mContext, SquareImageLookActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_INDEX, position);
        mContext.startActivity(intent);
    }


    public class MsgViewHolder {
        private int position;
        OnViewClickListener listener;
        private NineGridView gridView;
        private ImageView img_teacher, square_iv_share;
        public TextView tv_uname, tv_ctime, tv_city, tv_title2, tv_ok_num, tv_remark_num, tv_delete, tv_readCount;

        private SimpleDraweeView square_iv_head;

        MsgViewHolder() {
            listener = new MsgViewHolder.OnViewClickListener();
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
                final CircleMsgInfo contentInfo = (CircleMsgInfo) getItem(position);
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
                                YouMengType.onEvent(mContext, var, 1, "圈子");
                                v.setSelected(true);
                                contentInfo.setIs_ok("1");
                                contentInfo.setOk_num(contentInfo.getOk_num() + 1);
                            }
                            ((TextView) v).setText("" + contentInfo.getOk_num()); // 赞数
                            requestGiveOk(contentInfo.getCircle_id(), contentInfo.get_id());
                        }
                        break;
                    case R.id.tv_remark_num:
                        inTopicDetail(position, contentInfo);
                        break;
                    case R.id.tv_delete:
                        showDelDialog(mContext, contentInfo.getCircle_id(), contentInfo.get_id());
                        break;
                }

            }

        }
    }

    public class CircleViewHolder  {
        private int position;
        OnViewClickListener listener;

        private SimpleDraweeView ImgIcon;
        private TextView TvName;
        private TextView Tvtag;
        private ImageView Tvtype;
        private TextView TvNum;
        private LinearLayout LayoutNotifyNum;
        private TextView TvNotifyNum;
        private TextView TvPostNum;
        private TextView TvIsOpen;


        CircleViewHolder() {
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
                final CircleInfo circleInfo = (CircleInfo) getItem(position);
                Intent intent = new Intent(mContext, CircleDetailActivity.class);
                        intent.putExtra("circle_id", circleInfo.get_id());
                mContext.startActivity(intent);
                }
            }
        }

    public void requestShare(View view, String id, String txt) {
        ((NoSwipeBackActivity) mContext).sharedContentCustomize("#圈子帖子#", txt,
                SPUtils.instance().getSocialPropEntity().getShare_baseurl() + Constant.SHARE_CIRCLE_PATH + id,
                SPUtils.instance().getSocialPropEntity().getShare_baseurl() + Constant.SHARE_CIRCLE_PATH + id, view);
    }

    /**
     * 圈子贴增加阅读数请求
     *
     * @param post_id
     */
    public synchronized void requestReadAdd( final String circle_id, final String post_id) {
        RequestManager.request(mContext, new CircleReadParams(circle_id, post_id), BaseResponse.class,
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
    public void requestGiveOk(String circle_id, String post_id) {
        RequestManager.request(mContext, new CircleGiveOkParams(circle_id, post_id), GiveOkResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    /**
     * 赞接口返回回调（暂不做处理、也可以不用实现该接口，暂时保留）
     */
    @Override
    public void onResponse(BaseResponse response) {
        if ("0".equals(response.getResCode())) {
            if (response instanceof CirclePostDelResponse) {

            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
    }

    /**
     * sgdet
     * 跳轉進入圈子动态詳情
     */
    public void inTopicDetail(int position, CircleMsgInfo MsgInfo) {
        index = position;
        Intent intent = new Intent(mContext, CircleMsgDetailActivity.class);
        intent.putExtra("data", MsgInfo);
        intent.putExtra(CircleMsgDetailActivity.ACTIVITY_TYPE, CircleMsgDetailActivity.MAIN_ACTIVITY);
        ((Activity) mContext).startActivityForResult(intent, CircleMsgDetailActivity.DETAIL_CODE);
    }

    /**
     * 查看用户详情
     */
    public void toUserDetail(String userId) {
        Intent intent = new Intent(mContext, UserInfoActivity.class);
        intent.putExtra("user_id", userId);
        mContext.startActivity(intent);
    }

    public void showDelDialog(Context context, final String circle_id, final String post_id) {

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
                msgList.remove(index);
                notifyDataSetChanged();
                requestDelPost(circle_id, post_id);
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
     * 删帖接口请求
     */
    public void requestDelPost(String circle_id, String post_id) {
        RequestManager.request(mContext, new CirclePostDelParams(circle_id, post_id), CirclePostDelResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    @Override
    public void onResponse(String tag, BaseResponse response) {
        // TODO Auto-generated method stub

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
                    if (id.equals(((CircleBase) listView.getItemAtPosition(i)).get_id())) {
                        if (view.getTag() instanceof MsgViewHolder) {
                            CircleMsgInfo MsgInfo = (CircleMsgInfo) listView.getItemAtPosition(i);
                            MsgViewHolder holder = (MsgViewHolder) view.getTag();
                            holder.tv_ok_num.setText("" + MsgInfo.getOk_num()); // 赞数
                            holder.tv_remark_num.setText("" + MsgInfo.getRemark_num());// 评论数
                            holder.tv_ok_num.setSelected(!TextUtils.isEmpty(MsgInfo.getIs_ok()) && MsgInfo.getIs_ok().equals("1"));
                            holder.tv_readCount.setText("" + MsgInfo.getView_num());
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
            CircleMsgInfo contentInfo = (CircleMsgInfo) getItem((int) nineGridView.getTag());
            requestReadAdd(contentInfo.getCircle_id(),contentInfo.get_id());
            if (TextUtils.isEmpty(contentInfo.getVod_url())) {
                imageBrower(index, contentInfo.getImg_url());
            } else {
                toPlayVideo(index, contentInfo.getVod_url());
            }
        }

    }
}
