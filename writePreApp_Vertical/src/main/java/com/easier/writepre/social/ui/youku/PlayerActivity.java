
package com.easier.writepre.social.ui.youku;

import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.adapter.SjVodRemarkQueryAdapter;
import com.easier.writepre.adapter.VideoChapterPopAdapter;
import com.easier.writepre.entity.SjVodRemarkQueryInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.SjVodRemarkDelParams;
import com.easier.writepre.param.SjVodRemarkParams;
import com.easier.writepre.param.SjVodRemarkQueryParams;
import com.easier.writepre.param.YoukuVideoParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.SjVodRemarkDelResponse;
import com.easier.writepre.response.SjVodRemarkQueryResponse;
import com.easier.writepre.response.SjVodRemarkQueryResponse.SjVodRemarkQueryBody;
import com.easier.writepre.response.SjVodRemarkResponse;
import com.easier.writepre.response.YoukuVodListResponse;
import com.easier.writepre.response.YoukuVodListResponse.YoukuVodBody;
import com.easier.writepre.response.YoukuVodListResponse.YoukuVodInfo;
import com.easier.writepre.service.MusicService;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshBase.Mode;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnRefreshListener2;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.ui.BaseActivity;
import com.easier.writepre.ui.NoSwipeBackActivity;
import com.easier.writepre.utils.LoginUtil;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.youku.player.base.YoukuBasePlayerManager;
import com.youku.player.base.YoukuPlayer;
import com.youku.player.base.YoukuPlayerView;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 全屏视频播放
 * 
 */
@SuppressLint("InflateParams")
public class PlayerActivity extends NoSwipeBackActivity implements
		OnItemClickListener, OnItemLongClickListener {

	private String group_id;

	private String vid; // 需要播放的视频id

	private String _id = "";

	private YoukuPlayerView mYoukuPlayerView; // 播放器控件

	private YoukuBasePlayerManager basePlayerManager;

	private YoukuPlayer youkuPlayer; // YoukuPlayer实例，进行视频播放控制

	private PullToRefreshListView lv_comment; // 评论列表

	private List<SjVodRemarkQueryInfo> remark_list;

	private SjVodRemarkQueryAdapter remark_adapter;

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

	private View headerView; // 列表头部

	private LinearLayout header_ll;

	private View customPopView;

	private int screenHeight;

	private int popHeight;

	private List<YoukuVodInfo> list;

	private TextView tv_video_name, tv_uptime;

	private ArrayList<String> arrayList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.youku_player);

		screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();

		// 通过上个页面传递过来的Intent获取播放参数
		getIntentData(getIntent());
		iniView();

		basePlayerManager = new YoukuBasePlayerManager(this) {

			@Override
			public void setPadHorizontalLayout() {
			}

			@Override
			public void onInitializationSuccess(YoukuPlayer player) {
				// 初始化成功后需要添加该行代码
				addPlugins();

				// 实例化YoukuPlayer实例
				youkuPlayer = player;

				// 进行播放
				goPlay();
			}

			@Override
			public void onSmallscreenListener() {
				findViewById(R.id.rl_bottom).setVisibility(View.VISIBLE);
			}

			@Override
			public void onFullscreenListener() {
				findViewById(R.id.rl_bottom).setVisibility(View.GONE);
			}
		};
		basePlayerManager.onCreate();

		if (TextUtils.isEmpty(_id)) {
			ToastUtil.show("请求视频资源失败~");
		} else {
			requestData();
		}

		// 播放器控件
		mYoukuPlayerView = (YoukuPlayerView) this
				.findViewById(R.id.full_holder);
		// 控制竖屏和全屏时候的布局参数。这两句必填。
		mYoukuPlayerView
				.setSmallScreenLayoutParams(new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT));
		mYoukuPlayerView
				.setFullScreenLayoutParams(new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT,
						RelativeLayout.LayoutParams.MATCH_PARENT));
		// 初始化播放器相关数据
		mYoukuPlayerView.initialize(basePlayerManager);

	}

	private void requestData() {
		YoukuVideoParams ykvp = new YoukuVideoParams(_id);
		ykvp.setFlag(2);
		RequestManager
				.request(this, ykvp, YoukuVodListResponse.class, this, SPUtils
						.instance().getSocialPropEntity()
						.getApp_socail_server());
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			popHeight = screenHeight - mYoukuPlayerView.getHeight();
		}
	}

	@SuppressWarnings({ "deprecation", "unchecked", "rawtypes" })
	private void iniView() {

		list = new ArrayList<YoukuVodInfo>();

		remark_list = new ArrayList<SjVodRemarkQueryInfo>();

		handler = new Handler(Looper.getMainLooper());

		lv_comment = (PullToRefreshListView) findViewById(R.id.lv_comment);

		et_input_comment = (EditText) findViewById(R.id.et_input_comment);

		btn_send_comment = (Button) findViewById(R.id.btn_send_comment);
		btn_send_comment.setOnClickListener(this);
		lv_comment.setMode(Mode.PULL_UP_TO_REFRESH);

		headerView = LayoutInflater.from(this).inflate(
				R.layout.youku_comment_list_header, null);
		header_ll = (LinearLayout) headerView.findViewById(R.id.header_ll);

		tv_video_name = (TextView) headerView.findViewById(R.id.tv_video_name);

		tv_uptime = (TextView) headerView.findViewById(R.id.tv_uptime);

		tv_video_name.setText(getIntent().getStringExtra("pk_title"));

		tv_uptime.setText("更新时间为:" + getIntent().getStringExtra("pk_time"));

		headerView.findViewById(R.id.all_ll).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						if (popupwindow != null && popupwindow.isShowing()) {
							popupwindow.dismiss();
							return;
						} else {
							popView();
							popupwindow.showAsDropDown(findViewById(R.id.line_v));
						}
					}
				});

		lv_comment.getRefreshableView().addHeaderView(headerView);

		RequestManager.request(this,
				new SjVodRemarkQueryParams(_id, "9", "20"),
				SjVodRemarkQueryResponse.class, this, SPUtils.instance()
						.getSocialPropEntity().getApp_socail_server());
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
		// dlgLoad.loading();
		if (remark_list != null) {
			remark_list.clear();
			remark_adapter = null;
		}
		RequestManager.request(this,
				new SjVodRemarkQueryParams(_id, "9", "20"),
				SjVodRemarkQueryResponse.class, this, SPUtils.instance()
						.getSocialPropEntity().getApp_socail_server());
	}

	protected void loadOlds() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				stopRefresh();
				if (remark_list != null && remark_list.size() > 0) {
					requestMoreDate(remark_list.get(remark_list.size() - 1)
							.get_id());
				}
			}
		}, 300);
	}

	protected void stopRefresh() {
		lv_comment.onRefreshComplete();
	}

	public void requestMoreDate(String id) {
		RequestManager.request(this, new SjVodRemarkQueryParams(_id, id, "20"),
				SjVodRemarkQueryResponse.class, this, SPUtils.instance()
						.getSocialPropEntity().getApp_socail_server());

	}

	/**
	 * 填充章节
	 */
	public void fillView() {

		for (int i = 0; i < list.size(); i++) {

			final int index = i;

			View coupon_home_ad_item = LayoutInflater.from(this).inflate(
					R.layout.youku_video_chapter_item, null);

			LinearLayout linearLayout = (LinearLayout) coupon_home_ad_item
					.findViewById(R.id.linear_stroke);

			if (i == 0) {
				linearLayout.setBackgroundResource(R.drawable.stroke_red);
			} else {
				linearLayout.setBackgroundResource(R.drawable.stroke_grey);
			}

			TextView tv_chapter = (TextView) coupon_home_ad_item
					.findViewById(R.id.tv_chapter);
			tv_chapter.setText(list.get(i).getTitle());

			TextView tv_calligraphy_name = (TextView) coupon_home_ad_item
					.findViewById(R.id.tv_calligraphy_name);
			tv_calligraphy_name.setText(list.get(i).getDesc());

			coupon_home_ad_item.setOnClickListener(new OnClickListener() {// 每个item的点击事件加在这里

						@Override
						public void onClick(View v) {
							setSelector(index);
							vid = list.get(index).getYk_video_id();
							// tv_video_name.setText(list.get(index).getTitle());
							// tv_uptime.setText("更新时间为:"
							// + DateKit.StrToYMD(list.get(index)
							// .getCtime()));
							goPlay();
						}
					});

			header_ll.addView(coupon_home_ad_item);

		}

	}

	/**
	 * 
	 * 章节选中状态
	 */
	public void setSelector(int id) {
		for (int i = 0; i < list.size(); i++) {
			if (id == i) {
				header_ll.getChildAt(i).findViewById(R.id.linear_stroke)
						.setBackgroundResource(R.drawable.stroke_red);

			} else {
				header_ll.getChildAt(i).findViewById(R.id.linear_stroke)
						.setBackgroundResource(R.drawable.stroke_grey);
			}
		}
	}

	@Override
	public void onBackPressed() { // android系统调用
		super.onBackPressed();
		basePlayerManager.onBackPressed();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		basePlayerManager.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		basePlayerManager.onDestroy();
		Intent i = new Intent();
		i.setAction(MusicService.MUSIC_PLAYER_ACTION_RESTART);
		sendBroadcast(i);
	}

	@Override
	public void onLowMemory() { // android系统调用
		super.onLowMemory();
		basePlayerManager.onLowMemory();
	}

	@Override
	protected void onPause() {
		super.onPause();
		basePlayerManager.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		basePlayerManager.onResume();
	}

	@Override
	public boolean onSearchRequested() { // android系统调用
		return basePlayerManager.onSearchRequested();
	}

	@Override
	protected void onStart() {
		super.onStart();
		basePlayerManager.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		basePlayerManager.onStop();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		getIntentData(intent); // 通过Intent获取播放需要的相关参数
		goPlay(); // 进行播放
	}

	/**
	 * 获取上个页面传递过来的数据
	 */
	private void getIntentData(Intent intent) {
		if (intent != null) {
			_id = intent.getStringExtra("_id");
		}

	}

	private void goPlay() {
		Intent i = new Intent();
		i.setAction(MusicService.MUSIC_PLAYER_ACTION_PAUSE);
		sendBroadcast(i);
		youkuPlayer.playVideo(vid); // 播放在线视频
	}

	private PopupWindow popupwindow;

	private GridView chapterGV;

	private VideoChapterPopAdapter chapterAdapter; // 章节适配器

	// private ArrayList<String> chapterList; // 章节名字

	private int chapterIndex; // pop选中章节下标

	/**
	 * 弹出pop窗口 进行章节选择
	 */
	public void popView() {
		customPopView = getLayoutInflater().inflate(R.layout.youku_chapter_pop,
				null, false);
		popupwindow = new PopupWindow(customPopView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		popupwindow.setAnimationStyle(R.style.PopUpBottom);

		customPopView.findViewById(R.id.iv_dismiss_pop).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						popupwindow.dismiss();
					}
				});

		chapterGV = (GridView) customPopView
				.findViewById(R.id.youku_chapter_gv);

		chapterAdapter = new VideoChapterPopAdapter(this, list, chapterIndex);
		chapterGV.setAdapter(chapterAdapter);

		chapterGV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				chapterAdapter.clearSelection(position);
				chapterAdapter.notifyDataSetChanged();
				vid = list.get(position).getYk_video_id();
				// tv_video_name.setText(list.get(position).getTitle());
				// tv_uptime.setText("更新时间为:"
				// + DateKit.StrToYMD(list.get(position).getCtime()));
				goPlay();
				popupwindow.dismiss();
				chapterIndex = position;
			}
		});
	}

	@Override
	public void onResponse(BaseResponse response) {
		// dlgLoad.dismissDialog();
		if ("0".equals(response.getResCode())) {
			if (response instanceof YoukuVodListResponse) {
				YoukuVodListResponse ykvlResponse = (YoukuVodListResponse) response;
				YoukuVodBody body = ykvlResponse.getRepBody();

				for (int i = 0; i < body.getList().size(); i++) {
					list.add(body.getList().get(i));
				}

				if (list.size() > 0) {
					// tv_video_name.setText(list.get(0).getTitle());
					// tv_uptime.setText("更新时间为:"
					// + DateKit.StrToYMD(list.get(0).getCtime()));
					vid = list.get(0).getYk_video_id();
					goPlay();
				}

				fillView();
			} else if (response instanceof SjVodRemarkQueryResponse) {
				SjVodRemarkQueryResponse svrqResponse = (SjVodRemarkQueryResponse) response;
				SjVodRemarkQueryBody body = svrqResponse.getRepBody();
				for (int i = 0; i < body.getList().size(); i++) {
					remark_list.add(body.getList().get(i));
				}
				if (remark_adapter == null) {
					remark_adapter = new SjVodRemarkQueryAdapter(remark_list,
							this);
					lv_comment.setAdapter(remark_adapter);
				} else {
					notifyAdpterdataChanged();
				}
			} else if (response instanceof SjVodRemarkResponse) {
				ToastUtil.show("评论成功");
				et_input_comment.setText("");
				et_input_comment.setHint("对这个帖子说点什么吧");
				isReply = false;

				// dlgLoad.loading();
				if (remark_list != null) {
					remark_list.clear();
					remark_adapter = null;
				}
				RequestManager.request(this, new SjVodRemarkQueryParams(_id,
						"9", "20"), SjVodRemarkQueryResponse.class, this,
						SPUtils.instance().getSocialPropEntity()
								.getApp_socail_server());
			} else if (response instanceof SjVodRemarkDelResponse) {
				ToastUtil.show("删除成功");
				if (remark_adapter != null) {
					remark_list.remove(positionDel);
					remark_adapter.notifyDataSetChanged();
				}
			}
		} else {
			ToastUtil.show(response.getResMsg());
		}
	}

	private void notifyAdpterdataChanged() {
		if (remark_adapter != null) {
			remark_adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_send_comment:
			if (LoginUtil.checkLogin(PlayerActivity.this)) {
				deleteflag = "0";
				if (!isReply) { // 新增
					String commentContent = et_input_comment.getText()
							.toString();
					if (TextUtils.isEmpty(commentContent)) {
						ToastUtil.show("说点什么吧");
						return;
					}
					// commentContent.replaceAll("//s+g", "");
					// dlgLoad.loading();
					RequestManager.request(this, new SjVodRemarkParams(_id, "",
							"", commentContent), SjVodRemarkResponse.class,
							this, SPUtils.instance().getSocialPropEntity()
									.getApp_socail_server());
				} else { // 回复
					String commentContent = et_input_comment.getText()
							.toString();
					// dlgLoad.loading();

					RequestManager.request(this,
							new SjVodRemarkParams(_id, reply_to_commentId,
									reply_to_userId, commentContent),
							SjVodRemarkResponse.class, this, SPUtils.instance()
									.getSocialPropEntity()
									.getApp_socail_server());
				}
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		SjVodRemarkQueryInfo svrqi = remark_list.get(position - 2);
		String username = svrqi.getUname();
		reply_to_commentId = svrqi.get_id();
		reply_to_userId = svrqi.getUser_id();
		clickNum++;
		if (clickNum % 2 == 0) {
			et_input_comment.setHint("对这个帖子说点什么吧");
			isReply = false;
		} else {
			et_input_comment.setHint("回复@" + username);
			isReply = true;
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		SjVodRemarkQueryInfo svrqi = remark_list.get(position - 2);
		String username = svrqi.getUname();
		reply_to_userId = svrqi.getUser_id();
		reply_to_commentId = svrqi.get_id();
		comment_title = svrqi.getTitle();
		// et_input_comment.setHint("回复@" + username);
		if (LoginUtil.checkLogin(PlayerActivity.this)) {
			myId = SPUtils.instance().getLoginEntity().get_id();
			showPopupWindow(view);
			positionDel = (position - 2);
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
					RequestManager.request(PlayerActivity.this,
							new SjVodRemarkDelParams(reply_to_commentId),
							SjVodRemarkDelResponse.class, PlayerActivity.this,
							SPUtils.instance().getSocialPropEntity()
									.getApp_socail_server());
					deleteflag = "1";

				} else {
					copy(comment_title, PlayerActivity.this);
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
