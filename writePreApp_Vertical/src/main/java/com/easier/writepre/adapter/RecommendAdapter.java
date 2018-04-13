package com.easier.writepre.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.entity.RecommendInfo;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.http.WritePreListener;
import com.easier.writepre.param.AttentionParams;
import com.easier.writepre.param.UnAttentionParams;
import com.easier.writepre.response.AttentionResponse;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.UnAttentionResponse;
import com.easier.writepre.ui.ActiveDetailActivity;
import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.ui.SquareImageLookActivity;
import com.easier.writepre.ui.TopicDetailActivity;
import com.easier.writepre.ui.UserInfoActivity;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.RoundImageView;
import com.easier.writepre.widget.SquareAllEssenceGridView;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.AutoUtils;

import java.util.ArrayList;

/**
 * 搜索适配器
 */
public class RecommendAdapter extends BaseAdapter implements
        WritePreListener<BaseResponse> {
    private Context mCtx;
    private ArrayList<RecommendInfo> listRecommendData = new ArrayList<>();

    public RecommendAdapter(Context ctx) {
        this.mCtx = ctx;
    }


    public void setData(ArrayList<RecommendInfo> listData) {
        if (listData != null) {
            this.listRecommendData.clear();
            this.listRecommendData.addAll(listData);
            notifyDataSetChanged();
        }

    }

    public void clear() {
        if (listRecommendData != null) {
            listRecommendData.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return listRecommendData.size();
    }

    @Override
    public Object getItem(int position) {
        return listRecommendData.size() > 0 ? listRecommendData.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        //推荐布局
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mCtx).inflate(R.layout.recommend_item, parent, false);
            holder.iv_header_icon = (RoundImageView) convertView.findViewById(R.id.iv_img_head);
            holder.tv_uname = (TextView) convertView.findViewById(R.id.tv_uname);
            holder.tv_attention_num = (TextView) convertView.findViewById(R.id.tv_attention_num);
            holder.btn_attention = (Button) convertView.findViewById(R.id.btn_attention);
            holder.album_image = (ImageView) convertView.findViewById(R.id.album_image);
            holder.gridView = (SquareAllEssenceGridView) convertView.findViewById(R.id.gridview);
            convertView.setTag(holder);
            AutoUtils.autoSize(convertView);
            holder.gridView.setOnScrollListener(new PauseOnScrollListener(BitmapHelp.getBitmapUtils(), false, true));
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final RecommendInfo recommendInfo = (RecommendInfo) getItem(position);
        if (recommendInfo != null) {

            holder.tv_uname.setText(recommendInfo.getUname());
            holder.tv_attention_num.setText(recommendInfo.getCare_me_num());
            holder.iv_header_icon.setImageView(StringUtil.getHeadUrl(recommendInfo.getHead_img()));
            holder.iv_header_icon.setIconView(recommendInfo.getIs_teacher());
            holder.btn_attention.setTag(recommendInfo.getUser_id());
            if (TextUtils.equals("0", recommendInfo.getIsCared())) {
                //未关注
                holder.btn_attention.setText("关注TA");
            } else if (TextUtils.equals("1", recommendInfo.getIsCared())) {
                //已关注
                holder.btn_attention.setText("已关注");
                holder.btn_attention.setEnabled(false);
            }
            holder.btn_attention.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (LoginUtil.checkLogin(mCtx)) {
                        Button button = (Button) v;
                        String txt = button.getText().toString();
                        if (TextUtils.equals(txt, "取消关注")) {
                            // 调用取消关注接口
                            showDelDialog(mCtx, (String) button.getTag());
                        } else if (TextUtils.equals(txt, "关注TA")) {
                            // 调用关注接口
                            requestAttention((String) button.getTag());
                        }
                    }
                }
            });
            holder.iv_header_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mCtx, UserInfoActivity.class);
                    // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                    intent.putExtra("user_id", recommendInfo.getUser_id());
                    mCtx.startActivity(intent);
                }
            });
            if (recommendInfo.getImg_url() != null && recommendInfo.getImg_url().length > 0) {
                if (recommendInfo.getImg_url().length == 1) {
                    holder.album_image.setVisibility(View.VISIBLE);
                    holder.gridView.setVisibility(View.GONE);
                    BitmapHelp.getBitmapUtils().display(holder.album_image,
                            StringUtil.getImgeUrl(recommendInfo.getImg_url()[0]) + Constant.BIG_IMAGE_SUFFIX,
                            new BitmapLoadCallBack<View>() {

                                @Override
                                public void onLoadCompleted(View arg0, String arg1, Bitmap arg2, BitmapDisplayConfig arg3,
                                                            BitmapLoadFrom arg4) {
                                    holder.album_image.setImageBitmap(arg2);
                                }

                                @Override
                                public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
                                    holder.album_image.setImageResource(R.drawable.empty_photo);

                                }
                            });
                    holder.album_image.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            imageBrower(0, recommendInfo.getImg_url());
                        }
                    });
                } else {
                    holder.album_image.setVisibility(View.GONE);
                    holder.gridView.setVisibility(View.VISIBLE);
                    holder.gridView.setAdapter(
                            new SquareAllEssenceGridAdapter(holder.gridView,  mCtx,recommendInfo.getImg_url()));
                    holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                            imageBrower(i, recommendInfo.getImg_url());
                            // 增加阅读数
                        }
                    });

                    holder.gridView.setOnTouchInvalidPositionListener(new SquareAllEssenceGridView.OnTouchInvalidPositionListener() {
                        @Override
                        public boolean onTouchInvalidPosition(int motionEvent) {
                            inTopicDetail(recommendInfo);
                            return false; // 不终止路由事件让父级控件处理事件
                        }
                    });

                    if (recommendInfo.getImg_url().length > 2) {
                        holder.gridView.setNumColumns(3);
                        holder.gridView.setGravity(Gravity.LEFT);
                    } else if (recommendInfo.getImg_url().length == 2) {
                        holder.gridView.setNumColumns(2);
                        holder.gridView.setGravity(Gravity.LEFT);
                    } else {
                        holder.gridView.setNumColumns(1);
                        holder.gridView.setGravity(Gravity.LEFT);
                    }

                }
            } else {
                holder.gridView.setVisibility(View.GONE);
                holder.album_image.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    /**
     * 请求关注
     */
    private void requestAttention(String id) {
        LogUtils.e("关注请求id:" + id);
        AttentionParams parms = new AttentionParams(id);
        RequestManager.request(mCtx, id, parms, AttentionResponse.class,
                this, SPUtils.instance().getSocialPropEntity()
                        .getApp_socail_server());
    }

    /**
     * 请求取消关注
     */
    private void requestUnAttention(String id) {
        UnAttentionParams parms = new UnAttentionParams(id);
        RequestManager.request(mCtx, id, parms, UnAttentionResponse.class,
                this, SPUtils.instance().getSocialPropEntity()
                        .getApp_socail_server());
    }

    public void showDelDialog(Context context, final String id) {

        final Dialog dialog = new Dialog(context, R.style.loading_dialog);
        View view = LayoutInflater.from(context).inflate(
                R.layout.dialog_islogin, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_login_now);
        tv.setText("确定取消关注?");
        TextView confirm = (TextView) view.findViewById(R.id.tv_login);
        confirm.setText("确定");
        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                requestUnAttention(id);
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_cancel).setOnClickListener(
                new View.OnClickListener() {

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

    private void imageBrower(int position, String[] urls) {
        Intent intent = new Intent(mCtx, SquareImageLookActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_INDEX, position);
        mCtx.startActivity(intent);
    }

    /**
     * 跳轉進入帖子詳情
     */
    public void inTopicDetail(RecommendInfo recommendInfo) {

        Intent intent = new Intent(mCtx, TopicDetailActivity.class);
        intent.putExtra(TopicDetailActivity.IS_SHOW_TOPIC, mCtx instanceof ActiveDetailActivity);
        intent.putExtra("id", recommendInfo.getPost_id());
        mCtx.startActivity(intent);
    }

    @Override
    public void onResponse(BaseResponse response) {

    }

    @Override
    public void onResponse(String tag, BaseResponse response) {
        if ("0".equals(response.getResCode())) {
            if (response instanceof AttentionResponse) {
                // TODO 关注接口返回
                LogUtils.e("关注请求返回id:" + tag);
                updateData(tag, 1);

            } else if (response instanceof UnAttentionResponse) {
                // TODO 取消关注接口返回
                updateData(tag, 0);
            }
        } else {
            ToastUtil.show(response.getResMsg());
        }

    }

    /**
     * 更新数据
     *
     * @param userid
     * @param isCared 是否已经关注 1 关注  0 未关注
     */
    public synchronized void updateData(String userid, int isCared) {
        for (int i = 0; i < listRecommendData.size(); i++) {
            if (TextUtils.equals(listRecommendData.get(i).getUser_id(), userid)) {
                listRecommendData.get(i).setIsCared(isCared + "");
                break;
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 删除数据
     *
     * @param userid
     */
    public synchronized void removeData(String userid) {
        for (int i = 0; i < listRecommendData.size(); i++) {
            if (TextUtils.equals(listRecommendData.get(i).getUser_id(), userid)) {
                listRecommendData.remove(i);
                break;
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder {
        RoundImageView iv_header_icon;
        TextView tv_uname;
        TextView tv_attention_num;
        Button btn_attention;
        SquareAllEssenceGridView gridView;
        ImageView album_image;
    }
}
