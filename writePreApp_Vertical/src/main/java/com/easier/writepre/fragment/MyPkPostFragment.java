package com.easier.writepre.fragment;

import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.adapter.SquareAllEssenceListAdapter;
import com.easier.writepre.entity.ContentInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.SquareContentGetParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.SquareContentGetResponse;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.social.refreash.PullToRefreshBase.Mode;
import com.easier.writepre.ui.BaseActivity;
import com.easier.writepre.utils.SPUtils;

import android.view.View;

/**
 * 我的大赛发帖
 * @author zhaomaohan
 *
 */
public class MyPkPostFragment extends BaseFragment {
private PullToRefreshListView listView;
	
	private List<ContentInfo> list;
	
	private SquareAllEssenceListAdapter adapter;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getContextView() {
		// TODO Auto-generated method stub
		return R.layout.fragment_my_pk_post;
	}
	
	@Override
	protected void init() {
		listView = (PullToRefreshListView) findViewById(R.id.listview);
		listView.setMode(Mode.PULL_FROM_END);
		
		// 数据
		list = new ArrayList<ContentInfo>();
		
		adapter = new SquareAllEssenceListAdapter(getActivity(),listView.getRefreshableView());
		
		listView.setAdapter(adapter);
		
//		adapter.setData(list);
		
		listViewLoadData("9");
	}
	
	/**
	 * 首次获取数据
	 */
	protected void listViewLoadData(String lastId) {
//		RequestManager.request(new SquareContentGetParams(lastId, "20", true), SquareContentGetResponse.class, this,  SPUtils.instance().getSocialPropEntity().getApp_socail_server());
	}
	
	@Override
	public void onResponse(BaseResponse response) {
		if ("0".equals(response.getResCode())) {
			if (response instanceof SquareContentGetResponse) {
				SquareContentGetResponse gscrResult = (SquareContentGetResponse) response;
				if (gscrResult != null) {
					SquareContentGetResponse.Repbody rBody = gscrResult.getRepBody();
					if (rBody != null) {
						if (rBody.getList() != null) {
							if (rBody.getList().isEmpty()) {
								BaseActivity.setListLabel(listView, true);
							} else {
								list.addAll(rBody.getList());
							}
						}
						adapter.mergeData(list,null);
						adapter.notifyDataSetChanged();
					}
				}
			}
		}
	}
}
