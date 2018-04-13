package com.easier.writepre.ui;

import java.io.Serializable;
import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.easier.writepre.R;
import com.easier.writepre.adapter.ChildCategoryAdapter;
import com.easier.writepre.entity.CategoryInfo;

/**
 * 知识库三级目录
 */
public class FoundZhiShiKuThreeListActivity extends BaseActivity implements
		OnItemClickListener {

	private String title;

	private String group;

	private String type;

	private ListView listView;

	private ChildCategoryAdapter childCategoryAdapter;

	private List<CategoryInfo> mChildCategoryList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_found_zhishiku_three);
		init();
	}

	@SuppressWarnings("unchecked")
	private void init() {
		mChildCategoryList = (List<CategoryInfo>) getIntent()
				.getSerializableExtra("from_parent");
		title = getIntent().getStringExtra("title");
		group = getIntent().getStringExtra("group");
		type= getIntent().getStringExtra("group");
		setTopTitle(title);
		listView = (ListView) findViewById(R.id.found_zhishiku_three_list);
		childCategoryAdapter = new ChildCategoryAdapter(this,
				mChildCategoryList);
		listView.setAdapter(childCategoryAdapter);
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// Intent intent = new Intent(FoundZhiShiKuThreeListActivity.this,
		// FindDetailActivity.class);
		Intent intent = new Intent(FoundZhiShiKuThreeListActivity.this,
				CourseDetailActivity.class);
		intent.putExtra("from_child", (Serializable) mChildCategoryList);
		intent.putExtra("from_child_index", arg2);
		intent.putExtra("from_flag", true);
		intent.putExtra(CourseDetailActivity.TYPE, type);
		intent.putExtra(CourseDetailActivity.GROUP, group);
		intent.putExtra(CourseDetailActivity.TITLE,title);
		FoundZhiShiKuThreeListActivity.this.startActivity(intent);
		for (int i = 0, j = mChildCategoryList.size(); i < j; i++) {
			if (arg2 == i) {
				mChildCategoryList.get(i).setCheck(true);
			} else {
				mChildCategoryList.get(i).setCheck(false);
			}
		}
		childCategoryAdapter.notifyDataSetChanged();

	}

}
