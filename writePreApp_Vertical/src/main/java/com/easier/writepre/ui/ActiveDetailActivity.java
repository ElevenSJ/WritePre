package com.easier.writepre.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.SquareAllEssenceListAdapter;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.entity.ActiveInfo;
import com.easier.writepre.entity.ContentBase;
import com.easier.writepre.entity.ContentInfo;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.mainview.SocialMainView;
import com.easier.writepre.param.ActiveDetailParams;
import com.easier.writepre.param.SquareContentGetParams;
import com.easier.writepre.response.ActiveDetailResponse;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.SquareContentGetResponse;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;


public class ActiveDetailActivity extends BaseActivity {
    private String id = "";

    private Button btn_send_topic;
    private PullToRefreshListView lv_comment;

    public final int REQUEST_NEW_CODE = 1001;

    private ImageView bannerImg;
    private TextView titleTxt;
    private ImageView dropImg;
    private TextView descTxt;
    // 活动介绍详情收起标识
    private boolean flag = true;

    private ActiveInfo activeInfo;


    private SquareAllEssenceListAdapter adapter;
    //活动贴集合
    private List<ContentInfo> listData = new ArrayList<ContentInfo>();
    private Handler mHandler = new Handler();
    private boolean isRefresh = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_detail);
        id = getIntent().getStringExtra("id");
        initView();
        getActiveDetail();

    }

    private void initView() {
        setTopTitle("活动详情");

        btn_send_topic = (Button) findViewById(R.id.btn_send_topic);
        btn_send_topic.setOnClickListener(this);

        lv_comment = (PullToRefreshListView) findViewById(R.id.lv_comment);

        View headerView = LayoutInflater.from(this).inflate(R.layout.active_banner_layout, null);
        bannerImg = (ImageView) headerView.findViewById(R.id.active_banner);
        titleTxt = (TextView) headerView.findViewById(R.id.title);
        titleTxt.setVisibility(View.GONE);
        RelativeLayout relativeLayout = (RelativeLayout) headerView.findViewById(R.id.des_layout);
        relativeLayout.setVisibility(View.VISIBLE);
        descTxt = (TextView) headerView.findViewById(R.id.desc);
        dropImg = (ImageView) headerView.findViewById(R.id.drop_img);
        dropImg.setVisibility(View.VISIBLE);
        dropImg.setOnClickListener(this);
        descTxt.setOnClickListener(this);
        //动态设置banner高度
        LinearLayout.LayoutParams headerLayoutParams = new LinearLayout.LayoutParams(WritePreApp.getApp().getWidth(1f), WritePreApp.getApp().getWidth(0.333f));
        bannerImg.setLayoutParams(headerLayoutParams);
        lv_comment.getRefreshableView().addHeaderView(headerView);
        lv_comment
                .setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

                    @Override
                    public void onLastItemVisible() {

                    }
                });

        lv_comment.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < 2) {
                    return;
                }
                adapter.setSelectedIndex(position - 2);
                if (adapter.getItem(position - 2) instanceof ContentInfo) {
                    ContentInfo contentInfo = (ContentInfo) adapter.getItem(position - 2);
                    SquareAllEssenceListAdapter.ViewHolder holder = (SquareAllEssenceListAdapter.ViewHolder) view.getTag();
                    holder.tv_readCount.setTag(position - 2);
                    adapter.requestReadAdd(contentInfo);
                    Intent intent = new Intent(ActiveDetailActivity.this, TopicDetailActivity.class);
                    intent.putExtra(TopicDetailActivity.IS_SHOW_TOPIC, ActiveDetailActivity.this instanceof ActiveDetailActivity);
                    intent.putExtra("data", contentInfo);
                    startActivityForResult(intent, TopicDetailActivity.DETAIL_CODE);
                }

            }
        });

        lv_comment.setScrollingWhileRefreshingEnabled(true);
        lv_comment.setOnScrollListener(new PauseOnScrollListener(BitmapHelp.getBitmapUtils(), false, true));
        lv_comment.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh( // 顶部下拉刷新
                                             PullToRefreshBase<ListView> refreshView) {
                loadNews();
            }

            @Override
            public void onPullUpToRefresh( // 底部加载更多
                                           PullToRefreshBase<ListView> refreshView) {
                loadOlds();
            }
        });
        adapter = new SquareAllEssenceListAdapter(this,lv_comment.getRefreshableView());
        adapter.setNeedShow(false);
        lv_comment.setAdapter(adapter);
    }

    /**
     * 下拉获取数据
     */
    private void loadNews() {
        isRefresh = true;
        NoSwipeBackActivity.setListLabel(lv_comment, false);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                lv_comment.onRefreshComplete();
                getActivePL("9");
            }
        }, 300);
    }

    /**
     * 上拉获取数据
     */
    protected void loadOlds() {
        isRefresh = false;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                lv_comment.onRefreshComplete();
                if (listData != null && listData.size() > 0) {
                    if (adapter.getItem(adapter.getCount() - 1) instanceof ContentInfo) {
                        getActivePL(((ContentInfo) adapter.getItem(adapter.getCount() - 1))
                                .get_id());
                    }
                }
            }
        }, 300);
    }

    private void getActiveDetail() {
        dlgLoad.loading();
        RequestManager.request(this, new ActiveDetailParams(id),
                ActiveDetailResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    private void getActivePL(String lastId) {
        dlgLoad.loading();
        RequestManager.request(this, new SquareContentGetParams(lastId, 20, id),
                SquareContentGetResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_send_topic:
                if (LoginUtil.checkLogin(this)) {
                    Intent intent = new Intent(this, SendTopicActivity.class);
                    intent.putExtra(SendTopicActivity.MODE_TYPE,
                            SendTopicActivity.MODE_ACTIVE);
                    intent.putExtra("id", id);
                    startActivityForResult(intent, REQUEST_NEW_CODE);
                }
                break;
            case R.id.desc:
            case R.id.drop_img:
                if (flag) {
                    flag = false;
                    descTxt.setEllipsize(null); // 展开
                    descTxt.setSingleLine(flag);
                    dropImg.setImageResource(R.drawable.arrow_up);
                } else {
                    flag = true;
                    descTxt.setEllipsize(TextUtils.TruncateAt.END); // 收缩
//                    descTxt.setSingleLine(flag);
                    descTxt.setMaxLines(4);
                    dropImg.setImageResource(R.drawable.arrow_down);
                }
                break;
        }
    }


    @Override
    public void onResponse(BaseResponse response) {
        super.onResponse(response);

        if (response.getResCode().equals("0")) {
            if (response instanceof ActiveDetailResponse) {
                activeInfo = ((ActiveDetailResponse) response).getRepBody();
                updateActiveInfo();
                getActivePL("9");
            } else if (response instanceof SquareContentGetResponse) {
                dlgLoad.dismissDialog();
                SquareContentGetResponse gscrResult = (SquareContentGetResponse) response;
                if (gscrResult != null) {
                    SquareContentGetResponse.Repbody rBody = gscrResult.getRepBody();
                    if (rBody != null) {
                        if (rBody.getList() != null) {
                            if (isRefresh) {
                                listData.clear();
                            }
                            if (rBody.getList().isEmpty() && !isRefresh) {
                                NoSwipeBackActivity.setListLabel(lv_comment, true);
                            }
                            listData.addAll(rBody.getList());
                            adapter.mergeData(listData,null);
                            adapter.notifyDataSetChanged();
                        }

                    }
                }

            } else {
                dlgLoad.dismissDialog();
                ToastUtil.show(response.getResMsg());
            }
        }
    }

    private void updateActiveInfo() {
        if (activeInfo != null) {
            setTopTitle(activeInfo.getTitle());
            BitmapHelp.getBitmapUtils().display(bannerImg, StringUtil.getImgeUrl(TextUtils.isEmpty(activeInfo.getImg_url_h()) ? activeInfo.getImg_url() : activeInfo.getImg_url_h()), new BitmapLoadCallBack<ImageView>() {
                @Override
                public void onLoadCompleted(ImageView imageView, String s, Bitmap bitmap, BitmapDisplayConfig bitmapDisplayConfig, BitmapLoadFrom bitmapLoadFrom) {
                    bannerImg.setImageBitmap(bitmap);
                }

                @Override
                public void onLoadFailed(ImageView imageView, String s, Drawable drawable) {
                    bannerImg.setImageResource(R.drawable.empty_photo);
                }
            });
            descTxt.setText(activeInfo.getDesc());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case REQUEST_NEW_CODE:
                    loadNews();
                    break;
                case TopicDetailActivity.DETAIL_CODE:
                    if (data != null) {
                        adapter.replace(adapter.getSelectedIndex(), (ContentBase) data.getSerializableExtra("data"));
                    }
                    break;
            }
        } else if (RESULT_CANCELED == resultCode) {
            if (TopicDetailActivity.DETAIL_CODE == requestCode) {
                adapter.remove(adapter.getSelectedIndex());
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        uMeng();
    }

    /**
     * 友盟统计
     */
    private void uMeng() {
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.TYPE_TWO));
        var.add(SocialMainView.CONTENT[SocialMainView.TAB_SQUARE]);
        var.add("广场活动");
        YouMengType.onEvent(this, var, getShowTime(), activeInfo != null ? activeInfo.getTitle() : "广场活动");
    }
}
