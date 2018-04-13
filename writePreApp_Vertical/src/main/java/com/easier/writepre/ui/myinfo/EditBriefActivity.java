package com.easier.writepre.ui.myinfo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.entity.LoginEntity;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.UpdateUserNickPatams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.ui.BaseActivity;
import com.easier.writepre.ui.MyInfoActivity;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.google.gson.Gson;

/**
 * 编辑简介
 * @author zhaomaohan
 *
 */
public class EditBriefActivity extends BaseActivity implements OnClickListener{
	private ImageView img_back;
	private TextView tv_save;
	private EditText et_name;
	private CharSequence temp;
	private int selectionStart;
	private int selectionEnd;
	private int num = 50;
	private TextView tv_input_left;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_edit_brief);
		init();
	}
	private void init() {
		setTopTitle("编辑简介");
		setTopRightTxt("完成");
		
		et_name = (EditText) findViewById(R.id.et_name);
		tv_input_left = (TextView) findViewById(R.id.tv_input_left);
		
		et_name.addTextChangedListener(mTextWatcher);
		
		Intent intent = getIntent();
		String oldText = intent.getStringExtra(MyInfoActivity.OLD_BRIEF);
		if(!TextUtils.isEmpty(oldText)){
			et_name.setText(oldText);
			et_name.setSelection(oldText.length());
		}

		setLeftCount();
		
	}
	@Override
	public void onClick(View v) {
		super.onClick(v);
	}
	@Override
	public void onTopRightTxtClick(View view) {
		updateInfo(et_name.getText().toString());
		finish();
//		setResultFinish(RESULT_OK);
	}

//	private void setResultFinish(int resultCode) {
//		// TODO Auto-generated method stub
//		Intent intent = new Intent();
//		intent.putExtra("brief",et_name.getText().toString());
//		setResult(resultCode, intent);
//		finish();
//	}

//	@Override
//	public void beforeTextChanged(CharSequence s, int start, int count,
//			int after) {
//		// TODO Auto-generated method stub
//		temp = s;
//	}
//	@Override
//	public void onTextChanged(CharSequence s, int start, int before, int count) {
//		// TODO Auto-generated method stub
//		
//	}
//	@Override
//	public void afterTextChanged(Editable s) {
//		// TODO Auto-generated method stub
//		int number = num - s.length();
//		tv_input_left.setText("" + number);
//		selectionStart = et_name.getSelectionStart();
//		selectionEnd = et_name.getSelectionEnd();
//		if (temp.length() > num) {
//			s.delete(selectionStart - 1, selectionEnd);
//			int tempSelection = selectionEnd;
//			et_name.setText(s);
//			et_name.setSelection(tempSelection);// 设置光标在最后
//		}
//	}
//	private EditText et_name = null;  
//    private TextView tv_input_left = null;  
  
    private static final int MAX_COUNT = 50;

	private TextWatcher mTextWatcher = new TextWatcher() {  
		  
        private int editStart;  
  
        private int editEnd;  
  
        public void afterTextChanged(Editable s) {  
            editStart = et_name.getSelectionStart();  
            editEnd = et_name.getSelectionEnd();  
  
            // 先去掉监听器，否则会出现栈溢出  
            et_name.removeTextChangedListener(mTextWatcher);  
  
            // 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度  
            // 因为是中英文混合，单个字符而言，calculateLength函数都会返回1  
            while (calculateLength(s.toString()) > MAX_COUNT) { // 当输入字符个数超过限制的大小时，进行截断操作  
                s.delete(editStart - 1, editEnd);  
                editStart--;  
                editEnd--;  
            }  
            // et_name.setText(s);将这行代码注释掉就不会出现后面所说的输入法在数字界面自动跳转回主界面的问题了，多谢@ainiyidiandian的提醒  
            et_name.setSelection(editStart);  
  
            // 恢复监听器  
            et_name.addTextChangedListener(mTextWatcher);  
  
            setLeftCount();  
        }  
  
        public void beforeTextChanged(CharSequence s, int start, int count,  
                int after) {  
  
        }  
  
        public void onTextChanged(CharSequence s, int start, int before,  
                int count) {  
  
        }  
  
    };  
  
      
    private long calculateLength(CharSequence c) {  
        double len = 0;  
        for (int i = 0; i < c.length(); i++) {  
            int tmp = (int) c.charAt(i);  
            if (tmp > 0 && tmp < 127) {  
                len += 0.5;  
            } else {  
                len++;  
            }  
        }  
        return Math.round(len);  
    }  
  
      
    private void setLeftCount() {  
        tv_input_left.setText(String.valueOf((MAX_COUNT - getInputCount())));  
    }  
  
      
    private long getInputCount() {  
        return calculateLength(et_name.getText().toString());  
    }
	public void updateInfo(String sign) {

		LoginEntity body = SPUtils.instance().getLoginEntity();

		String[] coord = new String[0];

		if (body.getCoord() != null) {
			if (body.getCoord().size() < 2) {
				coord = new String[0];
			} else {
				coord = new String[] { body.getCoord().get(0).toString(), body.getCoord().get(1).toString() };
			}
		}

		RequestManager.request(this,
				new UpdateUserNickPatams(body.getUname(), body.getBirth_day(), body.getAge(), body.getSex(), body.getAddr(),
						body.getFav(), body.getInterest(), body.getQq(), body.getBei_tie(), body.getFav_font(),
						body.getStu_time(), body.getSchool(), body.getCompany(), body.getProfession(),
						sign, body.getEmail(), body.getReal_name(), coord),
				BaseResponse.class, this, Constant.URL);
	}
	@Override
	public void onResponse(BaseResponse response) {
		if ("0".equals(response.getResCode())) {
			LoginEntity loginEntity = SPUtils.instance().getLoginEntity();
			loginEntity.setSignature(et_name.getText().toString());
			SPUtils.instance().put(SPUtils.LOGIN_DATA, new Gson().toJson(loginEntity));
		} else {
			ToastUtil.show(response.getResMsg());
		}
	}
}
