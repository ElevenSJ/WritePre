package com.easier.writepre.video;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.ui.NoSwipeBackActivity;
import com.easier.writepre.utils.CheckPermissionUtils;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.ToastUtil;
import com.video.recored.CameraHelper;
import com.video.recored.CameraPreviewView;
import com.video.recored.WXLikeVideoRecorder;

import java.lang.ref.WeakReference;


/**
 * 视频录制
 */
public class VideoRecordActivity extends NoSwipeBackActivity implements View.OnTouchListener {

    private Camera mCamera;

    private TextView tv_start_recorder;  //按住拍

    private float mDownX, mDownY;

    private int nowTime;   //录制了多少秒

    private int oldTime;   //每次录制结束时是多少秒

    private TimeCount timeCount;  //计时器

    private boolean isMeet = false;    // 是否满足视频的最少播放时长

    private int cameraPosition = 1;//0代表前置摄像头，1代表后置摄像头

    private boolean isTimeOut = false;  //录制时间是否超时

    public static final int VIDEO_TIME = 2;    //视频最少必须2秒

    private WXLikeVideoRecorder mRecorder;

    private boolean isCancelRecord = false;

    private static final int OUTPUT_WIDTH = 480;  // 输出宽度

    private static final int OUTPUT_HEIGHT = 480;   // 输出高度

    private static final int CANCEL_RECORD_OFFSET = -100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_recorder);
        init();
    }

    /**
     * 打开权限设置提示
     */
    public void showSettingsDialog(final Context context) {
        final Dialog dialog = new Dialog(context, R.style.loading_dialog);
        View view = LayoutInflater.from(context).inflate(
                R.layout.dialog_islogin, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_login_now);
        tv.setText("摄像头权限被禁用,是否前往打开？");
        TextView confirm = (TextView) view.findViewById(R.id.tv_login);
        confirm.setText("去打开");
        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CheckPermissionUtils.getAppDetailSettingIntent(context);
                dialog.dismiss();
                finish();
            }
        });
        view.findViewById(R.id.tv_cancel).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        finish();

                    }
                });
        dialog.dismiss();
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    void init() {
        initCamera(cameraPosition);
        tv_start_recorder = (TextView) findViewById(R.id.tv_start_recorder);
        tv_start_recorder.setOnTouchListener(this);
        findViewById(R.id.iv_cut).setOnClickListener(this);
        findViewById(R.id.iv_cancel).setOnClickListener(this);

    }

    public void initCamera(int cameraPosition) {
        if (mRecorder != null) {
            mRecorder.stopRecording();
        }
        releaseCamera();
        int cameraId = cameraPosition == 1 ? CameraHelper.getDefaultCameraID() : CameraHelper.getFrontCameraID();
        mCamera = CameraHelper.getCameraInstance(cameraId);
        if (null == mCamera) {
            showSettingsDialog(this);
            return;
        }
        // 初始化录像机
        mRecorder = new WXLikeVideoRecorder(this, FileUtils.SD_MY_VIDEO, cameraPosition);
        mRecorder.setOutputSize(OUTPUT_WIDTH, OUTPUT_HEIGHT);
        CameraPreviewView preview = (CameraPreviewView) findViewById(R.id.camera_preview);
        preview.setCamera(mCamera, cameraId);
        mRecorder.setCameraPreviewView(preview);
        mRecorder.hideText();   //隐藏文字
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_cut:
                if (cameraPosition == 1) {
                    cameraPosition = 0;
                } else cameraPosition = 1;
                initCamera(cameraPosition);
                break;
            case R.id.iv_cancel:
                releaseClose();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseClose();
    }


    private void releaseClose() {
        if (mRecorder != null) {
            boolean recording = mRecorder.isRecording();
            mRecorder.stopRecording();          // 页面不可见就要停止录制
            if (recording) {  // 录制时退出，直接舍弃视频
                FileUtils.deleteFile(mRecorder.getFilePath());
            }
        }
        releaseCamera();
        finish();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();    // 释放前先停止预览
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    /**
     * 开始录制
     */
    private void startRecord() {
        if (mRecorder.isRecording()) {
            return;
        }
        if (prepareVideoRecorder()) {
            if (!mRecorder.startRecording())   // 录制视频
                ToastUtil.show("录制失败");
        }
    }

    /**
     * 准备视频录制器
     *
     * @return
     */
    private boolean prepareVideoRecorder() {
        if (!FileUtils.isSDCardMounted()) {
            ToastUtil.show("SD卡不可用");
            return false;
        }
        return true;
    }

    /**
     * 停止录制
     */
    private void stopRecord() {
        //停止录制视频
        mRecorder.stopRecording();
        //获取视频的时间长度
        LogUtils.e("视频时间长度：" + String.valueOf(mRecorder.getRecordTime() / 1000) + "秒");
        // ToastUtil.show(String.valueOf(mRecorder.getRecordTime()));
        //文件的路径
        String videoPath = mRecorder.getFilePath();
        //文件大小KB类型
        double fileSize = FileUtils.getFileOrFilesSize(videoPath, 2);
        // ToastUtil.show(String.valueOf(fileSize) + "KB");
        // 没超过2秒就删除录制的数据
        if (!isCancelRecord) {
            if (oldTime < 2000) {
                ToastUtil.show("录制时间太短");
                //取消录制，删除文件
                isCancelRecord = true;
            }
        }
        if (null == videoPath) {
            ToastUtil.show("保存地址失败");
            return;
        }
        // 若取消录制，则删除文件，否则通知宿主页面发送视频
        if (isCancelRecord) {
            oldTime = 0;               // 取消后恢复
            FileUtils.deleteFile(videoPath);
        } else {//录制成功返回视频路径
            // ToastUtil.show("视频路径：" + videoPath);
            Intent intent = new Intent();
            intent.putExtra("video_path", videoPath);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                tv_start_recorder.setVisibility(View.INVISIBLE);
                isCancelRecord = false;
                mDownX = event.getX();
                mDownY = event.getY();
                startRecord();
                mRecorder.changeText("↑ 上移取消", Color.WHITE);
                mRecorder.showText();    //显示文字
                mRecorder.hideProgressBar(false);     //显示progress
                timeCount = new TimeCount(8000 - oldTime, 50);     // 构造CountDownTimer对象   10000录制时间
                timeCount.start();// 开始计时
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mRecorder.isRecording())
                    return false;
                float y = event.getY();
                if (y - mDownY < CANCEL_RECORD_OFFSET) {
                    mRecorder.changeText("松开取消", Color.WHITE);   //改变文字
                    if (!isCancelRecord) {
                        isCancelRecord = true;   // cancel record
                    }
                } else {
                    isCancelRecord = false;
                    mRecorder.changeText("↑ 上移取消", Color.WHITE);
                }
                break;
            case MotionEvent.ACTION_UP:
                tv_start_recorder.setVisibility(View.VISIBLE);
                if (!isTimeOut) {
                    stopTouchEvent();
                }
                break;
        }
        return true;
    }

    /**
     * 结束录制的事务集合
     */
    private void stopTouchEvent() {
        //隐藏文字
        mRecorder.hideText();
        oldTime = nowTime + oldTime;
        if (oldTime >= VIDEO_TIME * 1000) {
            isMeet = true;
        }
        timeCount.cancel();
        stopRecord();
    }

    /**
     * 开始录制失败回调任务
     */
    public static class StartRecordFailCallbackRunnable implements Runnable {
        private WeakReference<VideoRecordActivity> mNewRecordVideoActivityWeakReference;

        public StartRecordFailCallbackRunnable(VideoRecordActivity activity) {
            mNewRecordVideoActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            VideoRecordActivity activity;
            if (null == (activity = mNewRecordVideoActivityWeakReference.get()))
                return;

            String filePath = activity.mRecorder.getFilePath();
            if (!TextUtils.isEmpty(filePath)) {
                FileUtils.deleteFile(filePath);
                ToastUtil.show("录制失败");
            }
        }
    }

    /**
     * 停止录制回调任务
     */
    public static class StopRecordCallbackRunnable implements Runnable {

        private WeakReference<VideoRecordActivity> mNewRecordVideoActivityWeakReference;

        public StopRecordCallbackRunnable(VideoRecordActivity activity) {
            mNewRecordVideoActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            VideoRecordActivity activity;
            if (null == (activity = mNewRecordVideoActivityWeakReference.get()))
                return;
            String filePath = activity.mRecorder.getFilePath();
            if (!TextUtils.isEmpty(filePath)) {
                if (activity.isCancelRecord) {
                    FileUtils.deleteFile(filePath);
                } else {
                    ToastUtil.show("视频路径:" + filePath);
                }
            }
        }
    }

    private class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {
            // 计时完毕时触发
            stopTouchEvent();
            isTimeOut = true;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // 计时过程显示
            nowTime = (int) (8000 - millisUntilFinished - oldTime);       //10000 录制时间
            LogUtils.e("倒计时nowTime=" + nowTime / 1000 + "秒");
            if (nowTime > 8000) {                                       //10000 录制时间
                LogUtils.e("倒计时nowTime的时间=" + nowTime / 1000 + "超过了10秒");
            }
            //条件：大于3秒，才提示，否则也会录制不成功，无需提示。
            if ((oldTime > 0 && oldTime > VIDEO_TIME * 1000) || (oldTime == 0 && nowTime > VIDEO_TIME * 1000)) {
            }
        }
    }
}
