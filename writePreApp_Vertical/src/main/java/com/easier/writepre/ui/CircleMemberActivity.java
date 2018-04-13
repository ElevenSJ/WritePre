package com.easier.writepre.ui;

import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.adapter.CircleMemberAdapter;
import com.easier.writepre.entity.CircleMember;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.CircleMemberParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CircleMemberResponse;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnRefreshListener2;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.RoundImageView;
import com.sj.autolayout.utils.AutoUtils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 圈子成员
 */
public class CircleMemberActivity extends BaseActivity {
    private final int REQUEST_ERROR = -1;

    private final int REQUEST_CIRCLE_MEMBER_SUCCESS = 1;

    private String circle_id;

    private PullToRefreshListView listView;
    private CircleMemberAdapter adapter;

    private List<CircleMember> memList = new ArrayList<CircleMember>();

    private RoundImageView ImgIcon;
    private TextView TvName;
    private TextView Tvtag;
    private Button bt;
    private CircleMember circleMember;
    public static int LIST_INDEX;
    public final static int REQUEST_UPDATE_CODE = 999;
    private PopupWindow popupWindow;
    private String role;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REQUEST_CIRCLE_MEMBER_SUCCESS:
                    CircleMemberResponse member = (CircleMemberResponse) msg.obj;
                    if (member != null) {
                        updateMember(member.getRepBody().getList());
                    }
                    break;
                case REQUEST_ERROR:
                    ToastUtil.show(((BaseResponse) msg.obj).getResMsg());
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_member);
        circle_id = getIntent().getStringExtra("circle_id");
        role = getIntent().getStringExtra("role");
        initView();
        getCircleMember("isNew","9");
    }

    private void initView() {
        setTopTitle("圈子成员");

        listView = (PullToRefreshListView) findViewById(R.id.listView);
        View listViewHead = getLayoutInflater().inflate(
                R.layout.circle_member_head, null);
        AutoUtils.autoSize(listViewHead);
        listView.getRefreshableView().addHeaderView(listViewHead);
        adapter = new CircleMemberAdapter(this, 1);
        listView.setAdapter(adapter);

        ImgIcon = (RoundImageView) listViewHead.findViewById(R.id.imgHead);
        TvName = (TextView) listViewHead.findViewById(R.id.tv_name);
        Tvtag = (TextView) listViewHead.findViewById(R.id.tv_tag);
        bt = (Button) listViewHead.findViewById(R.id.bt);

//        initPopupWindow();
        bt.setVisibility(View.GONE);
        Tvtag.setVisibility(View.VISIBLE);
        ImgIcon.setOnClickListener(this);
        TvName.setOnClickListener(this);
        listViewHead.setOnClickListener(this);

        listView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

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

        listView.getRefreshableView().setOnItemClickListener(
                new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if (position < 2) {
                            return;
                        }
                        toUserDetail(((CircleMember) adapter
                                .getItem(position - 2)).getUser_id());
                    }
                });
//        listView.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                popupWindow.showAsDropDown(view, 300, 0);
//                return true;
//            }
//        });
    }

//    private void initPopupWindow() {
//        View convertView = LayoutInflater.from(
//                CircleMemberActivity.this).inflate(
//                R.layout.pop_circle_manage_action, null);
//        popupWindow = new PopupWindow(convertView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        convertView.findViewById(R.id.btn_1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ToastUtil.show("修改昵称");
//            }
//        });
//        convertView.findViewById(R.id.btn_2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ToastUtil.show("设置管理员");
//            }
//        });
//        convertView.findViewById(R.id.btn_3).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ToastUtil.show("删除");
//            }
//        });
//    }

    /**
     * 下拉刷新数据
     */
    private void loadNews() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.onRefreshComplete();
                getCircleMember("isNew","9");
            }
        }, 300);
    }

    /**
     * 加载更多
     */
    protected void loadOlds() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.onRefreshComplete();
                if (memList != null && memList.size() > 0) {
                    getCircleMember("isOld",memList.get(memList.size() - 1).get_id());
                }
            }
        }, 300);
    }

    protected void updateMember(List<CircleMember> list) {
        if (list == null) {
            return;
        }
        memList.addAll(list);
        for (int i = 0; i < memList.size(); i++) {
            if (memList.get(i).getOwner_id().equals(memList.get(i).getUser_id())) {
                circleMember = memList.get(i);
                break;
            }
        }
        updateHeadView(circleMember);
        memList.remove(circleMember);

        adapter.setData(1, memList, role.equals("0"));

    }

    private void updateHeadView(CircleMember circleMember) {

        ImgIcon.setImageView(
                StringUtil.getHeadUrl(circleMember.getHead_img()));
        ImgIcon.setIconView(circleMember.getIs_teacher());
        // BitmapHelp.getBitmapUtils().display(ImgIcon,
        // StringUtil.getHeadUrl(circleMember.getHead_img()),new
        // BitmapLoadCallBack<View>() {
        //
        // @Override
        // public void onLoadCompleted(View arg0, String arg1, Bitmap arg2,
        // BitmapDisplayConfig arg3,
        // BitmapLoadFrom arg4) {
        // ImgIcon.setImageBitmap(arg2);
        //
        // }
        //
        // @Override
        // public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
        // ImgIcon.setImageResource(R.drawable.empty_head);
        //
        // }
        // });
        TvName.setText(circleMember.getUname());
    }

    private void getCircleMember(String tag,String lastId) {
        RequestManager.request(this, tag,new CircleMemberParams(circle_id, lastId,
                20), CircleMemberResponse.class, this, SPUtils.instance()
                .getSocialPropEntity().getApp_socail_server());
    }

    @Override
    public void onTopRightClick(View v) {
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.imgHead:
            case R.id.tv_name:
            case R.id.member_layout:
                if (circleMember != null) {
                    toUserDetail(circleMember.getUser_id());
                }
                break;
            default:
                break;
        }

    }

    /**
     * 查看用户详情
     */
    public void toUserDetail(String userId) {
        Intent intent = new Intent(this, UserInfoActivity.class);
        intent.putExtra("user_id", userId);
        startActivity(intent);
    }

    @Override
    public void onResponse(String tag,BaseResponse response) {
        if ("0".equals(response.getResCode())) {
            if (response instanceof CircleMemberResponse) {
                if (tag.equals("isNew")){
                    memList.clear();
                }
                Message message = new Message();
                message.what = REQUEST_CIRCLE_MEMBER_SUCCESS;
                message.obj = response;
                handler.sendMessage(message);
            }
        } else {
            Message message = new Message();
            message.what = REQUEST_ERROR;
            message.obj = response;
            handler.sendMessage(message);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_UPDATE_CODE:
                if (resultCode == RESULT_OK) {
                    String circleName = data.getStringExtra("name");
                    memList.get(LIST_INDEX).setUname(circleName);
                    adapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }

    }
}
