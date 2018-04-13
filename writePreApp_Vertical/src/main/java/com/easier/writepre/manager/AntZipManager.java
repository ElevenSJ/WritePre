package com.easier.writepre.manager;

import android.os.AsyncTask;

import com.easier.writepre.utils.AntZipUtils;
import com.easier.writepre.utils.LogUtils;

/**
 * zip 压缩/解压缩管理类
 * Created by zhoulu on 2017/8/17.
 */

public class AntZipManager {
    private static AntZipManager antZipManager;
    private MakeZipTask makeZipTask;//生成zip文件的异步请求类
    private UnZipTask unZipTask;//解压zip文件的异步请求类

    private AntZipManager() {

    }

    public synchronized static AntZipManager getInstance() {
        if (antZipManager == null) {
            antZipManager = new AntZipManager();
        }
        return antZipManager;
    }

    public MakeZipTask getMakeZipTask() {
        return makeZipTask;
    }

    public UnZipTask getUnZipTask() {
        return unZipTask;
    }

    /**
     * 压缩成zip
     *
     * @param localDir       本地需要压缩的文件夹路径 例如 xxx/dir
     * @param zipPath        生成的zip文件路径 例如 xxx/ant.zip
     * @param antZipCallBack 回调接口
     */
    public void makeZipTask(String localDir, String zipPath, AntZipCallBack antZipCallBack) {
        //生成ZIP压缩包【异步执行】
        makeZipTask = new MakeZipTask(antZipCallBack);
        makeZipTask.execute(localDir, zipPath);
    }

    /**
     * 解压缩zip
     *
     * @param localZipPath   本地需要解压缩的zip文件路径 例如 xxx/ant.zip
     * @param localDirPath   zip解压后存放的目录路径 例如: xxx/dir
     * @param antZipCallBack 回调接口
     */
    public void unZipTask(String localZipPath, String localDirPath, AntZipCallBack antZipCallBack) {
        //解压ZIP包【异步执行】
        unZipTask = new UnZipTask(antZipCallBack);
        unZipTask.execute(localZipPath, localDirPath);
    }

    /**
     * 压缩文件的异步请求任务
     */
    public class MakeZipTask extends AsyncTask<String, Void, String> {
        AntZipCallBack antZipCallBack;

        protected MakeZipTask(AntZipCallBack antZipCallBack) {
            this.antZipCallBack = antZipCallBack;
        }

        @Override
        protected void onPreExecute() {
            LogUtils.e("正在压缩...");
        }

        @Override
        protected String doInBackground(String... params) {
            String data = "";
            if (!isCancelled()) {
                try {
                    String[] srcFilePaths = new String[1];
                    srcFilePaths[0] = params[0];
                    String zipPath = params[1];
                    AntZipUtils.makeZip(srcFilePaths, zipPath);
                    data = zipPath;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (isCancelled()) {
                return;
            }
            try {
            } catch (Exception e) {
                if (!isCancelled()) {
                    LogUtils.e("文件压缩失败");
                }
            } finally {
                if (!isCancelled()) {
                    LogUtils.e("压缩完成");
                    if (this.antZipCallBack != null) {
                        antZipCallBack.onAntZipMaskZipCallback(result);
                    }
                }
            }
        }
    }

    /**
     * 解压文件的异步请求任务
     */
    public class UnZipTask extends AsyncTask<String, Void, String> {
        AntZipCallBack antZipCallBack;

        protected UnZipTask(AntZipCallBack antZipCallBack) {
            this.antZipCallBack = antZipCallBack;
        }

        @Override
        protected void onPreExecute() {
            LogUtils.e("正在解压");
        }

        @Override
        protected String doInBackground(String... params) {
            String data = "";
            if (!isCancelled()) {
                try {
                    String zipPath = params[0];
                    String targetDirPath = params[1];
                    AntZipUtils.unZip(zipPath, targetDirPath);
                    data = targetDirPath;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (isCancelled()) {
                return;
            }
            try {
                LogUtils.e("result=" + result);
            } catch (Exception e) {
                if (!isCancelled()) {
                    LogUtils.e("文件解压失败");
                }
            } finally {
                if (!isCancelled()) {
                    LogUtils.e("解压完成");
                    if (this.antZipCallBack != null) {
                        antZipCallBack.onAntZipUnZipCallback(result);
                    }
                }
            }
        }
    }


    public interface AntZipCallBack {
        void onAntZipMaskZipCallback(String result);

        void onAntZipUnZipCallback(String result);
    }
}
