package com.easier.writepre.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.adapter.PkWorksRemarkQueryAdapter;
import com.easier.writepre.entity.PkWorksRemarkQueryInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.inputmessage.RecordButton;
import com.easier.writepre.param.PkWorksRemarkDelParams;
import com.easier.writepre.param.PkWorksRemarkParams;
import com.easier.writepre.param.PkWorksRemarkQueryParams;
import com.easier.writepre.param.TopicPostCommentParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.PkWorksRemarkQueryResponse;
import com.easier.writepre.response.PkWorksRemarkQueryResponse.PkWorksRemarkQueryBody;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnRefreshListener2;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.utils.DeviceUtils;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class PkWorksRemarkActivity extends BaseActivity implements OnItemClickListener,
        OnItemLongClickListener {

    private PullToRefreshListView lv_comment;
    private String works_id;
    private List<PkWorksRemarkQueryInfo> list;
    private PkWorksRemarkQueryAdapter adapter;
    // private EditText et_input_comment;
    private Handler handler;
    private EditText et_input_comment;
    private Button btn_send_comment;
    private String reply_to_commentId;// 回复哪条评论的id
    private String reply_to_userId;// 回复哪个用户的id
    private Boolean isReply = false;// 是否回复状态，用来判断发送评论是新增还是回复。true_回复，false_评论
    private int positionDel;// 删除评论的list的位置
    private String myId;
    private String deleteflag = "0"; // 1为删除 0为评论
    protected String comment_title;
    protected int clickNum = 0;// 评论item的点击次数
    private Button pop_btn_delete;
    protected PopupWindow popupWindow;
    private ImageView img_back;
    private ImageView iv_input_type;//输入方式 0 文本模式  1语音模式
    private RecordButton btn_voice_send;
    private RelativeLayout rel_voice_input, rel_text_input;
    private HashMap<String, String> voiceMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_pk_works_remark);
        init();
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
                    if (LoginUtil.checkLogin(PkWorksRemarkActivity.this)) {
                        deleteflag = "0";
                        if (!isReply) { // 新增
                            RequestManager.request(PkWorksRemarkActivity.this, new PkWorksRemarkParams(works_id, "", "",
                                            "", voiceOssPath, voiceMap.get(voiceFilePath)), BaseResponse.class, PkWorksRemarkActivity.this,
                                    SPUtils.instance().getSocialPropEntity().getApp_socail_server());
                        } else { // 回复
                            RequestManager.request(PkWorksRemarkActivity.this, new PkWorksRemarkParams(works_id,
                                            reply_to_commentId, reply_to_userId, "", voiceOssPath, voiceMap.get(voiceFilePath)),
                                    BaseResponse.class, PkWorksRemarkActivity.this, SPUtils.instance().getSocialPropEntity().getApp_socail_server());
                        }
                    }
                    break;
            }
        }
    };

    private void init() {

        handler = new Handler(Looper.getMainLooper());
        list = new ArrayList<PkWorksRemarkQueryInfo>();

        lv_comment = (PullToRefreshListView) findViewById(R.id.lv_comment);
        et_input_comment = (EditText) findViewById(R.id.et_input_comment);
        btn_send_comment = (Button) findViewById(R.id.btn_send_comment);
        img_back = (ImageView) findViewById(R.id.img_back);
        iv_input_type = (ImageView) findViewById(R.id.iv_input_type);
        btn_voice_send = (RecordButton) findViewById(R.id.btn_voice_send);
        rel_voice_input = (RelativeLayout) findViewById(R.id.rel_vioce_input);
        rel_text_input = (RelativeLayout) findViewById(R.id.rel_text_input);
        iv_input_type.setOnClickListener(this);
        btn_voice_send.setRecorderCallback(new RecordButton.OnFinishedRecordListener() {
            @Override
            public void onFinishedRecord(String audioPath, int length) {
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

        btn_send_comment.setOnClickListener(this);
        img_back.setOnClickListener(this);
        Intent intent = getIntent();
        works_id = intent.getStringExtra("works_id");
        dlgLoad.loading();
        RequestManager.request(this, new PkWorksRemarkQueryParams(works_id, "9", "20"),
                PkWorksRemarkQueryResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_socail_server());

        lv_comment.setScrollingWhileRefreshingEnabled(true);
        lv_comment.setOnRefreshListener(new OnRefreshListener2<ListView>() {
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
        lv_comment.setOnItemClickListener(this);
        lv_comment.getRefreshableView().setOnItemLongClickListener(this);
    }

    protected void loadNews() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopRefresh();
                requestNewData();
            }
        }, 300);
    }

    protected void requestNewData() {
        dlgLoad.loading();
        if (list != null) {
            list.clear();
            adapter = null;
        }
        RequestManager.request(this, new PkWorksRemarkQueryParams(works_id, "9", "20"),
                PkWorksRemarkQueryResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_socail_server());
    }

    protected void loadOlds() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopRefresh();
                if (list != null && list.size() > 0) {
                    requestMoreDate(list.get(list.size() - 1).get_id());
                }
            }
        }, 300);
    }

    protected void stopRefresh() {
        lv_comment.onRefreshComplete();
    }

    public void requestMoreDate(String id) {
        RequestManager.request(this, new PkWorksRemarkQueryParams(works_id, id, "20"),
                PkWorksRemarkQueryResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_socail_server());

    }

    @Override
    public void onResponse(BaseResponse response) {
        dlgLoad.dismissDialog();
        if ("0".equals(response.getResCode())) {
            if (response instanceof PkWorksRemarkQueryResponse) {
                PkWorksRemarkQueryResponse mPkWorksRemarkQueryResponse = (PkWorksRemarkQueryResponse) response;
                PkWorksRemarkQueryBody mPkWorksRemarkQueryBody = mPkWorksRemarkQueryResponse
                        .getRepBody();
                for (int i = 0; i < mPkWorksRemarkQueryBody.getList().size(); i++) {
                    list.add(mPkWorksRemarkQueryBody.getList().get(i));
                }
                if (adapter == null) {
                    adapter = new PkWorksRemarkQueryAdapter(list, this);
                    lv_comment.setAdapter(adapter);
                } else {
                    notifyAdpterdataChanged();
                }
                // ToastUtil.show("评论列表查询成功");
            } else {
                if (deleteflag.equals("1")) {
                    ToastUtil.show("删除成功");
                    if (adapter != null) {
                        list.remove(positionDel);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    ToastUtil.show("评论成功");
                    et_input_comment.setText("");
                    et_input_comment.setHint("对这个帖子说点什么吧");
                    isReply = false;
                    PkTeacherStudentListActivity.COMMENT = true;
                    dlgLoad.loading();
                    if (list != null) {
                        list.clear();
                        adapter = null;
                    }
                    RequestManager.request(this, new PkWorksRemarkQueryParams(works_id, "9", "20"),
                            PkWorksRemarkQueryResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_socail_server());
                }
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (adapter != null) {
            adapter.stopVoice();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopVoice();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.stopVoice();
        }
    }
    private void notifyAdpterdataChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_comment:
                if (LoginUtil.checkLogin(PkWorksRemarkActivity.this)) {
                    deleteflag = "0";
                    if (!isReply) { // 新增
                        String commentContent = et_input_comment.getText()
                                .toString();
                        if (TextUtils.isEmpty(commentContent)) {
                            ToastUtil.show("说点什么吧");
                            return;
                        }
                        dlgLoad.loading();
                        RequestManager.request(this, new PkWorksRemarkParams(works_id, "", "",
                                        commentContent,"","0"), BaseResponse.class, this,
                                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
                    } else { // 回复
                        String commentContent = et_input_comment.getText()
                                .toString();
                        dlgLoad.loading();

                        RequestManager.request(this, new PkWorksRemarkParams(works_id,
                                        reply_to_commentId, reply_to_userId, commentContent,"","0"),
                                BaseResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_socail_server());
                    }
                }
                break;
            case R.id.img_back:
                onTopLeftClick(v);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        PkWorksRemarkQueryInfo pwrqi = list.get(position - 1);
        String username = pwrqi.getUname();
        reply_to_commentId = pwrqi.get_id();
        reply_to_userId = pwrqi.getUser_id();
        myId = SPUtils.instance().getLoginEntity().get_id();
        clickNum++;
        if (clickNum % 2 == 0) {
            et_input_comment.setHint("对这个帖子说点什么吧");
            isReply = false;
        } else {
            if(reply_to_userId.equals(myId))
            {
                return;
            }
            et_input_comment.setHint("回复@" + username);
            isReply = true;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {

        PkWorksRemarkQueryInfo pwrqi = list.get(position - 1);
        String username = pwrqi.getUname();
        reply_to_userId = pwrqi.getUser_id();
        reply_to_commentId = pwrqi.get_id();
        comment_title = pwrqi.getTitle();
        // et_input_comment.setHint("回复@" + username);
        if (LoginUtil.checkLogin(PkWorksRemarkActivity.this)) {
            myId = SPUtils.instance().getLoginEntity().get_id();
            showPopupWindow(view);
            positionDel = (position - 1);
        }
        return false;
    }

    private void showPopupWindow(View view) {

        // 长按评论item弹出删除或复制的popwindow
        View convertView2 = LayoutInflater.from(this).inflate(
                R.layout.pop_comments_topic, null);
        pop_btn_delete = (Button) convertView2
                .findViewById(R.id.pop_btn_delete);
        if (reply_to_userId.equals(myId)) {
            pop_btn_delete.setText("删除");
        } else {
            pop_btn_delete.setText("复制");
        }
        pop_btn_delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (pop_btn_delete.getText().equals("删除")) {
                    if (adapter != null) {
                        adapter.stopVoice();
                    }
                    RequestManager.request(PkWorksRemarkActivity.this, new PkWorksRemarkDelParams(reply_to_commentId),
                            BaseResponse.class, PkWorksRemarkActivity.this,
                            SPUtils.instance().getSocialPropEntity().getApp_socail_server());
                    deleteflag = "1";

                } else {
                    copy(comment_title, PkWorksRemarkActivity.this);
                }
                popupWindow.dismiss();
            }
        });
        popupWindow = new PopupWindow(convertView2,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        ColorDrawable cd = new ColorDrawable(0x000000);
        popupWindow.setBackgroundDrawable(cd);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        // popupWindow2.showAtLocation((View)view.getParent(),Gravity.RIGHT,
        // x, 0);
        popupWindow.showAsDropDown(view, 300, 0);
        popupWindow.update();

    }

    public static void copy(String content, Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
        ToastUtil.show("复制成功");
    }

}
