package com.easier.writepre.ui.myinfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.easier.writepre.R;
import com.easier.writepre.ui.BaseActivity;
import com.easier.writepre.ui.LittleCalligraphyTeacherEditCandidateInformationActivity;
import com.easier.writepre.utils.ChineseOrEnglishTextWatcher;
import com.easier.writepre.utils.ToastUtil;

public class EditIdCardActivity extends BaseActivity {

    private EditText edit_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_id_card);
        init();
    }
    private void init() {
        setTopTitle("编辑身份证号");
        setTopRightTxt("完成");

        edit_text = (EditText) findViewById(R.id.edit_text);
//		InputFilter[] filters = {new MyInputFilter(8,16)};
//		et_name.setFilters(filters);
        edit_text.addTextChangedListener(new ChineseOrEnglishTextWatcher(edit_text,18));
        Intent intent = getIntent();
        String oldText = intent.getStringExtra(LittleCalligraphyTeacherEditCandidateInformationActivity.OLD_IDCARD);
        if(!TextUtils.isEmpty(oldText)){
            edit_text.setText(oldText);
            edit_text.setSelection(oldText.length());
        }

    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
    @Override
    public void onTopRightTxtClick(View view) {
        if(18 != edit_text.getText().toString().length()){
            ToastUtil.show("身份证号没有18位");
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
        intent.putExtra("id_card",edit_text.getText().toString());
        setResult(resultCode, intent);
        finish();
    }

}
