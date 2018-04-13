package com.easier.writepre.fragment;

import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.adapter.CircleAllListAdapter;
import com.easier.writepre.adapter.CircleAllListAdapter.MsgViewHolder;
import com.easier.writepre.entity.CircleMsgInfo;
import com.easier.writepre.entity.ContentInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.mainview.SocialMainView;
import com.easier.writepre.param.MyCirclePostParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.CircleMsgResponse;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.social.refreash.PullToRefreshBase.Mode;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnRefreshListener2;
import com.easier.writepre.ui.CircleMsgListActivity;
import com.easier.writepre.ui.NoSwipeBackActivity;
import com.easier.writepre.ui.CircleMsgDetailActivity;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 我的圈子发帖
 * 
 * @author zhaomaohan
 * 
 */
public class MyCirclrPostFragment extends BaseFragment implements
		OnRefreshListener2<ListView> {
	private PullToRefreshListView listView;

	private List<CircleMsgInfo> list;

	private CircleAllListAdapter adapter;

	private Handler mHandler = new Handler();

	@Override
	public void onClick(View v) {
	}

	@Override
	public int getContextView() {
		return R.layout.fragment_my_circle_post;
	}

	@Override
	protected void init() {
		listView = (PullToRefreshListView) findViewById(R.id.listview);
//		listView.setMode(Mode.PULL_FROM_END);

		// 数据
		list = new ArrayList<CircleMsgInfo>();

		adapter = new CircleAllListAdapter( getActivity(),listView.getRefreshableView());

		listView.setAdapter(adapter);

		adapter.setMsgData(0, list);

		listView.setOnRefreshListener(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position < 1) {
					return;
				}
				adapter.setSelectedIndex(position - 1);
				CircleMsgInfo msgInfo = (CircleMsgInfo) adapter
						.getItem(position - 1);
				int type = adapter.getItemViewType(position - 1);
				if (type == 0) {
					MsgViewHolder msgViewHolder = (MsgViewHolder) view.getTag();
					msgViewHolder.tv_readCount.setTag(position - 1);
					adapter.requestReadAdd(
							msgInfo.getCircle_id(), msgInfo.get_id());
				}
				Intent intent = new Intent(getActivity(),
						CircleMsgDetailActivity.class);
				intent.putExtra("data", msgInfo);
				startActivityForResult(intent, CircleMsgDetailActivity.DETAIL_CODE);
			}
		});
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && isPrepared) {
			list.clear();
			listViewLoadData("9");
		}
	}

	/**
	 * 首次获取数据
	 */
	protected void listViewLoadData(String lastId) {
		RequestManager.request(getActivity(), new MyCirclePostParams(lastId,
				"20"), CircleMsgResponse.class, this, SPUtils.instance()
				.getSocialPropEntity().getApp_socail_server());
	}

	@Override
	public void onResponse(BaseResponse response) {
		if ("0".equals(response.getResCode())) {
			if (response instanceof CircleMsgResponse) {
				CircleMsgResponse gscrResult = (CircleMsgResponse) response;
				if (gscrResult != null) {
					CircleMsgResponse.Repbody rBody = gscrResult.getRepBody();
					if (rBody != null) {
						if (rBody.getList() != null) {
							if (rBody.getList().isEmpty()) {
								NoSwipeBackActivity
										.setListLabel(listView, true);
							} else {
								list.addAll(rBody.getList());
							}
						}
						adapter.setMsgData(0, list);
					}
				}
			}
		} else {
			ToastUtil.show(response.getResMsg());
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadNews();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadOlds();
	}

	/**
	 * 下拉获取数据
	 */
	private void loadNews() {
		NoSwipeBackActivity.setListLabel(listView, false);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				listView.onRefreshComplete();
				list.clear();
				listViewLoadData("9");
			}
		}, 300);
	}
	/**
	 * 加载更多
	 */
	protected void loadOlds() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				listView.onRefreshComplete();
				if (list != null && list.size() > 0) {
					listViewLoadData(adapter.getMsgData()
							.get(adapter.getCount() - 1).get_id());
				}
			}
		}, 300);
	}
	
//	@Override
//	public void onResume() {
//		super.onResume();
//		loadNews();
//	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) if (CircleMsgDetailActivity.DETAIL_CODE == requestCode) {
			if (getActivity().RESULT_OK == resultCode){
				adapter.replace(adapter.getSelectedIndex(),data.getSerializableExtra("data"));
			}else if (getActivity().RESULT_CANCELED == resultCode){
				adapter.remove(adapter.getSelectedIndex());
			}
		}
	}
}
