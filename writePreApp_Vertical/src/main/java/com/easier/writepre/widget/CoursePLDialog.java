package com.easier.writepre.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;


/**
 * 课程评论提示框
 *
 * @author sunjie
 */
public class CoursePLDialog extends Dialog implements View.OnClickListener {
    Context context;
    OKDialogListener listener;
    float rating;

    private RelativeLayout mainLayout;
    private TextView titleTxt;
    private RatingBar ratingBar;
    private EditText plEt;
    private Button okBt;

    public CoursePLDialog(Context context, int theme) {
        super(context, theme);
    }

    public CoursePLDialog(Context context, int theme, OKDialogListener listener) {
        super(context, theme);
        this.context = context;

        this.listener = listener;
    }
    public void show(float rating) {
        this.rating = rating;
        if (ratingBar!=null) {
            ratingBar.setRating(rating);
        }
        show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_pl_hint);

        initView();
    }

    private void initView() {
        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);

        //动态设置宽度
        LinearLayout.LayoutParams mainLayoutParams = new LinearLayout.LayoutParams(WritePreApp.getApp().getWidth(0.9f), RelativeLayout.LayoutParams.WRAP_CONTENT);
        mainLayout.setLayoutParams(mainLayoutParams);

        titleTxt = (TextView) findViewById(R.id.tv_title);
        ratingBar = (RatingBar) findViewById(R.id.user_ratingBar);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float ratingf, boolean fromUser) {
                rating = ratingf;
                ratingBar.setRating(rating);
                if (rating==1.0f){
                    titleTxt.setText("较差");
                }else if (rating==2.0f){
                    titleTxt.setText("一般");
                }else if (rating==3.0f){
                    titleTxt.setText("良好");
                }else if (rating==4.0f){
                    titleTxt.setText("推荐");
                }else if (rating==5.0f){
                    titleTxt.setText("极佳");
                }
            }
        });

        plEt = (EditText) findViewById(R.id.pl_edit);
        okBt = (Button) findViewById(R.id.ok_bt);
        okBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (listener!=null) {
            listener.OnClick(v, rating, plEt.getText().toString());
        }
    }

    public interface OKDialogListener {
        public void OnClick(View view, float rating,String content);
    }
}
