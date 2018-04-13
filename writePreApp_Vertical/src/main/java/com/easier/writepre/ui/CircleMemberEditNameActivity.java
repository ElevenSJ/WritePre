package com.easier.writepre.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.easier.writepre.R;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.CircleMemberEditNameParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.utils.ChineseOrEnglishTextWatcher;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;

/**
 * 编辑昵称
 *
 * @author zhaomaohan
 */
public class CircleMemberEditNameActivity extends BaseActivity implements OnClickListener {

    private EditText et_name;
    private String circleId;
    private String circleName;
    private String userId;
    private boolean canEmpty;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_edit_name);
        init();
    }

    private void init() {
        setTopTitle("编辑圈子昵称");
        setTopRightTxt("完成");

        et_name = (EditText) findViewById(R.id.et_name);
//		InputFilter[] filters = {new MyInputFilter(8,16)};  
//		et_name.setFilters(filters);  
        et_name.addTextChangedListener(new ChineseOrEnglishTextWatcher(et_name, 16));
        Intent intent = getIntent();
        String oldText = intent.getStringExtra("old_name");
        circleId = intent.getStringExtra("circle_id");
        userId = intent.getStringExtra("user_id");
        canEmpty = intent.getBooleanExtra("canEmpty", true);
        et_name.setText(oldText);
//		et_name.setSelection(oldText.length());
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    public void onTopRightTxtClick(View view) {
        if (!canEmpty && TextUtils.isEmpty(et_name.getText().toString())) {
            ToastUtil.show("昵称不能为空");
            return;
        }
        RequestManager.request(this, new CircleMemberEditNameParams(userId, circleId, et_name.getText().toString()), BaseResponse.class, this,
                SPUtils.instance().getSocialPropEntity().getApp_socail_server());
//		setResultFinish(RESULT_OK);
    }

    @Override
    public void onTopLeftClick(View view) {
        setResultFinish(RESULT_CANCELED);
    }

    private void setResultFinish(int resultCode) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.putExtra("name", et_name.getText().toString());
        setResult(resultCode, intent);
        finish();
    }

    @Override
    public void onResponse(BaseResponse response) {
//		super.onResponse(response);
        if ("0".equals(response.getResCode())) {
            setResultFinish(RESULT_OK);
        } else {
            ToastUtil.show(response.getResMsg());
        }

    }
}
