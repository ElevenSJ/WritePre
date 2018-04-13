package com.easier.writepre.ui;

import java.util.ArrayList;

import com.easier.writepre.R;
import com.easier.writepre.adapter.CreatItemChooseAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * 创建单选项
 * 
 */
public class CreatItemChooseActivity extends BaseActivity {

	private ListView listView;

	private CreatItemChooseAdapter adapter;

	private ArrayList<String> datas = new ArrayList<String>();

	// 选中的item名称
	private String selectedValue = "";
	
	/**
	 * 已选的item值键值
	 */
	public static final String SELECTED_STRING = "selected_string";

	/**
	 * 列表数据键值
	 */
	public static final String ALL_DATA = "all_data";

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

		adapter = new CreatItemChooseAdapter(this);

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
		selectedValue = getIntent().getStringExtra(SELECTED_STRING);
		datas = (ArrayList<String>) getIntent().getSerializableExtra(ALL_DATA);
		adapter.setData(datas, selectedValue);
	}

	@Override
	public void onTopRightTxtClick(View view) {
		Intent intent = new Intent();
		intent.putExtra(SELECTED_STRING, adapter.getSelectString());
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
	}

}
