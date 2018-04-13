package com.easier.writepre.ui;

import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.adapter.CircleAllListAdapter;
import com.easier.writepre.adapter.SquareAllEssenceListAdapter;
import com.easier.writepre.adapter.SquareAllEssenceListAdapter.ViewHolder;
import com.easier.writepre.entity.CircleInfo;
import com.easier.writepre.entity.ContentInfo;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.CircleGetMyParams;
import com.easier.writepre.param.UserCircleParams;
import com.easier.writepre.param.UserSquarePostParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CircleMyResponse;
import com.easier.writepre.response.SquareContentGetResponse;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshBase.Mode;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnRefreshListener2;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class UserCircleActivity extends BaseActivity  implements OnRefreshListener2<ListView> {

	private String user_id;
	private String user_name;
	
	private PullToRefreshListView listView;
	
	private List<CircleInfo> list;

	private CircleAllListAdapter adapter;
	
	private Handler mHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_user_circle);
		init();
	}

	private void init() {
		user_id = getIntent().getStringExtra("user_id");
		user_name = getIntent().getStringExtra("user_name");
		//setTopTitle(user_name+"的圈子");
		if(user_id.equals(SPUtils.instance().getLoginEntity().get_id())){
			setTopTitle("我的圈子");
		}else{
			setTopTitle("TA的圈子");
		}

		
		listView = (PullToRefreshListView) findViewById(R.id.listview);
		
		listView.setMode(Mode.DISABLED);
		// 数据
		list = new ArrayList<CircleInfo>();
		
		adapter = new CircleAllListAdapter(this,listView.getRefreshableView());
		adapter.setCircleData(1, list);
		listView.setAdapter(adapter);

		listViewLoadData("9");

		listView.setOnRefreshListener(this);
		listView.getRefreshableView().setOnItemClickListener(
				new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						adapter.setSelectedIndex(position - 1);
						CircleInfo CircleInfo = (CircleInfo) adapter
								.getItem(position - 1);
						toCircleList(CircleInfo.get_id());
					}
				});

		listView.setOnScrollListener(new PauseOnScrollListener(BitmapHelp.getBitmapUtils(), true, true));
	}
	private void toCircleList(String id) {
		if (TextUtils.isEmpty(id)) {
			return;
		}
		Intent intent = new Intent();
		intent.setClass(this, CircleMsgListActivity.class);
		intent.putExtra("circle_id", id); // 圈子id
		startActivity(intent);
	}
	/**
	 * 首次获取数据
	 */
	protected void listViewLoadData(String lastId) {
		RequestManager.request(UserCircleActivity.this, new UserCircleParams(user_id), CircleMyResponse.class, this, SPUtils.instance()
				.getSocialPropEntity().getApp_socail_server());
//		RequestManager.request(this, new CircleGetMyParams(),
//				CircleMyResponse.class, this, SPUtils.instance()
//						.getSocialPropEntity().getApp_socail_server());
	}
	
	@Override
	public void onResponse(BaseResponse response) {
		if ("0".equals(response.getResCode())) {
			if (response instanceof CircleMyResponse) {
				CircleMyResponse myResult = (CircleMyResponse) response;
				if (myResult != null) {
					CircleMyResponse.Repbody rBody = myResult.getRepBody();
					if (rBody != null) {
						if (rBody.getList() != null) {
							list.clear();
							list.addAll(rBody.getList());
							adapter.setCircleData(1, list);
						}
					}
				}
			}
		} else {
			ToastUtil.show(response.getResMsg());
		}
	}
	
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
	}

	@Override
	protected void onPause() {
		super.onPause();
		//友盟统计
		List<String> var = new ArrayList<String>();
		var.add(YouMengType.getName(MainActivity.TYPE_FIV));
		var.add("查看圈子");
		YouMengType.onEvent(this,var,getShowTime(), user_name);
	}
}
