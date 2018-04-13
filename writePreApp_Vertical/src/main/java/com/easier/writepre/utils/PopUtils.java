package com.easier.writepre.utils;

import com.easier.writepre.R;
import com.easier.writepre.widget.FloatViewGroup;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * 对比
 */
public class PopUtils implements OnSeekBarChangeListener {

    private FloatViewGroup fvg;

    private PopupWindow popupWindow;

    private final int initAlpha = 127;// 初始化透明度

    private final int iniRotate = 360;// 初始化旋转角度

    private final int ImageAlpha = 255;// 透明度

    private final int ImageRotate = 720;// 旋转度

    /**
     * @param cxt
     * @param photoFile
     * @param from      false描红 true碑帖
     */
    public PopupWindow showPop(Context cxt, String photoFile, boolean from,
                               int compPicWidth, int compPicHight) {
        View view = LayoutInflater.from(cxt).inflate(R.layout.float_img_view,
                null, false);
        fvg = (FloatViewGroup) view.findViewById(R.id.viewArea);
        view.findViewById(R.id.iv_cancel).bringToFront();
        view.findViewById(R.id.iv_show).bringToFront();
        fvg.setImageRes(photoFile);
        fvg.setViewRotate(iniRotate);
        SeekBar seekbar = (SeekBar) view.findViewById(R.id.rotate_seek);
        seekbar.setMax(ImageRotate);
        seekbar.setProgress(iniRotate);
        seekbar.setOnSeekBarChangeListener(this);
        SeekBar seekbar2 = (SeekBar) view.findViewById(R.id.alpha_seek);
        seekbar2.setMax(ImageAlpha);
        seekbar2.setProgress(initAlpha);
        fvg.setViewAlpha(initAlpha);
        seekbar2.setOnSeekBarChangeListener(this);
        view.setBackgroundColor(Color.TRANSPARENT);
        if (from) {
            popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT) {
                @Override
                public void dismiss() {
                    super.dismiss();
//                    if (fvg != null)
//                        fvg.recycle();
                }
            };
            setSmartBar(popupWindow);
        } else {
            popupWindow = new PopupWindow(view, compPicWidth, compPicHight) {
                @Override
                public void dismiss() {
                    super.dismiss();
//                    if (fvg != null)
//                        fvg.recycle();
                }
            };
//			view.findViewById(R.id.iv_show).setVisibility(View.GONE);
        }
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        return popupWindow;
    }

    public FloatViewGroup getFvg() {
        return fvg;
    }

    /**
     * 处理魅族手机开启smartBar后遮挡住pop
     *
     * @param pop
     */
    public static void setSmartBar(PopupWindow pop) {
        pop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        pop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.rotate_seek:
                fvg.setViewRotate(progress);
                break;
            case R.id.alpha_seek:
                fvg.setViewAlpha(progress);
                break;
            default:
                break;

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
