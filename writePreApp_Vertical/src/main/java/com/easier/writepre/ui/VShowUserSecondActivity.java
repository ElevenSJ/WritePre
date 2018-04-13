package com.easier.writepre.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.VShowUserSecondListAdapter;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.mainview.SocialMainView;
import com.easier.writepre.param.VShowGroupNumAddParams;
import com.easier.writepre.param.VShowUserSecondParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.VShowUserSecondResponse;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.XCRoundRectImageView;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.AutoUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * v展二级界面（v展专辑）
 */
public class VShowUserSecondActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private View headView;

    private TextView tv_name, tv_desc;

    private XCRoundRectImageView img_user_url;

    private PullToRefreshListView listView;

    private VShowUserSecondListAdapter adapter;

    private List<VShowUserSecondResponse.AlbumInfo> list = new ArrayList<VShowUserSecondResponse.AlbumInfo>();
    // 介绍详情收起标识
    private boolean flag = true;
    private RelativeLayout des_layout;
    private ImageView dropImg;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_v_show_second);
        init();
        addListHeader();
        request();
    }

    private void addListHeader() {
        headView = LayoutInflater.from(this).inflate(
                R.layout.list_head_v_show_second, null);
        AutoUtils.autoSize(headView);
        listView.getRefreshableView().addHeaderView(headView);
        tv_name = (TextView) headView.findViewById(R.id.tv_name);
        tv_desc = (TextView) headView.findViewById(R.id.tv_desc);
        img_user_url = (XCRoundRectImageView) headView.findViewById(R.id.img_user_url);
        tv_name.setText(getIntent().getStringExtra("real_name"));
        des_layout = (RelativeLayout) headView.findViewById(R.id.des_layout);
        dropImg = (ImageView) headView.findViewById(R.id.drop_img);
        tv_desc.setText(getIntent().getStringExtra("desc"));
        tv_desc.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                LogUtils.e("行数" + tv_desc.getLineCount());
                tv_desc.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                if (tv_desc.getLineCount() >= 4) {
                    dropImg.setVisibility(View.VISIBLE);
                    tv_desc.setEllipsize(TextUtils.TruncateAt.END); // 收缩
                    tv_desc.setMaxLines(4);
                    dropImg.setImageResource(R.drawable.arrow_down);
                    tv_desc.setOnClickListener(VShowUserSecondActivity.this);
                } else {
                    dropImg.setVisibility(View.GONE);
                    tv_desc.setOnClickListener(null);
                }


            }
        });
        dropImg.setVisibility(View.VISIBLE);
        dropImg.setOnClickListener(this);
        tv_desc.setOnClickListener(this);
        BitmapHelp.getBitmapUtils().display(
                img_user_url,
                StringUtil.getImgeUrl(getIntent().getStringExtra("photo_url"))
                        + Constant.VSHOW_IMAGE_SUFFIX, new BitmapLoadCallBack<ImageView>() {
                    @Override
                    public void onLoadCompleted(ImageView imageView, String s, Bitmap bitmap, BitmapDisplayConfig bitmapDisplayConfig, BitmapLoadFrom bitmapLoadFrom) {
                        imageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onLoadFailed(ImageView imageView, String s, Drawable drawable) {
                        imageView
                                .setImageResource(R.drawable.empty_photo);
                    }
                });
    }

    /**
     * V展专辑请求接口
     */
    private void request() {
        RequestManager.request(this, new VShowUserSecondParams(getIntent().getStringExtra("_id")), VShowUserSecondResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    /**
     * V展专辑数量
     */
    private void requestNum(String group_id) {
        RequestManager.request(this, new VShowGroupNumAddParams(group_id), BaseResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    private void init() {
        listView = (PullToRefreshListView) findViewById(R.id.list_v_show_second);
        findViewById(R.id.img_back).setOnClickListener(this);
        //setTopTitle(getIntent().getStringExtra("real_name"));
        setTopTitle("作品专辑");
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.drop_img:
                if (flag) {
                    flag = false;
                    tv_desc.setEllipsize(null); // 展开
                    tv_desc.setMaxLines(Integer.MAX_VALUE);
                    tv_desc.requestLayout();
                    dropImg.setImageResource(R.drawable.arrow_up);
                } else {
                    flag = true;
                    tv_desc.setEllipsize(TextUtils.TruncateAt.END); // 收缩
                    tv_desc.setMaxLines(4);
                    tv_desc.requestLayout();
                    dropImg.setImageResource(R.drawable.arrow_down);
                }
                break;
            case R.id.tv_desc:
                if (flag) {
                    flag = false;
                    tv_desc.setEllipsize(null); // 展开
                    tv_desc.setMaxLines(Integer.MAX_VALUE);
                    tv_desc.requestLayout();
                    dropImg.setImageResource(R.drawable.arrow_up);
                } else {
                    flag = true;
                    tv_desc.setEllipsize(TextUtils.TruncateAt.END); // 收缩
                    tv_desc.setMaxLines(4);
                    tv_desc.requestLayout();
                    dropImg.setImageResource(R.drawable.arrow_down);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onResponse(BaseResponse response) {
        des_layout.setVisibility(View.VISIBLE);
        if (response == null) {
            return;
        }
        if ("0".equals(response.getResCode())) {
            if (response instanceof VShowUserSecondResponse) {
                VShowUserSecondResponse vResult = (VShowUserSecondResponse) response;
                if (vResult != null) {
                    VShowUserSecondResponse.Repbody body = vResult.getRepBody();
                    if (body != null) {
                        if (body.getList() != null && body.getList().size() > 0) {
                            for (int i = 0; i < body.getList().size(); i++) {
                                list.add(body.getList().get(i));
                            }
                            if (adapter == null) {
                                adapter = new VShowUserSecondListAdapter(this, list);
                                listView.setAdapter(adapter);
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            adapter = new VShowUserSecondListAdapter(this, list);
                            listView.setAdapter(adapter);
                        }
                    }
                }
            }
        } else
            ToastUtil.show(response.getResMsg());
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i != 1) {
            {   // 无论接口成功与否(如果成功自+1可以在接口成功后处理即可)，专辑数量自加1
                requestNum(list.get(i - 2).get_id());
                list.get(i - 2).setView_num((Integer.parseInt(list.get(i - 2).getView_num()) + 1) + "");
                //adapter.getItem(i - 2).setView_num((Integer.parseInt(list.get(i - 2).getView_num()) + 1) + "");
                //adapter.notifyDataSetChanged();
                adapter.updateItemView(view, list.get(i - 2).getView_num());
            }
            Intent intent = new Intent(this, VShowUserThirdActivity.class);
            intent.putExtra("share_url", StringUtil.getImgeUrl(list.get(i - 2).getFace_url()) + Constant.VSHOW_IMAGE_SUFFIX);
            intent.putExtra("group_id", list.get(i - 2).get_id());
            intent.putExtra("name", tv_name.getText().toString());
            intent.putExtra("photo_url", getIntent().getStringExtra("photo_url"));
            intent.putExtra("title", list.get(i - 2).getTitle());
            intent.putExtra("url", SPUtils.instance().getSocialPropEntity().getShare_baseurl() + Constant.V_SHOW_PATH + list.get(i - 2).get_id());
            startActivity(intent);
            // if (adapter != null)
            // adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.TYPE_TWO));
        var.add(SocialMainView.CONTENT[SocialMainView.TAB_MicroExhibition]);
        var.add(getIntent().getStringExtra("real_name"));
        YouMengType.onEvent(this, var, getShowTime(), getIntent().getStringExtra("real_name"));
    }
}