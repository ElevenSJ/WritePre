package com.easier.writepre.inputmessage.recoder;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Handler;
import android.util.Log;

import com.easier.writepre.utils.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.SoftReference;


public class AudioMediaPlayer {

    public static int flag = 0;
    private static AudioMediaPlayer instance; //g
    private SoftReference<OnCompletionListener> onCompletionListener;
    private SoftReference<OnErrorListener> onErrorListener;
    private SoftReference<MediaPlayerOnProgressListener> onProgressListener;
    private SoftReference<MediaPlayerOnStopListener> onStopListener;
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private Runnable progress;
    private String path;

    public String getPath() {
        return path;
    }

    private AudioMediaPlayer() {
        handler = new Handler();
        progress = new Runnable() {

            @Override
            public void run() {
                if (onProgressListener != null && mediaPlayer != null && mediaPlayer.isPlaying()) {
                    MediaPlayerOnProgressListener lis = onProgressListener.get();
                    if (lis != null) {
                        lis.onProgress(mediaPlayer.getCurrentPosition(),
                                mediaPlayer.getDuration());
                    }
                    handler.postDelayed(progress, 50);
                }
            }
        };
        recoverRecorder();
    }

    public static AudioMediaPlayer getInstance() {//public static d a() {
        if (instance == null)
            instance = new AudioMediaPlayer();
        return instance;
    }

    private void recoverRecorder() { //e
        LogUtils.d(this.getClass(), "recoverRecorder");
        if (mediaPlayer != null)
            mediaPlayer.release();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(false);
        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                LogUtils.d(this.getClass(), "onCompletion");
                if (onCompletionListener != null) {
                    OnCompletionListener listener = onCompletionListener.get();
                    if (listener != null) {
                        listener.onCompletion(mediaPlayer);
                    }
                }
                stop();
            }
        });
        mediaPlayer.setOnErrorListener(new OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                LogUtils.d(this.getClass(), "onError");
                if (onErrorListener != null) {
                    OnErrorListener listener = onErrorListener.get();
                    if (listener != null) {
                        listener.onError(mp, what, extra);
                    }
                }
                recoverRecorder();
                return false;
            }
        });
    }

    public void seekTo(int seekTo) {// a()
        LogUtils.d(this.getClass(), "seekTo:" + seekTo);
        try {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AudioFocusChangeManager.abandonAudioFocus();
            AudioFocusChangeManager.unregisterListener();
            recoverRecorder();
        }
    }

    //public void a(MediaPlayer.OnCompletionListener listener)
    public void setOnCompletionListener(OnCompletionListener listener) {
        onCompletionListener = new SoftReference<OnCompletionListener>(listener);
    }

    public void setOnErrorListener(OnErrorListener listener) {// public void a(...)
        onErrorListener = new SoftReference<OnErrorListener>(listener);
    }

    public void setOnStopListener(MediaPlayerOnStopListener listener) {// public void a(...)
        onStopListener = new SoftReference<MediaPlayerOnStopListener>(listener);
    }

    public void setOnProgressListener(MediaPlayerOnProgressListener h1) { // public void a
        onProgressListener = new SoftReference<MediaPlayerOnProgressListener>(h1);
    }

    public void stopIfNoMe(MediaPlayerOnStopListener lis) {
        if (onStopListener != null && onStopListener.get() != lis) {
            onStopListener.get().onStop();
        }
    }

    public void play(String path) {//public void a(String s) {
        LogUtils.d(this.getClass(), "play:" + path);
        try {
            if (prepare(path) > 0L) {
                mediaPlayer.start();
                this.path = path;
                flag = 1;
                AudioFocusChangeManager.requestAudioFocus();
                AudioFocusChangeManager.checkVolume();
                AudioFocusChangeManager.registerListener();
                handler.post(progress);
            } else {
                stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AudioFocusChangeManager.abandonAudioFocus();
            AudioFocusChangeManager.unregisterListener();
            if (onErrorListener != null && onErrorListener.get() != null)
                onErrorListener.get().onError(mediaPlayer, -1, 0);
            recoverRecorder();
        }
    }

    public long prepare(String path) {//b
        LogUtils.d(this.getClass(), "prepare");
        try {
            if (mediaPlayer == null) {
                recoverRecorder();
            }

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }

            mediaPlayer.reset();
//            File file = new File(path);
//            if (!file.exists() || file.length() == 0L) {
//                throw new FileNotFoundException();
//            }
//
//            FileInputStream fis = new FileInputStream(file);
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
//            fis.close();
            return mediaPlayer.getDuration();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            AudioFocusChangeManager.abandonAudioFocus();
            AudioFocusChangeManager.unregisterListener();
            if (onErrorListener != null && onErrorListener.get() != null) {
                onErrorListener.get().onError(mediaPlayer, -2, 0);
            }
            recoverRecorder();
        } catch (Exception e) {
            e.printStackTrace();
            AudioFocusChangeManager.abandonAudioFocus();
            AudioFocusChangeManager.unregisterListener();
            if (onErrorListener != null && onErrorListener.get() != null) {
                onErrorListener.get().onError(mediaPlayer, -1, 0);
            }
            recoverRecorder();
        }
        return 0;
    }

    public void stop() { //public void b() {
        LogUtils.d(this.getClass(), "stop!!!");
        flag = 0;
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            path = null;
            handler.removeCallbacks(progress);
        } catch (Exception e) {
            e.printStackTrace();
            recoverRecorder();
        } finally {
            AudioFocusChangeManager.updateToNormalMode();
            AudioFocusChangeManager.abandonAudioFocus();
            AudioFocusChangeManager.unregisterListener();
        }
    }

    public void release() { //c
        LogUtils.d(this.getClass(), "release");
        stop();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        path = null;
        onCompletionListener = null;
        onErrorListener = null;

        if (onStopListener != null) {
            onStopListener.get().onStop();
        }
        onStopListener = null;
        instance = null;
    }

    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    public boolean isPlaying(String path) {
        return isPlaying() && path.equals(this.path);
    }
}
