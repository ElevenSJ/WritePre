package com.easier.writepre.ui;

import java.util.ArrayList;

import com.easier.writepre.R;
import com.easier.writepre.adapter.PkTeacherStudentListAdapter;
import com.easier.writepre.adapter.PkTeacherStudentListPopAdapter;
import com.easier.writepre.entity.PkContentInfo;
import com.easier.writepre.fragment.PkMsgDetailFragment;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.PkWorksGoodedParams;
import com.easier.writepre.param.PkWorksQueryParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.PkCategoryResponse;
import com.easier.writepre.response.PkWorksQueryStudentResponse;
import com.easier.writepre.response.PkCategoryResponse.PkCategoryBody;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;

/**
 * 参赛作品详情
 * 
 * @author sunjie
 */
public class PkMsgDetailActivity extends BaseActivity {

	private ViewPager viewPage;

	private PkDetailViewPagerAdapter adapter;

	private int position = 0;

	private String pk_id="",pk_type="",status = "", role = "", pk_cata_id = "", city = "";

	private int flag = 0; // 0全部 1人气 2我的

	private ArrayList<PkContentInfo> list;

	private boolean isGetData = false;
	private boolean isNone = true;
	private boolean canUpdate = true;
	private boolean isUpdate = false;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_pk_msg_detail);
		initData();
		init();
	}

	private void initData() {
		pk_id = getIntent().getStringExtra("pk_id");
		pk_type= getIntent().getStringExtra("pk_type");
		status = getIntent().getStringExtra("status");
		role = getIntent().getStringExtra("role");
		pk_cata_id = getIntent().getStringExtra("pk_cata_id");
		city = getIntent().getStringExtra("city");
		flag = getIntent().getIntExtra("flag", 0);
		position = getIntent().getIntExtra("position", 0);
		list = (ArrayList<PkContentInfo>) getIntent().getSerializableExtra("data");
	}

	@Override
	public void onTopLeftClick(View view) {
		setResultData();
		super.onTopLeftClick(view);
	}

	@Override
	public void onBackPressed() {
		setResultData();
		super.onBackPressed();
	}

	private void setResultData() {
		Intent intent = new Intent();
		intent.putExtra("data", list);
		intent.putExtra("position", position);
		setResult(RESULT_OK, intent);
	}

	private void init() {
		setTopTitle("作品详情");
		viewPage = (ViewPager) findViewById(R.id.web_viewpage);
		adapter = new PkDetailViewPagerAdapter(getSupportFragmentManager());
		viewPage.setAdapter(adapter);

		viewPage.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				position = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				if (arg2 == 0) {
					canUpdate = true;
					if (!isUpdate) {
						isUpdate = true;
						adapter.notifyDataSetChanged();
					}
					if (arg0 == list.size() - 1) {
						if (!isGetData) {
							if (flag == 0) {
								loadAllData(list.get(list.size() - 1).getUptime(), status, role, pk_cata_id, city);
							} else if (flag == 1) {
								loadVoteMoreData(status, role, pk_cata_id, city, list.size() + 1);
							} else {

							}
						} else {
							if (!isNone) {
//								ToastUtil.show("正在加载更多数据");
							} else {
//								ToastUtil.show("已加载全部数据");
							}
						}
					}
					if (arg0 == 0) {
						// ToastUtil.show("第一页");
					}

				} else {
					canUpdate = false;
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		viewPage.setCurrentItem(position);

	}

	// 全部接口
	private void loadAllData(String last_id, String status, String role, String pk_cata_id, String city) {
		isGetData = true;
		isNone = false;
		RequestManager.request(this, new PkWorksQueryParams(pk_id,last_id, status, role, pk_cata_id, city, "10"),
				PkWorksQueryStudentResponse.class, this,
				SPUtils.instance().getSocialPropEntity().getApp_socail_server()); // 全部接口
	}

	// 人气接口
	private void loadVoteMoreData(String status, String role, String pk_cata_id, String city, int start) {
		isGetData = true;
		isNone = false;
		RequestManager.request(this, new PkWorksGoodedParams(pk_id,status, role, pk_cata_id, city, start, 10),
				PkWorksQueryStudentResponse.class, this,
				SPUtils.instance().getSocialPropEntity().getApp_socail_server()); // 人气接口
	}

	private class PkDetailViewPagerAdapter extends FragmentStatePagerAdapter {

		public PkDetailViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return POSITION_UNCHANGED;
		}

		@Override
		public Fragment getItem(int position) {
			String id;
			id = list.get(position).get_id();
			return new PkMsgDetailFragment().newInstance(id,pk_type);
		}

	}

	@Override
	public void onResponse(BaseResponse response) {
		isGetData = false;
		if ("0".equals(response.getResCode())) {
			if (response instanceof PkWorksQueryStudentResponse) {
				PkWorksQueryStudentResponse pkqsResult = (PkWorksQueryStudentResponse) response;
				if (pkqsResult != null) {
					PkWorksQueryStudentResponse.Repbody body = pkqsResult.getRepBody();
					for (int i = 0; i < body.getList().size(); i++) {
						list.add(body.getList().get(i));
					}
					if (body.getList().size() > 0) {
						isNone = false;
						if (canUpdate) {
							isUpdate = true;
							adapter.notifyDataSetChanged();
						}
					} else {
						isNone = true;
					}
				}
			}
		} else {
			ToastUtil.show(response.getResMsg());
		}
	}
}
