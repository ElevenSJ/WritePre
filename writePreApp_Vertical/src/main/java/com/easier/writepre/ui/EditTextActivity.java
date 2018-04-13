package com.easier.writepre.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.easier.writepre.R;

/**
 * 新建圈子 选择参数
 */
public class EditTextActivity extends NoSwipeBackActivity {

    private EditText etName;

    public static final String TEXT_TITLE = "text_title";
    public static final String TEXT_VALUE = "text_value";
    public static final String TEXT_HINT = "text_hint";
    public static final String TEXT_MAX = "text_max";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_name);
        initView();
    }

    private void initView() {
        setTopTitle(TextUtils.isEmpty(getIntent().getStringExtra(TEXT_TITLE))?"编辑":getIntent().getStringExtra(TEXT_TITLE));

        etName = (EditText) findViewById(R.id.et_input_name);
        etName.setText(getIntent().getStringExtra(TEXT_VALUE));
        etName.setHint(getIntent().getStringExtra(TEXT_HINT));

        etName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(getIntent().getIntExtra(TEXT_MAX,999))});

        etName.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setTopRightTxt("完成");
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    @Override
    public void onTopRightTxtClick(View view) {
        Intent intent = new Intent();
        intent.putExtra("value", etName.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

}
