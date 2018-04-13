package com.easier.writepre.ui;

import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.adapter.PkCategoryAdapter;
import com.easier.writepre.adapter.PkTeacherStudentListPopAdapter;
import com.easier.writepre.entity.PkCategoryInfo;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.PkCategoryParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.PkCategoryResponse;
import com.easier.writepre.response.PkCategoryResponse.PkCategoryBody;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.MyGridView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 报名模块_选择组别
 * @author zhaomaohan
 *
 */
public class SignUpSelectGroupActivity extends BaseActivity{
	private MyGridView gridViewZQ,gridViewSQ;//赛事专区，赛事专区
	private List<PkCategoryInfo> numberListZQ = new ArrayList<PkCategoryInfo>();
	private ArrayList<String> numberListSQ = new ArrayList<String>();
	protected String selected_role;//已经选择的角色，从选择的radiobutton获取内容
	
	private TextView tv_selected;//您还没有选择任何组别
	private TextView tv_selected_role;//屏幕底下显示的已选择的角色。
	
	private TextView tv_selected_work ;//已经选好的作品
	private String selected_work;
	
	private PkCategoryAdapter seriesAdapterZQ;
	private String role;//参与角色： 0 学生与老师  1 学生  2 老师
	private String selected_work_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup_select_group);
		init();
	}

	private void init() {
		setTopTitle("选择组别");
		setTopRightTxt("确定");
		
		gridViewZQ = (MyGridView) findViewById(R.id.video_detail_series_gridview_zq); // 賽事专区
		gridViewSQ = (MyGridView) findViewById(R.id.video_detail_series_gridview_sq);// 賽事专区
		
		tv_selected = (TextView) findViewById(R.id.tv_selected);
		tv_selected_role = (TextView) findViewById(R.id.tv_selected_role);
		tv_selected_work = (TextView) findViewById(R.id.tv_selected_work);
		
		numberListSQ.add("教师专区");
		numberListSQ.add("学生专区");
		
		
		final PkTeacherStudentListPopAdapter seriesAdapterSQ = new PkTeacherStudentListPopAdapter(
				this, numberListSQ, 3);
		gridViewSQ.setAdapter(seriesAdapterSQ);

		gridViewSQ.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				seriesAdapterSQ.clearSelection(position);
				seriesAdapterSQ.notifyDataSetChanged();
				selected_role = numberListSQ.get(position);
				if(selected_role.equals("教师专区")){
					role = "2";
					
					requestData();
				}else{
					role = "1";
					requestData();
				}
				tv_selected.setText("您选择的组别为：");
				tv_selected_role.setVisibility(View.VISIBLE);
				tv_selected_work.setVisibility(View.GONE);
				tv_selected_role.setText(selected_role);
				selected_work = null;
				selected_work_id = null;
			}
		});
		

		gridViewZQ.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(numberListZQ.get(position).getSignup().equals("0")){
					seriesAdapterZQ.clearSelection(position);
					seriesAdapterZQ.notifyDataSetChanged();
					selected_work = numberListZQ.get(position).getTitle();
					selected_work_id = numberListZQ.get(position).get_id();		
					tv_selected_work.setVisibility(View.VISIBLE);
					tv_selected_work.setText(selected_work);
				}	
			}
		});
	}

	protected void requestData() {
		dlgLoad.loading();
		if (numberListZQ != null) {
			numberListZQ.clear();
			seriesAdapterZQ = null;
		}
		RequestManager.request(this,new PkCategoryParams(role),
						PkCategoryResponse.class, this, SPUtils.instance().getSocialPropEntity().getApp_socail_server());	}


	@Override
	public void onTopRightTxtClick(View view) {
			if(selected_role == null){
				ToastUtil.show("请选择组别");
			}else if(selected_work == null){
				ToastUtil.show("请选择专区");
			}else{
				setResultFinish(RESULT_OK);
			}
		}
	private void setResultFinish(int resultCode) {
		//数据是使用Intent返回
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("selected_role", selected_role);
        intent.putExtra("selected_work", selected_work);
        intent.putExtra("selected_work_id",selected_work_id);
        //设置返回数据
        setResult(resultCode, intent);
        //关闭Activity
        finish();
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		setResultFinish(RESULT_CANCELED);
		return true;
	}

	@Override
	public void onResponse(BaseResponse response) {
		dlgLoad.dismissDialog();

		if ("0".equals(response.getResCode())) {

			if (response instanceof PkCategoryResponse) {
				PkCategoryResponse mPkCategoryResponse = (PkCategoryResponse) response;
				PkCategoryBody mPkCategoryBody = mPkCategoryResponse.getRepBody();
				for (int i = 0; i < mPkCategoryBody.getList().size(); i++) {
					numberListZQ.add(mPkCategoryBody.getList().get(i));
				}
//				selected_work_id = numberListZQ.get(0).get_id();
//				selected_work = numberListZQ.get(0).getTitle();
				if (seriesAdapterZQ == null) {
					seriesAdapterZQ = new PkCategoryAdapter(
							 numberListZQ, this,numberListZQ.size()+1);
					gridViewZQ.setAdapter(seriesAdapterZQ);
				}else{
					notifyAdpterdataChanged();
				}
			}
		}else{
			ToastUtil.show(response.getResCode());
		}
	}
	private void notifyAdpterdataChanged() {
		if (seriesAdapterZQ != null) {
			seriesAdapterZQ.notifyDataSetChanged();
		}
	}
}
