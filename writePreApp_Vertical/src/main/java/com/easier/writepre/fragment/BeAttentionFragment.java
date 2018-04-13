package com.easier.writepre.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.AttentionAdapter;
import com.easier.writepre.entity.AttentionInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.UserCareMeParams;
import com.easier.writepre.response.AttentionListResponse;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.social.refreash.PullToRefreshBase;
import com.easier.writepre.social.refreash.PullToRefreshBase.Mode;
import com.easier.writepre.social.refreash.PullToRefreshBase.OnRefreshListener2;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.ui.UserInfoActivity;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;

/**
 * Ta的粉丝
 * 
 * @author zhoulu
 * 
 */
public class BeAttentionFragment extends BaseFragment implements
		OnRefreshListener2<ListView> {
	private PullToRefreshListView listView;

	private List<AttentionInfo> list;

	private AttentionAdapter adapter;

	private String _id;
	private int start = 0;
	private int count = 30;
	private boolean isFirst = true;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getContextView() {
		// TODO Auto-generated method stub
		return R.layout.fragment_attention;
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:// 刷新
				loadRefresh();
				break;
			case 1:// 打开个人中心页面
				Intent intent = new Intent(getActivity(),
						UserInfoActivity.class);
				intent.putExtra("user_id", (String) msg.obj);
				startActivityForResult(intent, getActivity().RESULT_FIRST_USER);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && isPrepared) { // false标识第一次加载 true说明已经加载数据
			// if (LoginUtil.checkLogin(getActivity())) {
			// if (isFirst)
			loadRefresh();
			// }
		} else {

		}
	}

	@Override
	protected void init() {
		Bundle bundle = getArguments();
		_id = bundle.getString("_id");
		listView = (PullToRefreshListView) findViewById(R.id.listview);
		listView.setMode(Mode.BOTH);

		// 数据
		list = new ArrayList<AttentionInfo>();

		adapter = new AttentionAdapter(_id, getActivity(), 1, mHandler);

		listView.setAdapter(adapter);

		adapter.setData(list);

		listView.setOnRefreshListener(this);
		listView.getRefreshableView().setOnItemClickListener(
				new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if (position < 1) {
							return;
						}
						AttentionInfo attentionInfo = adapter
								.getItem(position - 1);
						Intent intent = new Intent(getActivity(),
								UserInfoActivity.class);
						intent.putExtra("user_id", attentionInfo.getUser_id());
						getActivity().startActivity(intent);
					}
				});
		if (getUserVisibleHint()) {
			// if (LoginUtil.checkLogin(getActivity())) {
			loadRefresh();
			// }
		}
	}

	@Override
	public void onResponse(BaseResponse response) {
		listView.onRefreshComplete();
		if ("0".equals(response.getResCode())) {
			if (response instanceof AttentionListResponse) {
				AttentionListResponse r = (AttentionListResponse) response;
				if (r.getRepBody().getList() != null) {
					if (!r.getRepBody().getList().isEmpty()) {
						start++;
						list.addAll(r.getRepBody().getList());
						adapter.setData(list);
					} else {
						ToastUtil.show("暂无更多!");
					}
				} else {
					ToastUtil.show("暂无更多!");
				}
			}
		} else {
			ToastUtil.show(response.getResMsg());
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		loadRefresh();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadMore();
	}

	/**
	 * 刷新数据
	 */
	private void loadRefresh() {
		isFirst = false;
		if (list != null)
			list.clear();
		if (adapter != null) {
			adapter.clearData();
		}
		start = 0;
		count = 30;
		UserCareMeParams params = new UserCareMeParams(_id, "" + start, ""
				+ count);
		RequestManager.request(this.getActivity(), params,
				AttentionListResponse.class, this, SPUtils.instance()
						.getSocialPropEntity().getApp_socail_server());
	}

	/**
	 * 获取更多数据
	 */
	private void loadMore() {
		isFirst = false;
		start = adapter.getCount();
		UserCareMeParams params = new UserCareMeParams(_id, "" + start, ""
				+ count);
		RequestManager.request(this.getActivity(), params,
				AttentionListResponse.class, this, SPUtils.instance()
						.getSocialPropEntity().getApp_socail_server());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == getActivity().RESULT_FIRST_USER
				&& resultCode == getActivity().RESULT_OK) {
			if (data != null) {
				String tempUserId = data.getStringExtra("user_id");
				// type =0 关注他 1已关注
				int type = data.getIntExtra("type", 0) == 0 ? 1 : 2;
				adapter.updateData(tempUserId, type);
			}
		}
	}
}
