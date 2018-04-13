package com.easier.writepre.ui.myinfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.ui.BaseActivity;
import com.easier.writepre.ui.MyInfoActivity;
import com.easier.writepre.utils.ChineseOrEnglishTextWatcher;
import com.easier.writepre.utils.ToastUtil;

/**
 * 编辑昵称
 * @author zhaomaohan
 *
 */
public class EditNameActivity extends BaseActivity implements OnClickListener {
	
	private EditText et_name;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_edit_name);
		init();
	}
	private void init() {
		setTopTitle("编辑昵称");
		setTopRightTxt("完成");
		
		et_name = (EditText) findViewById(R.id.et_name);
//		InputFilter[] filters = {new MyInputFilter(8,16)};  
//		et_name.setFilters(filters);  
		et_name.addTextChangedListener(new ChineseOrEnglishTextWatcher(et_name,16));
		Intent intent = getIntent();
		String oldText = intent.getStringExtra(MyInfoActivity.OLD_NAME);
		et_name.setText(oldText);
//		et_name.setSelection(oldText.length());
	}
	@Override
	public void onClick(View v) {
		super.onClick(v);
	}
	@Override
	public void onTopRightTxtClick(View view) {
		if(TextUtils.isEmpty(et_name.getText().toString().trim())){
			ToastUtil.show("昵称不能为空");
		}else{
			setResultFinish(RESULT_OK);
		}

	}
	@Override
	public void onTopLeftClick(View view) {
		setResultFinish(RESULT_CANCELED);
	}
	private void setResultFinish(int resultCode) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.putExtra("name",et_name.getText().toString());
		setResult(resultCode, intent);
		finish();
	}
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		if(keyCode == 4){
//			finish();
//		}
//		return true;
//	}
//	private EditText mEditText = null;  
//    private TextView mTextView = null;  
//  
//    private static final int MAX_COUNT = 8; 
//	private TextWatcher mTextWatcher = new TextWatcher() {  
//		  
//        private int editStart;  
//  
//        private int editEnd;  
//  
//        public void afterTextChanged(Editable s) {  
//            editStart = mEditText.getSelectionStart();  
//            editEnd = mEditText.getSelectionEnd();  
//  
//            // 先去掉监听器，否则会出现栈溢出  
//            mEditText.removeTextChangedListener(mTextWatcher);  
//  
//            // 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度  
//            // 因为是中英文混合，单个字符而言，calculateLength函数都会返回1  
//            while (calculateLength(s.toString()) > MAX_COUNT) { // 当输入字符个数超过限制的大小时，进行截断操作  
//                s.delete(editStart - 1, editEnd);  
//                editStart--;  
//                editEnd--;  
//            }  
//            // mEditText.setText(s);将这行代码注释掉就不会出现后面所说的输入法在数字界面自动跳转回主界面的问题了，多谢@ainiyidiandian的提醒  
//            mEditText.setSelection(editStart);  
//  
//            // 恢复监听器  
//            mEditText.addTextChangedListener(mTextWatcher);  
//  
//            setLeftCount();  
//        }  
//  
//        public void beforeTextChanged(CharSequence s, int start, int count,  
//                int after) {  
//  
//        }  
//  
//        public void onTextChanged(CharSequence s, int start, int before,  
//                int count) {  
//  
//        }  
//  
//    };  
  
      
//    private long calculateLength(CharSequence c) {  
//        double len = 0;  
//        for (int i = 0; i < c.length(); i++) {  
//            int tmp = (int) c.charAt(i);  
//            if (tmp > 0 && tmp < 127) {  
//                len += 0.5;  
//            } else {  
//                len++;  
//            }  
//        }  
//        return Math.round(len);  
//    }  
//  
      
//    private void setLeftCount() {  
//        mTextView.setText(String.valueOf((MAX_COUNT - getInputCount())));  
//    }  
//  
//      
//    private long getInputCount() {  
//        return calculateLength(mEditText.getText().toString());  
//    }  
}
