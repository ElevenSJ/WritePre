package com.easier.writepre.ui;

import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.adapter.PkNewsAdapter;
import com.easier.writepre.entity.PkNewsInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.PkNewsParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.PkNewsResponse;
import com.easier.writepre.response.PkNewsResponse.PkNewsBody;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnRefreshListener2;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

public class PkNewsActivity extends BaseActivity {
	
	private ImageView img_back;
	
	private PullToRefreshListView lv_news;//新闻列表
	
	private List<PkNewsInfo> list;
	
	private PkNewsAdapter adapter;
	
	private String pk_id;
	
	private Handler handler;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_pk_news);
		init();
	}

	private void init() {
		handler = new Handler(Looper.getMainLooper());
		list = new ArrayList<PkNewsInfo>();
		img_back = (ImageView) findViewById(R.id.img_back);
		lv_news = (PullToRefreshListView) findViewById(R.id.lv_news);
		
		img_back.setOnClickListener(this);
		lv_news.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				PkNewsInfo pni= list.get(position - 1);
				Intent intent = new Intent(PkNewsActivity.this,WebViewActivity.class);
				String url = pni.getUrl();
				intent.putExtra("url", url);
				intent.putExtra("title", "会事内容");
				startActivity(intent);
//				Intent intent = new Intent(PkNewsActivity.this,PkWorksManageActivity.class);
//				startActivity(intent);
			}
			
		});
		lv_news.setScrollingWhileRefreshingEnabled(true);
		lv_news.setOnRefreshListener(new OnRefreshListener2<ListView>() {
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
		Intent intent = getIntent();
		pk_id = intent.getStringExtra("pk_id");
		dlgLoad.loading();
		RequestManager.request(this,new PkNewsParams(pk_id, "9", "20"),
						PkNewsResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_socail_server());
		
	}
	protected void refresh(){
		dlgLoad.loading();
		if (list != null) {
			list.clear();
			adapter = null;
		}
		RequestManager.request(this,new PkNewsParams(pk_id,"9","20"),
				PkNewsResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_socail_server());
	}
	
	protected void loadNews() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				stopRefresh();
				refresh();
			}	
		},300);
		
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
		lv_news.onRefreshComplete();
	}
	public void requestMoreDate(String id) {
		RequestManager.request(this,new PkNewsParams(pk_id, id, "20"),
				PkNewsResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_socail_server());

	}

	@Override
	public void onResponse(BaseResponse response) {
		// TODO Auto-generated method stub
		dlgLoad.dismissDialog();
		if (response == null) {
			return;
		}
		if ("0".equals(response.getResCode())) {

			if (response instanceof PkNewsResponse) {
				PkNewsResponse mPkNewsResponse = (PkNewsResponse) response;
				PkNewsBody mPkNewsBody = mPkNewsResponse.getRepBody();
				for (int i = 0; i < mPkNewsBody.getList().size(); i++) {
					list.add(mPkNewsBody.getList().get(i));
				}
				if (adapter == null) {
					adapter = new PkNewsAdapter(list, this);
					lv_news.setAdapter(adapter);
				}else{
					notifyAdpterdataChanged();
				}
			}
		}else{
			ToastUtil.show(response.getResCode());
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
