package com.easier.writepre.ui;

import java.util.ArrayList;
import java.util.HashMap;

import com.easier.writepre.R;
import com.easier.writepre.adapter.CircleCreatItemAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * 新建圈子 选择参数
 * 
 */
public class CircleCreatItemChooseActivity extends BaseActivity {

	private ListView listView;

	private CircleCreatItemAdapter adapter;

	private ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();

	// 选中的item名称
	private String selectedValue = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creat_item_choose);
		initView();
		initData();
	}

	private void initView() {
		setTopTitle("选择");

		listView = (ListView) findViewById(R.id.listView);

		adapter = new CircleCreatItemAdapter(this);

		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				setTopRightTxt("完成");
				adapter.setSelect(position);
			}
		});

	}

	private void initData() {
		selectedValue = getIntent().getStringExtra(CircleCreatActivity.SELECTED_VALUE);
		datas = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra(CircleCreatActivity.ALL_DATA);
		adapter.setData(datas, selectedValue);
	}

	@Override
	public void onTopRightTxtClick(View view) {
		Intent intent = new Intent();
		intent.putExtra(CircleCreatActivity.SELECTED_STRING, adapter.getSelectString());
		intent.putExtra(CircleCreatActivity.SELECTED_VALUE, adapter.getSelectValue());
		setResult(CircleCreatActivity.DATA_RESULT_CODE, intent);
		finish();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
	}

}
