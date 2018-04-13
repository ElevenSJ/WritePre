package com.easier.writepre.social.ui.youku;

import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.adapter.PkVideoListAdapter;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.YoukuVideoParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.YoukuVodgpListResponse;
import com.easier.writepre.response.YoukuVodgpListResponse.YoukuVodgpBody;
import com.easier.writepre.response.YoukuVodgpListResponse.YoukuVodgpInfo;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnRefreshListener2;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.ui.BaseActivity;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.sj.autolayout.utils.DateKit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * 视频列表
 * 
 * @author kai.zhong
 * 
 */
public class VideoListActivity extends BaseActivity {

	private Handler handler;

	private ImageView img_back;

	private List<YoukuVodgpInfo> list;

	private PkVideoListAdapter adapter;

	private PullToRefreshListView lv_video;// 视频列表

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.youku_video_list);
		init();
		requestData("9");
	}

	private void init() {
		setTopTitle("大会视频");
		handler = new Handler(Looper.getMainLooper());
		list = new ArrayList<YoukuVodgpInfo>();
		img_back = (ImageView) findViewById(R.id.img_back);
		lv_video = (PullToRefreshListView) findViewById(R.id.lv_video);
		img_back.setOnClickListener(this);
		lv_video.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent(VideoListActivity.this, PlayerActivity.class);
				i.putExtra("_id", list.get(position - 1).get_id());
				i.putExtra("pk_title", list.get(position - 1).getTitle());
				if (TextUtils.isEmpty(list.get(position - 1).getUptime())) {
					i.putExtra("pk_time", DateKit.StrToYMD(list.get(position - 1).getCtime()));
				} else {
					i.putExtra("pk_time", DateKit.StrToYMD(list.get(position - 1).getUptime()));
				}

				startActivity(i);
			}

		});

		lv_video.setOnRefreshListener(new OnRefreshListener2<ListView>() {
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

	}

	private void requestData(String last_id) {
		YoukuVideoParams ykvp = new YoukuVideoParams(last_id, "10");
		ykvp.setFlag(1);
		RequestManager.request(this,ykvp, YoukuVodgpListResponse.class, this,
				SPUtils.instance().getSocialPropEntity().getApp_socail_server());
	}

	protected void loadNews() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				stopRefresh();
				list.clear();
				requestData("9");
			}
		}, 300);

	}

	protected void loadOlds() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				stopRefresh();
				if (list != null && list.size() > 0) {
					requestData(list.get(list.size() - 1).get_id());
				}
			}
		}, 300);
	}

	protected void stopRefresh() {
		lv_video.onRefreshComplete();
	}

	@Override
	public void onResponse(BaseResponse response) {
		if ("0".equals(response.getResCode())) {
			if (response instanceof YoukuVodgpListResponse) {
				YoukuVodgpListResponse ykvlResponse = (YoukuVodgpListResponse) response;
				YoukuVodgpBody body = ykvlResponse.getRepBody();
				for (int i = 0; i < body.getList().size(); i++) {
					list.add(body.getList().get(i));
				}
				if (adapter == null) {
					adapter = new PkVideoListAdapter(list, this);
					lv_video.setAdapter(adapter);
				} else {
					notifyAdpterdataChanged();
				}
			}
		} else {
			ToastUtil.show(response.getResMsg());
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
		case R.id.img_back:
			onTopLeftClick(v);
			break;

		default:
			break;
		}
	}

}
