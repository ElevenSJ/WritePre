package com.easier.writepre.inputmessage.recoder;

import android.media.MediaRecorder;
import android.os.Handler;
import android.text.TextUtils;

import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.utils.LogUtils;

import java.io.IOException;


public class AudioMediaRecorder {

    private int state = 0;
    private MediaRecorder mediaRecorder;
    private long startTime;
    private long endTime;
    private Handler handler = new Handler(WritePreApp.getApp().getApplication().getMainLooper());
    private MediaRecorderOnProgressListener onProgressListener;
    private MediaRecorderOnErrorListener onErrorListener;
    private boolean isRecording = false;
    private double maxRecordTime = 60*1000;
    private Runnable progress = new Runnable() {

        @Override
        public void run() {
            long pro = System.currentTimeMillis() - startTime;// / 1000.0D;
            if (onProgressListener != null) {
                onProgressListener.onProgress(pro);
            }
            if (maxRecordTime > 0.0D && pro > maxRecordTime) {
                stop();
                handleStateError(3, WritePreApp.getApp().getApplication().getString(R.string.recordfinish));
            }
            if (isRecording) {
                handler.postDelayed(progress, 1000);
            }
        }
    };

    public AudioMediaRecorder() {
        recoverRecorder();
    }

    private void handleStateError(int i1, String message) {//a
        state = 5;
        if (onErrorListener != null) {
            onErrorListener.onError(i1, message);
        }
    }

    private void recoverRecorder() {//g
        if (state != 0 && state != 5) {
            LogUtils.e("recoverRecorder  can be recovered only at initialization or Error");
        } else {
            release();
            state = 0;
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {

                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    state = 5;
                    if (onErrorListener != null) {
                        onErrorListener.onError(mr, what, extra);
                    }
                    recoverRecorder();
                }
            });
        }
    }

    private void startCountRecordTime() {
        isRecording = true;
        startTime = System.currentTimeMillis();
        handler.postDelayed(progress, 0);
    }

    private void stopCountRecordTime() {
        isRecording = false;
        endTime = System.currentTimeMillis();
        handler.removeCallbacks(progress);
    }

    public void stop() {//
        if (mediaRecorder != null && state != 0 && state != 3 && state != 5) {
            try {
                stopCountRecordTime();
                mediaRecorder.stop();
                state = 3;
            } catch (Exception e) {
                e.printStackTrace();
                handleStateError(0, WritePreApp.getApp().getApplication().getString(R.string.stopRecordFail));
            } finally {
                AudioFocusChangeManager.abandonAudioFocus();
            }
        }
    }

    public void setMaxRecordTime(double d1) {//
        maxRecordTime = d1;
    }

    public void setOnErrorListener(MediaRecorderOnErrorListener l1) {//a
        onErrorListener = l1;
    }

    public void setOnProgressListener(MediaRecorderOnProgressListener m) {
        onProgressListener = m;
    }

    public void start(String filePath) {//
        if (mediaRecorder != null) {
            int initState = state;
            int retryCount = 0;
            while (true) {
                try {
                    AudioFocusChangeManager.abandonAudioFocus();
                    if (state != 1) {
                        prepare(filePath);
                    }
                    AudioFocusChangeManager.requestAudioFocus();
                    mediaRecorder.start();
                    state = 2;
                    startCountRecordTime();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    if (retryCount >= 1) {
                        AudioFocusChangeManager.abandonAudioFocus();
                        handleStateError(0, WritePreApp.getApp().getApplication().getString(R.string.recordError));
                        break;
                    }
                    state = initState;
                    ++retryCount;
                }
            }
        }
    }

    public void reset() {//
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            endTime = 0;
            startTime = 0;
            stopCountRecordTime();
            handler.removeCallbacks(progress);
        }
    }

    public void prepare(String outputFile) throws IOException {
        if (mediaRecorder != null) {
            if (TextUtils.isEmpty(outputFile))
                throw new IOException();
            try {
                reset();
                // 设置meidaRecorder的音频源是麦克风
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                // 设置文件音频的输出格式为amr
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                // 设置音频的编码格式为amr
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mediaRecorder.setOutputFile(outputFile);
                mediaRecorder.prepare();
                state = 1;
            } catch (IllegalStateException e) {
                e.printStackTrace();
                handleStateError(0, WritePreApp.getApp().getApplication().getString(R.string.prepardError));
                recoverRecorder();
            } catch (IOException e) {
                e.printStackTrace();
                handleStateError(2, WritePreApp.getApp().getApplication().getString(R.string.storageError));
                recoverRecorder();
            } catch (Exception e) {
                e.printStackTrace();
                handleStateError(2, WritePreApp.getApp().getApplication().getString(R.string.storageError));
                recoverRecorder();
            }
        }
    }

    public void release() {//
        reset();
        if (mediaRecorder != null) {
            mediaRecorder.release();
            state = 4;
        }
        onErrorListener = null;
        onProgressListener = null;
    }

    public long getRecordTime() {//
        if (startTime == 0.0)
            startTime = System.currentTimeMillis();
        if (endTime == 0.0)
            endTime = System.currentTimeMillis();
        return Math.max(0, (endTime - startTime));// /1000
    }

    public int getMaxAmplitude() {//
        try {
            return mediaRecorder.getMaxAmplitude();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
