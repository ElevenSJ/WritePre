package com.easier.writepre.fragment;

import java.util.List;
import com.easier.writepre.R;
import com.easier.writepre.adapter.FoundBeiTieListAdapter;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.ResPenmenParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.ResPenmenResponse;
import com.easier.writepre.response.ResPenmenResponse.ResPenmenBody;
import com.easier.writepre.response.ResPenmenResponse.ResPenmenInfo;
import com.easier.writepre.social.refreash.PullToRefreshListView;
import com.easier.writepre.ui.FoundBeiTieCalligrapherActivity;
import com.easier.writepre.utils.ToastUtil;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

/**
 * 
 * 经典碑帖
 * 
 */
public class FoundBeiTieFragment extends BaseFragment {

	private List<ResPenmenInfo> list;

	private PullToRefreshListView listView;

	private EditText found_beitie_search_et;

	@Override
	public int getContextView() {
		return R.layout.found_beitie;
	}

	@Override
	protected void init() {

		listView = (PullToRefreshListView) findViewById(R.id.found_beitie_listview);

		found_beitie_search_et = (EditText) findViewById(R.id.found_beitie_search_et);

		found_beitie_search_et.setOnClickListener(this);

		RequestManager.request(getActivity(),new ResPenmenParams(), ResPenmenResponse.class,
				this, Constant.URL);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.found_beitie_search_et:
			Intent intent = new Intent(getActivity(),
					FoundBeiTieCalligrapherActivity.class);
			intent.putExtra("SEARCH_TYPE", true);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public void onResponse(BaseResponse response) {
		if ("0".equals(response.getResCode())) {
			if (response instanceof ResPenmenResponse) {
				ResPenmenResponse mResPenmenResponse = (ResPenmenResponse) response;
				if (mResPenmenResponse != null) {
					ResPenmenBody mResPenmenBody = mResPenmenResponse
							.getRepBody();
					if (mResPenmenBody != null) {
						list = mResPenmenBody.getList();
						if (list != null && list.size() > 0) {
							listView.setAdapter(new FoundBeiTieListAdapter(
									getActivity(), list));

						}
					}

				}
			}
		} else
			ToastUtil.show(response.getResMsg());
	}

}
