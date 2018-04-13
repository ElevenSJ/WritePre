
package com.easier.writepre.ui;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.SquareAllEssenceGridAdapter;
import com.easier.writepre.adapter.SquareAllEssenceListAdapter;
import com.easier.writepre.adapter.TopicCommentsAdapter;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.entity.ContentInfo;
import com.easier.writepre.entity.TopicCommentsInfo;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.inputmessage.RecordButton;
import com.easier.writepre.mainview.SocialMainView;
import com.easier.writepre.param.GiveOkParams;
import com.easier.writepre.param.SquarePostDelParams;
import com.easier.writepre.param.TopicCommentsDeleteParams;
import com.easier.writepre.param.TopicCommentsParams;
import com.easier.writepre.param.TopicDetailParams;
import com.easier.writepre.param.TopicPostCommentParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.GiveOkResponse;
import com.easier.writepre.response.SquarePostDelResponse;
import com.easier.writepre.response.TopicCommentsResponse;
import com.easier.writepre.response.TopicCommentsResponse.TopicCommentsBody;
import com.easier.writepre.response.TopicDetailResponse;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshBase.Mode;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnLastItemVisibleListener;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnRefreshListener2;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.DeviceUtils;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.NetWorkUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.NineGridView;
import com.easier.writepre.widget.NineGridViewAdapter;
import com.easier.writepre.widget.RoundImageView;
import com.easier.writepre.widget.SquareAllEssenceGridView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sj.autolayout.utils.AutoUtils;
import com.sj.autolayout.utils.DateKit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 帖子详情
 *
 * @author zhaomaohan
 */

public class TopicDetailActivity extends BaseActivity {

    public static final int DETAIL_CODE = 1003;

    private SimpleDraweeView img_head;

    private ImageView img_share;

    private ImageView img_teacher;

    private TextView tv_username;

    private TextView tv_create_time;

    private TextView tv_city;

    private NineGridView gv_topic_image;// 帖子图片的gridview

    private TextView tv_title;

    private TextView tv_like_number;

    private TextView tv_comment_number;

    private PullToRefreshListView lv_comment;// 评论列表

    private EditText et_input_comment;

    private Button btn_send_comment;

    private List<TopicCommentsInfo> list = new ArrayList<TopicCommentsInfo>();

    private String flag = "1"; // 0 赞 1 评论

    private TopicCommentsAdapter mTopicCommentsAdapter;

    private String reply_to_commentId;// 回复哪条评论的id

    private String reply_to_userId;// 回复哪个用户的id

    private Boolean isReply = false;// 是否回复状态，用来判断发送评论是新增还是回复。true_回复，false_评论

    private int positionDel;// 删除评论的list的位置

    private String myId;

    private String deleteflag = "0"; // 1为删除 0为评论

    private TextView tv_readCount;// 阅读数
    protected String comment_title;

    protected PopupWindow popupWindow2;
    /**
     * 点击赞按钮的次数，用来判断是否调用点赞接口，如果从评论界面点赞按钮。 count = 1,则赞只显示赞数。 在赞的点击事件里设置count++；
     * 若在赞页面再点一次赞，count= 2;则请求赞接口。再赞则再请求取消赞,即判断count>=2时，请求赞。 若点了评论，则设count = 0;
     */
    private Handler handler;

    private PopupWindow popupWindow1;

    protected int clickNum = 0;// 评论item的点击次数

    private View headView;

    private ImageView img_comment_line;

    private ImageView img_zan_line;

    private TextView tv_delete;

    private ContentInfo contentInfo;
    private String topicId;

    public final static String IS_SHOW_TOPIC = "is_show_topic";
    public final static int MAIN_ACTIVITY = 0;
    public final static int LIST_ACTIVITY = 1;
    public final static int USER_SQUARE_POST_ACTIVITY = 2;

    public final static int INPUT_TYPE_TEXT = 0;
    public final static int INPUT_TYPE_VOICE = 1;
    private ImageView iv_input_type;//输入方式 0 文本模式  1语音模式
    private RecordButton btn_voice_send;
    private RelativeLayout rel_voice_input, rel_text_input;
    private HashMap<String, String> voiceMap = new HashMap<>();
    private boolean isShowTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);
        isShowTopic = !getIntent().getBooleanExtra(IS_SHOW_TOPIC, false);
        contentInfo = (ContentInfo) getIntent().getSerializableExtra("data");
        initView();
        if (contentInfo != null) {
            topicId = contentInfo.get_id();
            if (!NetWorkUtils.isNetworkConnected()) {
                setTopicInfo();
            }
        } else {
            topicId = getIntent().getStringExtra("id");
        }
        getTopicInfo();
    }

    private void getTopicInfo() {
        if (TextUtils.isEmpty(topicId)) {
            return;
        } else {
            dlgLoad.loading();
            RequestManager.request(this, new TopicDetailParams(topicId),
                    TopicDetailResponse.class, this, SPUtils.instance()
                            .getSocialPropEntity().getApp_socail_server());
        }
    }

    private void setTopicInfo() {
        if (contentInfo == null) {
            return;
        }
        if (TextUtils.isEmpty(contentInfo.getIs_ok())) {
            contentInfo.setIs_ok("0");
        }
        // 删除帖子
        if (contentInfo != null
                && SPUtils.instance().getLoginEntity().get_id()
                .equals(contentInfo.getUser_id())) {
            tv_delete.setVisibility(View.VISIBLE);
        } else {
            tv_delete.setVisibility(View.GONE);
        }

        img_head.setImageURI(StringUtil.getHeadUrl(contentInfo.getHead_img()));
        img_teacher.setVisibility(TextUtils.isEmpty(contentInfo.getIs_teacher()) ? View.GONE : View.VISIBLE);
        tv_username.setText(TextUtils.isEmpty(contentInfo.getUname())?"_"+contentInfo.getUser_id():contentInfo.getUname());
        tv_create_time.setText(DateKit.timeFormat(contentInfo.getCtime()));
        tv_readCount.setText(contentInfo.getView_num() + "");
        tv_city.setVisibility(!TextUtils.isEmpty(contentInfo.getCity())
                && !contentInfo.getCity().equalsIgnoreCase("null") ? View.VISIBLE : View.GONE);
        tv_city.setText(contentInfo.getCity());
        if (TextUtils.isEmpty(contentInfo.getTitle())) {
            if (isShowTopic && !TextUtils.isEmpty(contentInfo.getTopic_title())) {
                tv_title.setVisibility(View.VISIBLE);
            } else {
                tv_title.setVisibility(View.GONE);
            }
        } else {
            tv_title.setVisibility(View.VISIBLE);
        }
        if (isShowTopic && !TextUtils.isEmpty(contentInfo.getTopic_title())) {
            String contentStr = "#" + contentInfo.getTopic_title().trim() + "#" + " "
                    + (TextUtils.isEmpty(contentInfo.getTitle()) ? "" : contentInfo.getTitle().trim());
            SpannableString spannableString = new SpannableString(contentStr);

            spannableString.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(TopicDetailActivity.this, ActiveDetailActivity.class);
                    intent.putExtra("id", contentInfo.getTopic_id());
                    TopicDetailActivity.this.startActivity(intent);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
//                            ds.setColor(mContext.getResources().getColor(R.color.social_red));
                    ds.setUnderlineText(false);
                    ds.clearShadowLayer();
                }
            }, 0, contentInfo.getTopic_title().trim().length() + 2, 0);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#14A4BF")), 0,
                    contentInfo.getTopic_title().trim().length() + 2, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

            tv_title.setText(spannableString);
            tv_title.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            tv_title.setText(contentInfo.getTitle().trim());
        }
        tv_like_number.setText("" + contentInfo.getOk_num());
        tv_comment_number.setText("" + contentInfo.getRemark_num());
        tv_like_number.setSelected(contentInfo.getIs_ok().equals("1"));

        ArrayList<String> imageInfo = new ArrayList<>();
        for (int i = 0; i < contentInfo.getImg_url().length; i++) {
            imageInfo.add(StringUtil.getImgeUrl(contentInfo.getImg_url()[i]) + Constant.SMALL_IMAGE_SUFFIX);
        }
        NineGridViewClickAdapter adapter;
        if (gv_topic_image.getTag(gv_topic_image.getId()) != null) {
            adapter = (NineGridViewClickAdapter) gv_topic_image.getTag(gv_topic_image.getId());
            adapter.setImageInfoList(imageInfo);
        } else {
            adapter = new NineGridViewClickAdapter(this, imageInfo);
            gv_topic_image.setTag(gv_topic_image.getId(), adapter);
        }
        gv_topic_image.setAdapter(adapter, !TextUtils.isEmpty(contentInfo.getVod_url()));

        // 页面加载就先请求评论数据
        dlgLoad.loading();
        loadNews();
    }

    /**
     * 播放视频
     *
     * @param video_url
     */
    private void playVideo(String video_url) {
        //友盟统计
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.TYPE_TWO));
        var.add("查看小视频");
        if (contentInfo instanceof ContentInfo && !TextUtils.isEmpty(contentInfo.getTopic_id())) {
            YouMengType.onEvent(this, var, 1, "活动");
        } else {
            YouMengType.onEvent(this, var, 1, "广场");
        }
        Intent intent = new Intent(this, MediaActivity.class);
        intent.putExtra(MediaActivity.URL, StringUtil.getImgeUrl(contentInfo.getVod_url()));
        startActivity(intent);
    }

    private void imageBrower(int position, String[] urls) {
        //友盟统计
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.TYPE_TWO));
        var.add("查看大图");
        if (!TextUtils.isEmpty(contentInfo.getTopic_id())) {
            YouMengType.onEvent(TopicDetailActivity.this, var, 1, "活动");
        } else {
            YouMengType.onEvent(TopicDetailActivity.this, var, 1, "广场");
        }

        Intent intent = new Intent(TopicDetailActivity.this,
                SquareImageLookActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(SquareImageLookActivity.EXTRA_IMAGE_INDEX, position);
        startActivity(intent);
    }

    private void initView() {
        setTopTitle("帖子详情");
        setTopRight(R.drawable.list);
        // 加载控件
        handler = new Handler(Looper.getMainLooper());

        lv_comment = (PullToRefreshListView) findViewById(R.id.lv_comment);

        et_input_comment = (EditText) findViewById(R.id.et_input_comment);

        btn_send_comment = (Button) findViewById(R.id.btn_send_comment);
        iv_input_type = (ImageView) findViewById(R.id.iv_input_type);
        btn_voice_send = (RecordButton) findViewById(R.id.btn_voice_send);
        rel_voice_input = (RelativeLayout) findViewById(R.id.rel_vioce_input);
        rel_text_input = (RelativeLayout) findViewById(R.id.rel_text_input);
        iv_input_type.setOnClickListener(this);
        btn_voice_send.setRecorderCallback(new RecordButton.OnFinishedRecordListener() {
            @Override
            public void onFinishedRecord(String audioPath, int length) {
                //友盟统计
                List<String> var = new ArrayList<String>();
                var.add(YouMengType.getName(MainActivity.TYPE_TWO));
                var.add("评价");
                if (!TextUtils.isEmpty(contentInfo.getTopic_id())) {
                    YouMengType.onEvent(TopicDetailActivity.this, var, 1, "活动");
                } else {
                    YouMengType.onEvent(TopicDetailActivity.this, var, 1, "广场");
                }
                if (TextUtils.isEmpty(audioPath)) {
                    ToastUtil.show("录音失败");
                    return;
                } else {
                    if (FileUtils.getFileSize(audioPath) <= 0) {
                        ToastUtil.show("录音失败");
                        return;
                    }
                }
                voiceMap.put(audioPath, "" + length);
                commitVoiceOss(audioPath, mVoiceHandler);
            }
        });
        initListHeader();
        // 默认禁止下拉刷新和上拉加载

        lv_comment.setMode(Mode.PULL_FROM_START);

        // 拉到底部监听，设置到底部再上拉加载，解决headview内容过多显示不全的问题
        lv_comment
                .setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

                    @Override
                    public void onLastItemVisible() {
                        if (contentInfo != null) {
                            if (contentInfo.getRemark_num() == 0
                                    && contentInfo.getOk_num() == 0) {

                            } else {
                                lv_comment.setMode(Mode.BOTH);
                            }
                        }
                        //
                    }
                });

        lv_comment.setScrollingWhileRefreshingEnabled(true);
        lv_comment.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh( // 顶部下拉刷新
                                             PullToRefreshBase<ListView> refreshView) {
                if (mTopicCommentsAdapter != null) {
                    mTopicCommentsAdapter.stopVoice();
                }
                loadNews();
            }

            @Override
            public void onPullUpToRefresh( // 底部加载更多
                                           PullToRefreshBase<ListView> refreshView) {
                if (mTopicCommentsAdapter != null) {
                    mTopicCommentsAdapter.stopVoice();
                }
                loadOlds();
            }
        });

        lv_comment
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if (position == 1) {
                            return;
                        }

                        myId = SPUtils.instance().getLoginEntity().get_id();

                        TopicCommentsInfo tci = list.get(position - 2);
                        String username = tci.getUname();
                        reply_to_commentId = tci.get_id();
                        reply_to_userId = tci.getUser_id();
                        clickNum++;
                        if (clickNum % 2 == 0
                                || (!TextUtils.isEmpty(myId) && myId
                                .equals(reply_to_userId))) {
                            et_input_comment.setHint("对这个帖子说点什么吧");
                            isReply = false;
                        } else {
                            et_input_comment.setHint("回复@" + username);
                            isReply = true;
                        }
                        et_input_comment.requestFocus();
                    }
                });
        lv_comment.getRefreshableView().setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {

                    private Button pop_btn_delete;

                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent,
                                                   View view, int position, long id) {
                        if (position == 1 || flag.equals("0")) {
                            return true;
                        }
                        TopicCommentsInfo tci = list.get(position - 2);
                        String username = tci.getUname();
                        reply_to_userId = tci.getUser_id();
                        reply_to_commentId = tci.get_id();
                        comment_title = tci.getTitle();
                        // et_input_comment.setHint("回复@" + username);
                        if (LoginUtil.checkLogin(TopicDetailActivity.this)) {
                            myId = SPUtils.instance().getLoginEntity()
                                    .get_id();
                            if (reply_to_userId.equals(myId)) {
                                showPopupWindow_2(view);
                            } else {
                                if (!TextUtils.isEmpty(tci.getVoice_url())) {
                                    return true;
                                }
                                showPopupWindow_2(view);
                            }

                            positionDel = (position - 2);
                        }
                        return false;
                    }

                    private void showPopupWindow_2(View view) {
                        // 长按评论item弹出删除或复制的popwindow
                        View convertView2 = LayoutInflater.from(
                                TopicDetailActivity.this).inflate(
                                R.layout.pop_comments_topic, null);
                        pop_btn_delete = (Button) convertView2
                                .findViewById(R.id.pop_btn_delete);
                        if (reply_to_userId.equals(myId)) {
                            pop_btn_delete.setText("删除");
                        } else {
                            pop_btn_delete.setText("复制");
                        }
                        pop_btn_delete
                                .setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        if (pop_btn_delete.getText()
                                                .equals("删除")) {
                                            if (mTopicCommentsAdapter != null) {
                                                mTopicCommentsAdapter.stopVoice();
                                            }
                                            RequestManager
                                                    .request(
                                                            TopicDetailActivity.this,
                                                            new TopicCommentsDeleteParams(
                                                                    reply_to_commentId),
                                                            BaseResponse.class,
                                                            TopicDetailActivity.this,
                                                            SPUtils.instance()
                                                                    .getSocialPropEntity()
                                                                    .getApp_socail_server());
                                            deleteflag = "1";

                                        } else {
                                            copy(comment_title,
                                                    TopicDetailActivity.this);
                                        }
                                        popupWindow2.dismiss();
                                    }
                                });
                        popupWindow2 = new PopupWindow(convertView2,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);

                        ColorDrawable cd = new ColorDrawable(0x000000);
                        popupWindow2.setBackgroundDrawable(cd);
                        // 产生背景变暗效果
                        // WindowManager.LayoutParams lp = getWindow()
                        // .getAttributes();
                        // lp.alpha = 0.4f;
                        // getWindow().setAttributes(lp);

                        popupWindow2.setOutsideTouchable(true);
                        popupWindow2.setFocusable(true);
                        // popupWindow2.showAtLocation((View)view.getParent(),Gravity.RIGHT,
                        // x, 0);
                        popupWindow2.showAsDropDown(view, 300, 0);
                        popupWindow2.update();
                        // popupWindow2
                        // .setOnDismissListener(new OnDismissListener() {
                        //
                        // // 在dismiss中恢复透明度
                        // public void onDismiss() {
                        // WindowManager.LayoutParams lp = getWindow()
                        // .getAttributes();
                        // lp.alpha = 1f;
                        // getWindow().setAttributes(lp);
                        // }
                        // });

                    }
                });
    }

    private void initListHeader() {

        headView = LayoutInflater.from(this).inflate(
                R.layout.list_header_topic_detail, null);
        AutoUtils.autoSize(headView);
        lv_comment.getRefreshableView().addHeaderView(headView);
        img_head = (SimpleDraweeView) headView.findViewById(R.id.square_iv_head);
        img_teacher = (ImageView) headView.findViewById(R.id.teacher_icon);
        tv_username = (TextView) headView.findViewById(R.id.tv_uname);
        tv_create_time = (TextView) headView.findViewById(R.id.tv_ctime);
        tv_city = (TextView) headView.findViewById(R.id.tv_city);
        tv_readCount = (TextView) headView.findViewById(R.id.tv_readCount);
        gv_topic_image = (NineGridView) headView
                .findViewById(R.id.gridview);

        tv_title = (TextView) headView.findViewById(R.id.tv_title2);

        img_share = (ImageView) headView.findViewById(R.id.square_iv_share);

        img_zan_line = (ImageView) headView.findViewById(R.id.img_zan_line);
        img_comment_line = (ImageView) headView
                .findViewById(R.id.img_comment_line);

        tv_like_number = (TextView) headView.findViewById(R.id.tv_ok_num);

        tv_comment_number = (TextView) headView
                .findViewById(R.id.tv_remark_num);
        tv_delete = (TextView) findViewById(R.id.tv_delete);
        img_head.setOnClickListener(this);
        tv_like_number.setOnClickListener(this);
        tv_comment_number.setOnClickListener(this);
        img_share.setOnClickListener(this);
        btn_send_comment.setOnClickListener(this);
        tv_delete.setOnClickListener(this);

        mTopicCommentsAdapter = new TopicCommentsAdapter(list,
                TopicDetailActivity.this, flag);
        lv_comment.setAdapter(mTopicCommentsAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTopicCommentsAdapter != null) {
            mTopicCommentsAdapter.stopVoice();
        }
        //友盟统计
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.TYPE_TWO));
        var.add("帖子详情");
        if (contentInfo != null && !TextUtils.isEmpty(contentInfo.getTopic_id())) {
            YouMengType.onEvent(this, var, getShowTime(), "活动");
        } else {
            YouMengType.onEvent(this, var, getShowTime(), "广场");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mTopicCommentsAdapter != null) {
            mTopicCommentsAdapter.stopVoice();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTopicCommentsAdapter != null) {
            mTopicCommentsAdapter.stopVoice();
        }
    }

    private void notifyAdpterdataChanged() {
        mTopicCommentsAdapter.setData(list, flag);
        mTopicCommentsAdapter.notifyDataSetChanged();
        if (flag.equals("0")) {
            contentInfo.setOk_num(mTopicCommentsAdapter.getCount());
            tv_like_number.setText(mTopicCommentsAdapter.getCount() + "");
        } else {
            contentInfo.setRemark_num(mTopicCommentsAdapter.getCount());
            tv_comment_number.setText(mTopicCommentsAdapter.getCount() + "");
        }
    }

    @Override
    public void onResponse(String tag, BaseResponse response) {
        super.onResponse(tag, response);
        dlgLoad.dismissDialog();
        if ("0".equals(response.getResCode())) {
            if (response instanceof TopicCommentsResponse) {
                TopicCommentsResponse mTopicCommentsResponse = (TopicCommentsResponse) response;
                TopicCommentsBody mTopicCommentsBody = mTopicCommentsResponse
                        .getRepBody();
                if (tag.equals("isNew")) {
                    list.clear();
                }
                if (mTopicCommentsBody.getList().isEmpty()) {
                    if (tag.equals("isNew")) {
                        notifyAdpterdataChanged();
                    }
                } else {
                    for (int i = 0; i < mTopicCommentsBody.getList().size(); i++) {
                        list.add(mTopicCommentsBody.getList().get(i));
                    }
                    notifyAdpterdataChanged();
                }
            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
    }

    @Override
    public void onResponse(BaseResponse response) {
        dlgLoad.dismissDialog();

        if ("0".equals(response.getResCode())) {
            if (response instanceof GiveOkResponse) {
                // ToastUtil.show("赞成功");
                if (contentInfo.getIs_ok().equals("0")) {
                    contentInfo.setOk_num(contentInfo.getOk_num() + 1);
                    tv_like_number.setSelected(true);
                    contentInfo.setIs_ok("1");
                    //友盟统计
                    List<String> var = new ArrayList<String>();
                    var.add(YouMengType.getName(MainActivity.TYPE_TWO));
                    var.add("点赞");
                    if (!TextUtils.isEmpty(contentInfo.getTopic_id())) {
                        YouMengType.onEvent(this, var, 1, "活动");
                    } else {
                        YouMengType.onEvent(this, var, 1, "广场");
                    }
                } else {
                    contentInfo.setOk_num(contentInfo.getOk_num() - 1);
                    tv_like_number.setSelected(false);
                    contentInfo.setIs_ok("0");
                }
                tv_like_number.setText("" + contentInfo.getOk_num());
                requestMoreDate("isNew", "9", true);
            } else if (response instanceof SquarePostDelResponse) {
                ToastUtil.show("删除成功");
                onback(RESULT_CANCELED);
            } else if (response instanceof TopicDetailResponse) {
                TopicDetailResponse mTopicDetailResponse = (TopicDetailResponse) response;
                contentInfo = mTopicDetailResponse.getRepBody();
                setTopicInfo();
            } else {
                if (deleteflag.equals("1")) {
                    ToastUtil.show("删除成功");
                    contentInfo.setRemark_num(contentInfo.getRemark_num() - 1);
                    tv_comment_number.setText("" + contentInfo.getRemark_num());
                    list.remove(positionDel);
                    notifyAdpterdataChanged();
                } else {
                    ToastUtil.show("评论成功");
                    et_input_comment.setText("");
                    et_input_comment.setHint("对这个帖子说点什么吧");
                    isReply = false;
                    contentInfo.setRemark_num(contentInfo.getRemark_num() + 1);
                    tv_comment_number.setText("" + contentInfo.getRemark_num());
                    img_comment_line.setVisibility(View.VISIBLE);
                    img_zan_line.setVisibility(View.GONE);
                    flag = "1";
                    requestMoreDate("isNew", "9", false);
                }
            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
    }

    public void requestMoreDate(String tag, String id, boolean zanOrComment) {
        RequestManager.request(this, tag,
                new TopicCommentsParams(contentInfo.get_id(), id, 20, zanOrComment),
                TopicCommentsResponse.class, this, SPUtils.instance()
                        .getSocialPropEntity().getApp_socail_server());
    }

    protected void loadNews() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopRefresh();
                list.clear();
                if (flag.equals("0")) {
                    requestMoreDate("isNew", "9", true);
                } else {
                    requestMoreDate("isNew", "9", false);
                }
            }
        }, 300);
    }

    protected void loadOlds() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopRefresh();
                if (list != null && list.size() > 0) {
                    if (flag.equals("0")) {
                        requestMoreDate("isOld", list.get(list.size() - 1).get_id(), true);
                    } else {
                        requestMoreDate("isOld", list.get(list.size() - 1).get_id(), false);
                    }
                }
            }
        }, 300);
    }

    protected void stopRefresh() {
        lv_comment.onRefreshComplete();
    }

    public void onTopRightClick(View view) {
        showPopupWindow1(view);// 举报窗口
    }

    public void onTopLeftClick(View view) {
        onback(RESULT_OK);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_ok_num:
                if (contentInfo == null) {
                    ToastUtil.show("未获取到详情");
                    return;
                }
                if (flag.equals("0")) {
                    if (LoginUtil.checkLogin(TopicDetailActivity.this)) {
                        RequestManager.request(this,
                                new GiveOkParams(contentInfo.get_id()),
                                GiveOkResponse.class, this, SPUtils.instance()
                                        .getSocialPropEntity()
                                        .getApp_socail_server());
                    }
                } else {
                    img_zan_line.setVisibility(View.VISIBLE);
                    img_zan_line.bringToFront();
                    img_comment_line.setVisibility(View.INVISIBLE);
                    flag = "0";
                    dlgLoad.loading();
                    requestMoreDate("isNew", "9", true);
                }
                break;
            case R.id.tv_remark_num:
                if (contentInfo == null) {
                    ToastUtil.show("未获取到详情");
                    return;
                }
                img_comment_line.setVisibility(View.VISIBLE);
                img_comment_line.bringToFront();
                img_zan_line.setVisibility(View.INVISIBLE);
                flag = "1";
                dlgLoad.loading();
                requestMoreDate("isNew", "9", false);
                break;
            case R.id.square_iv_share:
                if (contentInfo == null) {
                    ToastUtil.show("未获取到详情");
                    return;
                }
                sharedContentCustomize(
                        "#广场帖子#",
                        contentInfo.getTitle(),
                        SPUtils.instance().getSocialPropEntity()
                                .getShare_baseurl()
                                + Constant.SHARE_SQUARE_PATH
                                + contentInfo.get_id(),
                        SPUtils.instance().getSocialPropEntity()
                                .getShare_baseurl()
                                + Constant.SHARE_SQUARE_PATH
                                + contentInfo.get_id(), gv_topic_image.getImageView(0));

                break;
            case R.id.btn_send_comment:
                if (contentInfo == null) {
                    ToastUtil.show("未获取到详情");
                    return;
                }
                if (LoginUtil.checkLogin(TopicDetailActivity.this)) {
                    //友盟统计
                    List<String> var = new ArrayList<String>();
                    var.add(YouMengType.getName(MainActivity.TYPE_TWO));
                    var.add("评价");
                    if (!TextUtils.isEmpty(contentInfo.getTopic_id())) {
                        YouMengType.onEvent(TopicDetailActivity.this, var, 1, "活动");
                    } else {
                        YouMengType.onEvent(TopicDetailActivity.this, var, 1, "广场");
                    }
                    deleteflag = "0";
                    String commentContent = et_input_comment.getText().toString();
                    String temp = null;
                    if (!TextUtils.isEmpty(commentContent)) {
                        temp = commentContent.trim();
                    }
                    if (TextUtils.isEmpty(commentContent)
                            || TextUtils.isEmpty(temp)) {
                        ToastUtil.show("说点什么吧");
                        return;
                    }
                    dlgLoad.loading();
                    if (!isReply) { // 新增
                        RequestManager.request(this, new TopicPostCommentParams(
                                        contentInfo.get_id(), "", "", commentContent.trim(), "", "0"),
                                BaseResponse.class, this, SPUtils.instance()
                                        .getSocialPropEntity()
                                        .getApp_socail_server());
                    } else { // 回复
                        RequestManager.request(this, new TopicPostCommentParams(
                                        contentInfo.get_id(), reply_to_commentId,
                                        reply_to_userId, commentContent.trim(), "", "0"),
                                BaseResponse.class, this, SPUtils.instance()
                                        .getSocialPropEntity()
                                        .getApp_socail_server());
                    }
                }
                break;
            case R.id.square_iv_head:
                if (contentInfo == null) {
                    ToastUtil.show("未获取到详情");
                    return;
                }
                Intent intent = new Intent(TopicDetailActivity.this,
                        UserInfoActivity.class);
                intent.putExtra("user_id", contentInfo.getUser_id());
                startActivity(intent);
                break;
            case R.id.tv_delete:
                if (contentInfo == null) {
                    ToastUtil.show("未获取到详情");
                    return;
                }
                showDelDialog(this, contentInfo.get_id());
                break;
            case R.id.iv_input_type:
                //语音文本切换
                if (rel_text_input.getVisibility() == View.VISIBLE) {
                    //文本切换到语音
                    if (et_input_comment != null) {
                        DeviceUtils.closeKeyboard(et_input_comment, this);
                    }
                    iv_input_type.setBackgroundResource(R.drawable.rc_keyboard);
                    rel_voice_input.setVisibility(View.VISIBLE);
                    rel_text_input.setVisibility(View.GONE);
                } else {
                    //语音切换到文本
                    iv_input_type.setBackgroundResource(R.drawable.rc_audio_toggle);
                    rel_text_input.setVisibility(View.VISIBLE);
                    rel_voice_input.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    private void showPopupWindow1(View v) {
        // TODO Auto-generated method stub
        View convertView1 = LayoutInflater.from(this).inflate(
                R.layout.pop_report_topic, null);
        Button pop_btn_report = (Button) convertView1
                .findViewById(R.id.pop_btn_report);
        pop_btn_report.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // ToastUtil.show("举报");
                popupWindow1.dismiss();
                if (contentInfo == null) {
                    ToastUtil.show("未获取到详情");
                    return;
                }
                if (LoginUtil.checkLogin(TopicDetailActivity.this)) {
                    Intent intent = new Intent(TopicDetailActivity.this,
                            ReportTopicActivity.class);
                    intent.putExtra("topic_id", contentInfo.get_id());
                    startActivity(intent);
                }
            }
        });
        popupWindow1 = new PopupWindow(convertView1,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        ColorDrawable cd = new ColorDrawable(0x000000);
        popupWindow1.setBackgroundDrawable(cd);
        // 产生背景变暗效果
        // WindowManager.LayoutParams lp = getWindow().getAttributes();
        // lp.alpha = 0.4f;
        // getWindow().setAttributes(lp);

        popupWindow1.setOutsideTouchable(true);
        popupWindow1.setFocusable(true);
        // popupWindow1.showAtLocation((View) v.getParent(), Gravity.TOP
        // | Gravity.RIGHT, 10, 100);
        popupWindow1.showAsDropDown(v, 10, 30);
        popupWindow1.update();
        // popupWindow1.setOnDismissListener(new OnDismissListener() {
        //
        // // 在dismiss中恢复透明度
        // public void onDismiss() {
        // WindowManager.LayoutParams lp = getWindow().getAttributes();
        // lp.alpha = 1f;
        // getWindow().setAttributes(lp);
        // }
        // });

    }

    @SuppressWarnings("deprecation")
    public static void copy(String content, Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
        ToastUtil.show("复制成功");
    }

    public void showDelDialog(Context context, final String post_id) {

        final Dialog dialog = new Dialog(context, R.style.loading_dialog);
        View view = LayoutInflater.from(context).inflate(
                R.layout.dialog_islogin, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_login_now);
        tv.setText("确定是否删除?");
        TextView confirm = (TextView) view.findViewById(R.id.tv_login);
        confirm.setText("确定");
        confirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dlgLoad.loading();
                requestDelPost(post_id);
            }
        });
        view.findViewById(R.id.tv_cancel).setOnClickListener(
                new OnClickListener() {

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
    public void requestDelPost(String post_id) {
        RequestManager.request(this, new SquarePostDelParams(post_id),
                SquarePostDelResponse.class, this, SPUtils.instance()
                        .getSocialPropEntity().getApp_socail_server());
    }

    private void onback(int resultCode) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("data", contentInfo);
        setResult(resultCode, resultIntent);
        super.onTopLeftClick(null);
    }

    @Override
    public void onBackPressed() {
        onback(RESULT_OK);
    }

    @Override
    public void onSwipBack() {
        onback(RESULT_OK);
    }

    /**
     * OSS提交状态
     */
    private Handler mVoiceHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case COMMIT_VOICE_OSS_FAIL:
                    dlgLoad.dismissDialog();
                    ToastUtil.show("语音上传失败");
                    break;
                case COMMIT_VOICE_OSS_SUCCESS:
                    Bundle bundle = msg.getData();
                    String voiceFilePath = (String) bundle.get("filePath");
                    String voiceOssPath = (String) bundle.get("ossPath");
                    if (LoginUtil.checkLogin(TopicDetailActivity.this)) {
                        deleteflag = "0";
                        if (!isReply) { // 新增
                            RequestManager.request(TopicDetailActivity.this, new TopicPostCommentParams(
                                            contentInfo.get_id(), "", "", "", voiceOssPath, voiceMap.get(voiceFilePath)),
                                    BaseResponse.class, TopicDetailActivity.this, SPUtils.instance()
                                            .getSocialPropEntity()
                                            .getApp_socail_server());
                        } else { // 回复
                            RequestManager.request(TopicDetailActivity.this, new TopicPostCommentParams(
                                            contentInfo.get_id(), reply_to_commentId,
                                            reply_to_userId, "", voiceOssPath, voiceMap.get(voiceFilePath)),
                                    BaseResponse.class, TopicDetailActivity.this, SPUtils.instance()
                                            .getSocialPropEntity()
                                            .getApp_socail_server());
                        }
                    }
                    break;
            }
        }
    };

    class NineGridViewClickAdapter extends NineGridViewAdapter {

        public NineGridViewClickAdapter(Context context, List<String> imageInfo) {
            super(context, imageInfo);
        }

        @Override
        protected void onImageItemClick(Context context, NineGridView nineGridView, int index, List imageInfo) {
            super.onImageItemClick(context, nineGridView, index, imageInfo);
            if (TextUtils.isEmpty(contentInfo.getVod_url())) {
                imageBrower(index, contentInfo.getImg_url());
            } else {
                playVideo(contentInfo.getVod_url());
            }
        }

    }
}
