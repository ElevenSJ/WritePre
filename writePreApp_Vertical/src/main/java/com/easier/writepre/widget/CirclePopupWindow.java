package com.easier.writepre.widget;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.easier.writepre.R;

public class CirclePopupWindow extends PopupWindow {

    private Button btn_1, btn_2, btn_3;
    private View mMenuView;
    private LinearLayout ll_popup;

    private Activity context;

    public CirclePopupWindow(Activity context, OnClickListener itemsOnClick) {
        this(context, itemsOnClick, 1);
    }

    /**
     * flag
     *      1：圈子加入弹出口令加入和直接加入
     *      2：设置口令的编辑和删除操作
     */
    public CirclePopupWindow(Activity context, OnClickListener itemsOnClick,
                             int flag) {
        super(context);
        this.context = context;
        mMenuView = LayoutInflater.from(context).inflate(
                R.layout.popwindow_circle_set_code, null);
        ll_popup = (LinearLayout) mMenuView.findViewById(R.id.ll_popup);
        btn_1 = (Button) mMenuView
                .findViewById(R.id.btn_1);
        btn_2 = (Button) mMenuView
                .findViewById(R.id.btn_2);
        btn_3 = (Button) mMenuView
                .findViewById(R.id.btn_3);
        btn_3.setText("取消");
        if (flag == 1 ) {
            btn_2.setText("口令加入");
            btn_1.setText("申请加入");
        } else {
            btn_2.setText("编辑");
            btn_1.setText("删除");
        }
        btn_3.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                dismiss();
            }
        });
        // 设置按钮监听
        btn_1.setOnClickListener(itemsOnClick);
        btn_2.setOnClickListener(itemsOnClick);
        this.setContentView(mMenuView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        // this.setAnimationStyle(R.style.AnimationFromBottom);
        // ColorDrawable cd = new ColorDrawable(0x000000);
        // this.setBackgroundDrawable(cd);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        this.setBackgroundDrawable(dw);
        this.setOutsideTouchable(true);
        mMenuView.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.ll_popup).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    public void setVideoSupport(boolean isSupport) {
        if (mMenuView != null) {
            if (isSupport) {
                mMenuView.findViewById(R.id.video_layout).setVisibility(View.VISIBLE);
            } else {
                mMenuView.findViewById(R.id.video_layout).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void dismiss() {
        // TODO Auto-generated method stub
        if (ll_popup != null) {
            ll_popup.clearAnimation();
        }
        super.dismiss();
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        // TODO Auto-generated method stub
        super.showAtLocation(parent, gravity, x, y);
        if (ll_popup != null) {
            Animation in = AnimationUtils.loadAnimation(context,
                    R.anim.in_bottomtop);
            ll_popup.setAnimation(in);
        }
    }

}
