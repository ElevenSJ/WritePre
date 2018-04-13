package com.easier.writepre.ui;

import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.adapter.MyListAdapter;
import com.easier.writepre.entity.LoginEntity;
import com.easier.writepre.entity.MyListData;
import com.easier.writepre.ui.myinfo.EditTelActivity;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class AccountAndSecurityActivity extends BaseActivity implements OnItemClickListener {
	private ListView lv_list;
	private List<MyListData> list;
	public String left_text[] = new String[]{"手机绑定","修改密码","微信账号","腾讯QQ","新浪微博"};
	public int left_image[] = new int[]{-1,-1,R.drawable.bg_btn_wx_norm,R.drawable.bg_btn_qq_norm,R.drawable.bg_btn_wb_norm};
	public String center_text[] = new String[]{null,null,null,null,null};
	public String right_button_text[] = new String[]{"未绑定",null,"未绑定","未绑定","未绑定"};
	public int right_button_background[] = new int[]{R.drawable.shape2,-1,R.drawable.shape2,R.drawable.shape2,R.drawable.shape2};
	public int right_button_text_color[] = new int[]{R.color.text_color_gray_9,-1,R.color.text_color_gray_9,R.color.text_color_gray_9,R.color.text_color_gray_9}; 
	private LoginEntity entity;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_my_list);
		initView();
	}

	private void initView() {
		setTopTitle("帐号与安全");
		
		entity = SPUtils.instance().getLoginEntity();
		
		//刷新状态
		refreshStatus();
		
		list = getData();
		lv_list = (ListView) findViewById(R.id.lv_list);
		MyListAdapter adapter = new MyListAdapter(this, list);
		lv_list.setAdapter(adapter);
		lv_list.setOnItemClickListener(this);
		
	}
	private void refreshStatus() {
		//刷新手机状态
		if(TextUtils.isEmpty(entity.getLogin_tel())){
			center_text[0] = null;
			right_button_background[0] = R.drawable.shape2;
			right_button_text[0] = "未绑定";
			right_button_text_color[0] = R.color.text_color_gray_9;
		}else{
			center_text[0] = entity.getLogin_tel();
			right_button_background[0] = R.drawable.shape;
			right_button_text[0] = "更换手机号";
			right_button_text_color[0] = R.color.white;
		}
		//刷新第三方的状态
		refreshThirdStatus(entity.getId_weixin(),2,R.drawable.bg_btn_wx_norm,R.drawable.bg_btn_wx_press);
		refreshThirdStatus(entity.getId_qq(),3,R.drawable.bg_btn_qq_norm,R.drawable.bg_btn_qq_press);
		refreshThirdStatus(entity.getId_weibo(),4,R.drawable.bg_btn_wb_norm,R.drawable.bg_btn_wb_press);
	}

	/**
	 * 刷新第三方绑定的状态
	 * @param type：绑定的类型：微信/QQ/微博
	 * @param position：处在listview里的位置
	 * @param unbind：未绑定显示的图片
	 * @param bind：绑定后显示的图片
	 */
	private void refreshThirdStatus(String type,int position,int unbind,int bind) {
		if(TextUtils.isEmpty(type)){
			left_image[position] = unbind;
			right_button_background[position] = R.drawable.shape2;
			right_button_text[position] = "未绑定";
			right_button_text_color[position] = R.color.text_color_gray_9; 
		}else{
			left_image[position] = bind;
			right_button_background[position] = R.drawable.shape;
			right_button_text[position] = "绑定";
			right_button_text_color[position] = R.color.white; 
		}
	}

	//获取动态数组数据  可以由其他地方传来(json等)  
    private List<MyListData> getData() {  
        List<MyListData> list = new ArrayList<MyListData>();  
        for (int i =0;i<left_text.length;i++){
        	MyListData myListData = new MyListData(left_image[i],left_text[i],-1,null,center_text[i],right_button_text[i],right_button_background[i],right_button_text_color[i]);
        	list.add(myListData);
        	}
        return list;  
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (position) {
		case 0:
			Intent intent0 = new Intent(AccountAndSecurityActivity.this,EditTelActivity.class);
			startActivity(intent0);
			break;
		case 1:
			Intent intent1 = new Intent(AccountAndSecurityActivity.this,ModifyPasswordActivity.class);
			startActivity(intent1);
			break;
		case 2:
			ToastUtil.show("微信帐号");
			break;
		case 3:
			ToastUtil.show("腾讯QQ");
			break;
		case 4:
			ToastUtil.show("新浪微博");
			break;
		default:
			break;
		}
		
	}
}
