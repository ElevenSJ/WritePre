package com.easier.writepre.fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easier.writepre.R;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.ui.CourseCatalogActivityNew;
import com.easier.writepre.ui.CurriculumInfoActivity;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.video.universalvideoview.UniversalMediaController;
import com.easier.writepre.video.universalvideoview.UniversalVideoView;

/**
 * 视频播放控件
 *
 * @author zhoulu
 */
public class VideoPlayFragment extends DialogFragment implements UniversalVideoView.VideoViewCallback {
    private View contextView;
    private static final String SEEK_POSITION_KEY = "SEEK_POSITION_KEY";
    private UniversalVideoView mVideoView;
    private UniversalMediaController mMediaController;
    private int mSeekPosition;
    private boolean isFullscreen;
    private Handler handler;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contextView = inflater.inflate(R.layout.fragment_videopaly, container, false);
        mVideoView = (UniversalVideoView) contextView.findViewById(R.id.videoView);
        mMediaController = (UniversalMediaController) contextView.findViewById(R.id.media_controller);
        mMediaController.setShowLeftView(false);
        mVideoView.setMediaController(mMediaController);
        mVideoView.requestFocus();
        mVideoView.setVideoViewCallback(this);
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //播放完成,通知获取学时
                if(handler!=null)
                {
                    handler.sendEmptyMessage(0);
                }
            }
        });
        return contextView;
    }

    public void setProgressEnable(boolean enable) {
        if (mMediaController != null) {
            mMediaController.setProgressEnable(enable);
        }
    }
    public void setHandler(Handler handler)
    {
        this.handler=handler;
    }
    /**
     * 视频播放
     *
     * @param url
     */
    public void playVideo(String url, String title) {
        if (TextUtils.isEmpty(url)) {
            ToastUtil.show("无效的播放地址!");
            return;
        }
        if (mVideoView != null) {
            if (url.startsWith("http")) {
                String currentUrl = WritePreApp.getProxy(getActivity()).getProxyUrl(url);
                mVideoView.setVideoPath(currentUrl);
            } else {
                mVideoView.setVideoPath(url);
            }
            if (mMediaController != null) {
                mMediaController.setTitle(TextUtils.isEmpty(title) ? "" : title);
            }
            mVideoView.requestFocus();
            mVideoView.start();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SEEK_POSITION_KEY, mSeekPosition);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null)
            mSeekPosition = savedInstanceState.getInt(SEEK_POSITION_KEY);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mVideoView != null && mSeekPosition > 0) {
            mVideoView.seekTo(mSeekPosition);
            if (!mVideoView.isPlaying()) {
                mVideoView.start();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mVideoView != null && mVideoView.isPlaying()) {
            mSeekPosition = mVideoView.getCurrentPosition();
            mVideoView.pause();
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    public void setFullscreen(boolean isFullscreen) {
        if (mVideoView != null) {
            mVideoView.setFullscreen(isFullscreen);
        }
    }

    @Override
    public void onScaleChange(boolean isFullscreen) {
        if (getActivity() instanceof CourseCatalogActivityNew) {
            ((CourseCatalogActivityNew) getActivity()).onScaleChange(isFullscreen);
        } else if (getActivity() instanceof CurriculumInfoActivity) {
            ((CurriculumInfoActivity) getActivity()).onScaleChange(isFullscreen);
        }
    }

    @Override
    public void onPause(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onStart(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onFinishVideoView() {

    }
}
