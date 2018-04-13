package com.easier.writepre.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.easier.writepre.R;
import com.easier.writepre.entity.MaskCoordinateInfo;
import com.easier.writepre.utils.LogUtils;

import java.util.ArrayList;

public class MaskActivity extends Activity {
    private ImageView iv_mask;
    private ArrayList<MaskCoordinateInfo> list;
    private int index = 0;
    private MaskCoordinateInfo maskCoordinateInfo;
    public static final int RESULT_MASK_CODE = 0x187609;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_mask);
        list = (ArrayList<MaskCoordinateInfo>) getIntent().getSerializableExtra("maskList");
        maskCoordinateInfo = (MaskCoordinateInfo) getIntent().getSerializableExtra("maskInfo");
        LogUtils.e("蒙版页面");
        iv_mask = (ImageView) findViewById(R.id.iv_mask);

    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);

        if (hasFocus && isFirst) {
            isFirst = false;
            //获取屏幕位置，可以争取获取到位置参数。
            if (list != null && !list.isEmpty()) {
                resetLayout(list.get(index));
                iv_mask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LogUtils.e("index=" + index);
                        if (index < list.size() - 1) {
                            index++;
                            LogUtils.e("index++=" + index);
                            iv_mask.setVisibility(View.INVISIBLE);
                            resetLayout(list.get(index));
                        } else {
                            setResult(RESULT_MASK_CODE);
                            finish();
                            overridePendingTransition(0, 0);
                        }
                    }
                });
            } else {
                resetLayout(maskCoordinateInfo);
                iv_mask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent resultIntent = new Intent(MaskActivity.this, RegisterForExaminationActivity.class);
                        resultIntent.putExtra("resultMask", maskCoordinateInfo);
                        setResult(RESULT_MASK_CODE, resultIntent);
                        finish();
                        overridePendingTransition(0, 0);
                    }
                });
            }
        }
    }

    /**
     * 动态设置蒙版位置
     *
     * @param maskCoordinateInfo
     */
    private void resetLayout(final MaskCoordinateInfo maskCoordinateInfo) {
        if (maskCoordinateInfo == null) {
            return;
        }
        iv_mask.setImageResource(maskCoordinateInfo.getBgRes());
        iv_mask.post(new Runnable() {
            @Override
            public void run() {
                iv_mask.layout(maskCoordinateInfo.getTopLeftX(), maskCoordinateInfo.getTopLeftY(), iv_mask.getWidth() + maskCoordinateInfo.getTopLeftX(), maskCoordinateInfo.getTopLeftY() + iv_mask.getHeight());
            }
        });
        iv_mask.setVisibility(View.VISIBLE);
    }
}

