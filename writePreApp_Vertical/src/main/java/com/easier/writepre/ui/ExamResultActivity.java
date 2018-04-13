package com.easier.writepre.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.response.ExamSubmitResponse;
import com.hp.hpl.sparta.Text;

/**
 * 理论考试结果页面
 *
 * @author zhoulu
 */
public class ExamResultActivity extends BaseActivity {

    private String title;
    private ExamSubmitResponse.ExamSubmitResponseInfo examSubmitResponseInfo;
    private TextView tv_exam_result;
    private Button btn_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examresult);
        title = getIntent().getStringExtra("title");
        examSubmitResponseInfo = (ExamSubmitResponse.ExamSubmitResponseInfo) getIntent().getSerializableExtra("result");
        initView();
    }

    private void initView() {
        setTopTitle(title);
        tv_exam_result = (TextView) findViewById(R.id.tv_exam_result);
        tv_exam_result.setText("本次理论成绩: " + examSubmitResponseInfo.getScore() + " 分");
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBack();
            }
        });
        if (!TextUtils.isEmpty(examSubmitResponseInfo.getStu_time()) && !TextUtils.equals("0", examSubmitResponseInfo.getStu_time())) {
            showExamScoreDialog("恭喜您获得了 " + examSubmitResponseInfo.getStu_time() + " 个学时!");
        }
    }

    /**
     * 通知用户上次考试存在异常
     *
     * @param text
     */
    private void showExamScoreDialog(String text) {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_submitexam,
                null);
        TextView tv = (TextView) view.findViewById(R.id.tv_tips);
        TextView confirm = (TextView) view.findViewById(R.id.tv_confirm);
        TextView cancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView line = (TextView) view.findViewById(R.id.tv_line);
        tv.setText(text);
        cancel.setVisibility(View.GONE);
        line.setVisibility(View.GONE);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void onBack() {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBack();
            return true;
        }
        return false;
    }

}
