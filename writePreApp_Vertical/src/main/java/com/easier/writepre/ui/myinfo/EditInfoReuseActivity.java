package com.easier.writepre.ui.myinfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.easier.writepre.R;
import com.easier.writepre.ui.BaseActivity;
import com.easier.writepre.ui.MyInfoActivity;

/**
 * 编辑个人信息，相似布局和功能 共用的Activity模板
 *
 * @author zhaomaohan
 */
public class EditInfoReuseActivity extends BaseActivity {

    private EditText editText;
    private String oldText;
    private int edit_type;
    private static final String EDIT_TYPE = "EDIT_TYPE";

    private static final int EDIT_IDCARD = 21;
    private static final int EDIT_ADDRESS = 23;
    private static final int EDIT_SCHOOL = 24;
    private static final int EDIT_SCHOOL_TEL = 25;
    public static String OLD_IDCARD = "OLD_IDCARD";
    public static String OLD_ADDRESS = "OLD_ADDRESS";
    public static String OLD_SCHOOL = "OLD_SCHOOL";
    public static String OLD_SCHOOL_TEL = "OLD_SCHOOL_TEL";

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_edit_beitie);
        init();
    }

    private void init() {
        editText = (EditText) findViewById(R.id.editText);

        Intent intent = getIntent();
        edit_type = intent.getIntExtra(MyInfoActivity.EDIT_TYPE, 0);

//		intent.getExtras().getInt(MyInfoActivity.EDIT_TYPE);

        switch (edit_type) {
            case MyInfoActivity.BEITIE:
                setTopTitle("编辑碑帖");
                editText.setHint("请输入喜欢的碑帖哦");
                oldText = intent.getStringExtra(MyInfoActivity.OLD_BEITIE);
                break;
            case MyInfoActivity.SHUTI:
                setTopTitle("编辑书体");
                editText.setHint("请输入喜欢的书体哦");
                oldText = intent.getStringExtra(MyInfoActivity.OLD_SHUTI);
                break;
            case MyInfoActivity.HOBBY:
                setTopTitle("编辑兴趣爱好");
                editText.setHint("请输入您的兴趣爱好哦");
                oldText = intent.getStringExtra(MyInfoActivity.OLD_HOBBY);
                break;
            case MyInfoActivity.SCHOOL:
                setTopTitle("编辑学校");
                editText.setHint("请编辑您的学校");
                oldText = intent.getStringExtra(MyInfoActivity.OLD_SCHOOL);
                break;
            case MyInfoActivity.PROFESSION:
                setTopTitle("编辑职业");
                editText.setHint("请输入职业哦");
                oldText = intent.getStringExtra(MyInfoActivity.OLD_PROFESSION);
                break;
            case MyInfoActivity.GOODAT:
                setTopTitle("编辑擅长");
                editText.setHint("请输入擅长");
                oldText = intent.getStringExtra(MyInfoActivity.OLD_GOODAT);
                break;
            case MyInfoActivity.GRADE:
                setTopTitle("编辑学校");
                editText.setHint("请输入您所在的学校名称");
                oldText = intent.getStringExtra(MyInfoActivity.OLD_GRADE);
                break;
            case EDIT_IDCARD:
                setTopTitle("编辑身份证");
                editText.setHint("请输入身份证");
                oldText = intent.getStringExtra(OLD_IDCARD);
                break;
            case 23:
                setTopTitle("编辑详细地址");
                editText.setHint("请输入详细地址");
                oldText = intent.getStringExtra(OLD_ADDRESS);
                break;
            case 24:
                setTopTitle("编辑培训机构或学校");
                editText.setHint("请输入培训机构或学校");
                oldText = intent.getStringExtra(OLD_SCHOOL);
                break;
            case 25:
                setTopTitle("编辑培训机构或学校联系方式");
                editText.setHint("请输入培训机构或学校联系方式");
                oldText = intent.getStringExtra(OLD_SCHOOL_TEL);
                break;
            default:
                break;
        }
        setTopRightTxt("完成");
        if(!TextUtils.isEmpty(oldText)){
            editText.setText(oldText);
            editText.setSelection(oldText.length());
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    public void onTopRightTxtClick(View view) {
        setResultFinish(RESULT_OK);
    }

    @Override
    public void onTopLeftClick(View view) {
        setResultFinish(RESULT_CANCELED);
    }

    private void setResultFinish(int resultCode) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.putExtra(MyInfoActivity.EDITTEXT, editText.getText().toString());
        setResult(resultCode, intent);
        finish();
    }

}
