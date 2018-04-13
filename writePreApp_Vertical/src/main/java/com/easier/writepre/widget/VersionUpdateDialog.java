package com.easier.writepre.widget;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.entity.VersionInfo;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.sj.autolayout.AutoLayoutActivity;

import java.io.File;

/**
 * 版本更新
 *
 * @author zhaomaohan
 */
public class VersionUpdateDialog extends Dialog implements android.view.View.OnClickListener {
    TextView tv_version;
    TextView tv_content;
    Button btn_cancel;
    Button btn_update;
    Context context;
    VersionInfo versionInfo;

    public static final String DOWNLOAD_SIZE = "com.easier.writepre.downloadprogress";
    public static final String DOWNLOAD_COMPLETE = "com.easier.writepre.downloadcomplete";
    public static final String DOWNLOAD_FAIL = "com.easier.writepre.downloadfail";


    private String  APK_TMP_NAME;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DOWNLOAD_SIZE)) {
                String size = intent.getStringExtra("size");
                String sizeTotal = intent.getStringExtra("sizeTotal");
                btn_cancel.setVisibility(View.GONE);
                btn_update.setText("已下载:" + FileUtils.getFormatSize(Long.parseLong(size)) + "/"
                        + FileUtils.getFormatSize(Long.parseLong(sizeTotal)));
                btn_update.setEnabled(false);
            } else if (action.equals(DOWNLOAD_COMPLETE)) {
                btn_cancel.setVisibility(View.VISIBLE);
                btn_update.setText("立即安装");
                btn_update.setEnabled(true);
            } else if (action.equals(DOWNLOAD_FAIL)) {
                btn_cancel.setVisibility(View.VISIBLE);
                btn_update.setText("再试一次");
                btn_update.setEnabled(true);
            }
        }

    };

    public VersionUpdateDialog(Context context, int theme, VersionInfo versionInfo) {
        super(context, theme);
        this.context = context;
        this.versionInfo = versionInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_version_update);

        initView();
    }

    private void initView() {

        APK_TMP_NAME = System.currentTimeMillis()+"";

        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version.setText(versionInfo.getVersion());
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_content.setText(versionInfo.getUp_reason());

        btn_update = (Button) findViewById(R.id.btn_update);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        btn_update.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        if (versionInfo.getIs_must() == 1) {
            if (FileUtils.fileIsExists(FileUtils.SD_APP_PATH,
                    APK_TMP_NAME + "-" + versionInfo.getLevel())) {
                btn_update.setText("免流量安装");
                btn_cancel.setVisibility(View.GONE);
            } else
                btn_update.setText("立即升级");
            setCanceledOnTouchOutside(false);
            setOnKeyListener(new OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        return true;
                    }
                    return false;
                }
            });
        } else if (versionInfo.getIs_must() == 2) {
            if (FileUtils.fileIsExists(FileUtils.SD_APP_PATH,
                    APK_TMP_NAME + "-" + versionInfo.getLevel())) {
                btn_update.setText("免流量安装");
                btn_cancel.setVisibility(View.GONE);
            } else
                btn_update.setText("建议升级");
        } else {
            if (FileUtils.fileIsExists(FileUtils.SD_APP_PATH,
                    APK_TMP_NAME + "-" + versionInfo.getLevel())) {
                btn_update.setText("免流量安装");
                btn_cancel.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub
        super.show();
//        registerBoradcastReceiver();
    }

    public void registerBoradcastReceiver() {
        // 注册广播
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(DOWNLOAD_SIZE);
        myIntentFilter.addAction(DOWNLOAD_COMPLETE);
        myIntentFilter.addAction(DOWNLOAD_FAIL);
        context.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void dismiss() {
        // TODO Auto-generated method stub
        super.dismiss();
//        context.unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                setCanceledOnTouchOutside(false);
                setOnKeyListener(new OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            return true;
                        }
                        return false;
                    }
                });
                if (FileUtils.fileIsExists(FileUtils.SD_APP_PATH,
                        APK_TMP_NAME + "-" + versionInfo.getLevel())) {
                    FileUtils.installApk(context, FileUtils.SD_APP_PATH
                            + APK_TMP_NAME + "-" + versionInfo.getLevel());
                } else {
//                    FileUtils.downloadApk(context, versionInfo.getVersion(), versionInfo.getFile_url(),
//                            APK_TMP_NAME + "-" + versionInfo.getLevel());
                    new HttpUtils().download(versionInfo.getFile_url(), FileUtils.SD_APP_PATH + APK_TMP_NAME + "-" + versionInfo.getLevel() + "tmp", true, false,
                            new RequestCallBack<File>() {
                                @Override
                                public void onLoading(long total, long current, boolean isUploading) {
                                    super.onLoading(total, current, isUploading);
                                    btn_cancel.setVisibility(View.GONE);
                                    btn_update.setText("已下载:" + FileUtils.getFormatSize(Long.parseLong(current + "")) + "/"
                                            + FileUtils.getFormatSize(Long.parseLong(total + "")));
                                    btn_update.setEnabled(false);
                                }

                                @Override
                                public void onSuccess(ResponseInfo<File> responseInfo) {
                                    new File(FileUtils.SD_APP_PATH + APK_TMP_NAME + "-" + versionInfo.getLevel() + "tmp")
                                            .renameTo(new File(FileUtils.SD_APP_PATH,
                                                    APK_TMP_NAME + "-" + versionInfo.getLevel()));
                                    btn_cancel.setVisibility(View.VISIBLE);
                                    btn_update.setText("立即安装");
                                    btn_update.setEnabled(true);
                                    FileUtils.installApk(context, FileUtils.SD_APP_PATH
                                            + APK_TMP_NAME + "-" + versionInfo.getLevel());
                                }

                                @Override
                                public void onFailure(HttpException e, String s) {
                                    FileUtils.deleteFile(FileUtils.SD_APP_PATH + APK_TMP_NAME + "-" + versionInfo.getLevel() + "tmp");
                                    ToastUtil.show("下载失败!");
                                    btn_cancel.setVisibility(View.VISIBLE);
                                    btn_update.setText("再试一次");
                                    btn_update.setEnabled(true);
                                }
                            });
                }
                break;
            case R.id.btn_cancel:
                dismiss();
                if (versionInfo.getIs_must() == 1) {
                    ((AutoLayoutActivity) context).finish();
                }
                break;
            default:
                break;
        }

    }
}
