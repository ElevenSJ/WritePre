package com.easier.writepre.widget;

import com.easier.writepre.R;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class SelectPicPopupWindow extends PopupWindow {

    private Button btn_take_video, btn_take_photo, btn_pick_photo, btn_cancel;
    private View mMenuView;
    private LinearLayout ll_popup;

    private Activity context;

    public SelectPicPopupWindow(Activity context, OnClickListener itemsOnClick) {
        this(context, itemsOnClick, true);
    }

    public SelectPicPopupWindow(Activity context, OnClickListener itemsOnClick,
                                boolean flag) { // flag true 图片类型 false视频类型
        super(context);
        this.context = context;
        mMenuView = LayoutInflater.from(context).inflate(
                R.layout.item_popupwindows, null);
        ll_popup = (LinearLayout) mMenuView.findViewById(R.id.ll_popup);
        btn_take_video = (Button) mMenuView
                .findViewById(R.id.item_popupwindows_Video);
        btn_take_photo = (Button) mMenuView
                .findViewById(R.id.item_popupwindows_camera);
        btn_pick_photo = (Button) mMenuView
                .findViewById(R.id.item_popupwindows_Photo);
        btn_cancel = (Button) mMenuView
                .findViewById(R.id.item_popupwindows_cancel);
        if (flag) {
            btn_take_photo.setText("拍照");
            btn_pick_photo.setText("从相册中选择");
        } else {
            btn_take_photo.setText("拍视频");
            btn_pick_photo.setText("本地视频");
        }
        btn_cancel.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                dismiss();
            }
        });
        // 设置按钮监听
        btn_take_video.setOnClickListener(itemsOnClick);
        btn_pick_photo.setOnClickListener(itemsOnClick);
        btn_take_photo.setOnClickListener(itemsOnClick);
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
