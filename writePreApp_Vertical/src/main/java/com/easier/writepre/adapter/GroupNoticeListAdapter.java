package com.easier.writepre.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.entity.ActiveInfo;
import com.easier.writepre.entity.CircleDetail;
import com.easier.writepre.entity.ContentInfo;
import com.easier.writepre.entity.GroupNoticeInfo;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.http.WritePreListener;
import com.easier.writepre.mainview.SocialMainView;
import com.easier.writepre.param.GiveOkParams;
import com.easier.writepre.param.GroupNoticeAddPostParams;
import com.easier.writepre.param.GroupNoticePostDelParams;
import com.easier.writepre.param.SquarePostDelParams;
import com.easier.writepre.param.SquareReadParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CirclePostDelResponse;
import com.easier.writepre.response.GiveOkResponse;
import com.easier.writepre.response.GroupNoticeAddPostResponse;
import com.easier.writepre.response.GroupNoticePostDelResponse;
import com.easier.writepre.response.SquarePostDelResponse;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.ui.ActiveDetailActivity;
import com.easier.writepre.ui.GroupQianDaoMemberListActivity;
import com.easier.writepre.ui.MainActivity;
import com.easier.writepre.ui.MediaActivity;
import com.easier.writepre.ui.NoSwipeBackActivity;
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
import com.easier.writepre.widget.SquareAllEssenceGridView.OnTouchInvalidPositionListener;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.AutoUtils;
import com.sj.autolayout.utils.DateKit;

import java.util.ArrayList;
import java.util.List;

/**
 * 群公告适配器
 *
 * @author zhoulu
 */
@SuppressLint("InflateParams")
public class GroupNoticeListAdapter extends BaseAdapter implements WritePreListener<BaseResponse> {

    private Context mContext;
    private List<GroupNoticeInfo> data = new ArrayList<>();
    private CircleDetail mCircleBody;

    public GroupNoticeListAdapter(Context context) {
        mContext = context;
    }

    public void setCircleBody(CircleDetail mCircleBody) {
        this.mCircleBody = mCircleBody;
        notifyDataSetChanged();
    }

    public String getLastDataId() {
        if (data.isEmpty()) {
            return null;
        }
        return data.get(data.size() - 1).get_id();
    }

    public void setData(List<GroupNoticeInfo> data) {
        if (data == null) {
            return;
        }
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
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
        data.remove(index);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_groupnoticelist, parent, false);
            holder.square_iv_head = (RoundImageView) convertView.findViewById(R.id.square_iv_head);
            holder.layoutOtherInfo = (LinearLayout) convertView.findViewById(R.id.layout_other_info);
            holder.album_image = (ImageView) convertView.findViewById(R.id.album_image);
            holder.gridView = (SquareAllEssenceGridView) convertView.findViewById(R.id.gridview);
            holder.tv_uname = (TextView) convertView.findViewById(R.id.tv_uname);
            holder.tv_ctime = (TextView) convertView.findViewById(R.id.tv_ctime);
            holder.tv_title2 = (TextView) convertView.findViewById(R.id.tv_title2);
            holder.tv_delete = (TextView) convertView.findViewById(R.id.tv_delete);
            holder.tv_qiandaonum = (TextView) convertView.findViewById(R.id.tv_qiandaonum);
            holder.tv_qiandao_lookall = (TextView) convertView.findViewById(R.id.tv_qiandao_lookall);
            holder.btn_qiandao = (Button) convertView.findViewById(R.id.btn_qiandao);
            convertView.setTag(holder);
            AutoUtils.autoSize(convertView);
            holder.gridView.setOnScrollListener(new PauseOnScrollListener(BitmapHelp.getBitmapUtils(), false, true));
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final GroupNoticeInfo groupNoticeInfo = (GroupNoticeInfo) getItem(position);
        holder.square_iv_head.setImageView(StringUtil.getHeadUrl(groupNoticeInfo.getHead_img()));
        holder.tv_uname.setText(groupNoticeInfo.getUname()); // 昵称
        holder.tv_title2.setText(groupNoticeInfo.getTitle());
        holder.tv_qiandaonum.setText(groupNoticeInfo.getView_num());//签到数
        holder.tv_qiandao_lookall.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //查看全部签到成员
                Intent intent = new Intent(mContext, GroupQianDaoMemberListActivity.class);
                intent.putExtra("mTargetId", groupNoticeInfo.get_id());
                mContext.startActivity(intent);
            }
        });
        if (TextUtils.equals("1", groupNoticeInfo.getIs_viewed())) {
            holder.btn_qiandao.setText("已查看");
            holder.btn_qiandao.setEnabled(false);
        } else {
            holder.btn_qiandao.setText("确认");
            holder.btn_qiandao.setEnabled(true);
            holder.btn_qiandao.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestLookNotice(groupNoticeInfo.get_id(), groupNoticeInfo.get_id());
                }
            });
        }
        holder.tv_ctime.setText(DateKit.timeFormat(groupNoticeInfo.getCtime())); // 时间
        if (mCircleBody != null) {
            // 删除帖子
            if (TextUtils.equals(SPUtils.instance().getLoginEntity().get_id(), groupNoticeInfo.getUser_id()) || TextUtils.equals(mCircleBody.getRole(), "0")) {
                holder.tv_delete.setVisibility(View.VISIBLE);
            } else {

                holder.tv_delete.setVisibility(View.GONE);
            }
        } else {
            if (TextUtils.equals(SPUtils.instance().getLoginEntity().get_id(), groupNoticeInfo.getUser_id())) {
                holder.tv_delete.setVisibility(View.VISIBLE);
            } else {

                holder.tv_delete.setVisibility(View.GONE);
            }
        }
        holder.tv_delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                index = position;
                showDelDialog(mContext, groupNoticeInfo.getCircle_id(), groupNoticeInfo.get_id());
            }
        });
        if (groupNoticeInfo.getImg_url() != null && groupNoticeInfo.getImg_url().length > 0) {
            if (groupNoticeInfo.getImg_url().length == 1) {
                holder.album_image.setVisibility(View.VISIBLE);
                holder.gridView.setVisibility(View.GONE);
                BitmapHelp.getBitmapUtils().display(holder.album_image,
                        StringUtil.getImgeUrl(groupNoticeInfo.getImg_url()[0]) + Constant.BIG_IMAGE_SUFFIX,
                        new BitmapLoadCallBack<View>() {

                            @Override
                            public void onLoadCompleted(View arg0, String arg1, Bitmap arg2,
                                                        BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
                                int width = WritePreApp.getApp().getWidth(1f) / 8 * 5;
                                RelativeLayout.LayoutParams layoutParams;
                                if (arg2.getWidth() > width) {
                                    int height = (int) (arg2.getHeight() * ((float) width)
                                            / (float) arg2.getWidth());
                                    layoutParams = (LayoutParams) holder.album_image.getLayoutParams();
                                    if (layoutParams == null) {
                                        layoutParams = new RelativeLayout.LayoutParams(width, height);
                                        layoutParams.topMargin = 20;
                                    } else {
                                        layoutParams.width = width;
                                        layoutParams.height = height;
                                    }

                                } else {
                                    layoutParams = (LayoutParams) holder.album_image.getLayoutParams();
                                    if (layoutParams == null) {
                                        layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                                        layoutParams.topMargin = 20;
                                    } else {
                                        layoutParams.width = LayoutParams.WRAP_CONTENT;
                                        layoutParams.height = LayoutParams.WRAP_CONTENT;
                                    }
                                }
                                holder.album_image.setLayoutParams(layoutParams);
                                holder.album_image.setImageBitmap(arg2);
                            }

                            @Override
                            public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
                                ((ImageView) arg0).setImageResource(R.drawable.empty_photo);
                            }
                        });
                holder.album_image.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        imageBrower(0, groupNoticeInfo.getImg_url());
                    }
                });
            } else {
                holder.album_image.setVisibility(View.GONE);
                holder.gridView.setVisibility(View.VISIBLE);
                holder.gridView.setAdapter(
                        new SquareAllEssenceGridAdapter(holder.gridView,  mContext,groupNoticeInfo.getImg_url()));
                holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                        imageBrower(i, groupNoticeInfo.getImg_url());
                    }
                });


                if (groupNoticeInfo.getImg_url().length > 2) {
                    holder.gridView.setNumColumns(3);
                    holder.gridView.setGravity(Gravity.LEFT);
                } else if (groupNoticeInfo.getImg_url().length == 2) {
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

        return convertView;
    }

    private void imageBrower(int position, String[] urls) {
        Intent intent = new Intent(mContext, SquareImageLookActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_INDEX, position);
        mContext.startActivity(intent);
    }

    @Override
    public void onResponse(BaseResponse response) {
        if ("0".equals(response.getResCode())) {
            if (response instanceof GroupNoticePostDelResponse) {

            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
    }

    @Override
    public void onResponse(String tag, BaseResponse response) {
        if ("0".equals(response.getResCode())) {
            if (response instanceof GroupNoticeAddPostResponse) {
                if (!data.isEmpty()) {
                    for (int i = 0; i < data.size(); i++) {
                        if (TextUtils.equals(tag, data.get(i).get_id())) {
                            data.get(i).setIs_viewed("1");
                            data.get(i).setView_num((Integer.parseInt(data.get(i).getView_num()) + 1) + "");
                            break;
                        }
                    }
                    notifyDataSetChanged();
                }
            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
    }


    public class ViewHolder {
        private LinearLayout layoutOtherInfo;
        private SquareAllEssenceGridView gridView;
        private ImageView album_image;
        public TextView tv_uname, tv_ctime, tv_title2, tv_delete, tv_qiandaonum, tv_qiandao_lookall;
        private RoundImageView square_iv_head;
        private Button btn_qiandao;
    }

    /**
     * 删公告接口请求
     */
    public void requestDelPost(String circle_id, String pub_news_id) {
        RequestManager.request(mContext, new GroupNoticePostDelParams(circle_id, pub_news_id), GroupNoticePostDelResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    //确认查看公告
    public void requestLookNotice(String id, String pub_news_id) {
        RequestManager.request(mContext, id, new GroupNoticeAddPostParams(pub_news_id), GroupNoticeAddPostResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    public void showDelDialog(Context context, final String circle_id, final String pub_news_id) {

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
                requestDelPost(circle_id, pub_news_id);
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
}
