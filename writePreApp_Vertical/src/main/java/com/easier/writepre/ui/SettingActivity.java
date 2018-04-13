package com.easier.writepre.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.easier.writepre.BuildConfig;
import com.easier.writepre.R;
import com.easier.writepre.adapter.MyListAdapter;
import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.entity.MyListData;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.LoginOutParams;
import com.easier.writepre.param.VersionParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.VersionResponse;
import com.easier.writepre.utils.DataCleanUtils;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.utils.Utils;
import com.easier.writepre.widget.ConfirmHintDialog;
import com.easier.writepre.widget.ConfirmHintDialog.ConfirmHintDialogListener;
import com.easier.writepre.widget.VersionUpdateDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 设置页面
 *
 * @author zhaomaohan
 */
public class SettingActivity extends BaseActivity implements OnItemClickListener {
    private ListView lv_list;
    private List<MyListData> list;
    public static String left_text[] = new String[]{"修改密码", "清除缓存", "关于写字派", "版本更新"};
    public static String right_text[] = new String[]{null, null, null, "已经是最新版本"};
    // public static int right_image[] = new int[] { -1, -1, R.drawable.bg_off,
    // -1, -1, -1, -1};
    private int count;// 声音 ：0关闭，1开启
    private MyListAdapter adapter;

    private int newVersionCode = 0;
    private String newVersion = "";
    private String url;
    //	private VersionHintDialog dlg;
    private Button btn_logout;

    private ArrayList<String> changeUrl = new ArrayList<>();

    //现网、内网切换密令
    private String changeTag = "1011010";
    //日志输出切换密令
    private String logChangeTag = "1111010";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_my_setting);
        initView();
    }

    private void initView() {
        setTopTitle("设置");

//		dlg = new VersionHintDialog(this);
//		dlg.setOnClickListener(this);

        requestVersion();
        list = getData();
        lv_list = (ListView) findViewById(R.id.lv_list);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
            btn_logout.setVisibility(View.VISIBLE);
        } else {
            btn_logout.setVisibility(View.GONE);
        }
        adapter = new MyListAdapter(this, list);
        lv_list.setAdapter(adapter);
        lv_list.setOnItemClickListener(this);
        findViewById(R.id.left_view).setOnClickListener(this);
        findViewById(R.id.right_view).setOnClickListener(this);

        showDataCache();
    }

    /**
     * 显示缓存大小
     */
    private void showDataCache() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    File file = new File(FileUtils.SD_IMAGES_CACHE);
                    final String cacheSize = DataCleanUtils.getCacheSize(file);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            right_text[1] = cacheSize;
                            adapter.setData(getData());
                        }
                    });
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 获取版本升级
     */
    private void requestVersion() {
        // 获取版本升级
        RequestManager.request(this, new VersionParams(), VersionResponse.class, this, Constant.URL);
    }

    /**
     * 获取动态数组数据 可以由其他地方传来(json等)
     */
    private List<MyListData> getData() {
        List<MyListData> list = new ArrayList<MyListData>();
        for (int i = 0; i < left_text.length; i++) {
            MyListData myListData = new MyListData(-1, left_text[i], -1, right_text[i], null);
            list.add(myListData);
        }
        return list;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                Intent intent0 = new Intent(SettingActivity.this, ModifyPasswordActivity.class);
                startActivity(intent0);
                break;
            case 1:
                // BitmapHelp.getBitmapUtils().clearCache();
                clearCache();
                // ToastUtil.show("缓存内容已清除");
                break;
            case 2:
//			Intent intent4 = new Intent(SettingActivity.this, WebViewActivity.class);
//			intent4.putExtra("url", "http://m.xiezipai.com/apphtml/index.html");
//			intent4.putExtra("title", "关于写字派");
                Intent intent4 = new Intent(SettingActivity.this, AboutActivity.class);
                startActivity(intent4);
                break;

            case 3:
                if (right_text[3].equals("应用有更新")) {
                    // dlg.show();
                    VersionUpdateDialog dialog = new VersionUpdateDialog(SettingActivity.this, R.style.loading_dialog,
                            resp.getRepBody());
                    dialog.show();
                } else {
                    ToastUtil.show(right_text[3]);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 清除缓存
     */
    private void clearCache() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    //dlgLoad.loading();
                    WritePreApp.getApp().clearBitmapCache();
                    DataCleanUtils.deleteFolderFile(FileUtils.SD_IMAGES_CACHE, false);
                    DataCleanUtils.deleteFolderFile(FileUtils.SD_APP_PATH, false);
                    // File file = new File(FileUtils.SD_IMAGES_CACHE);
                    // final String newCacheSize = DataCleanUtils
                    // .getCacheSize(file);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            //dlgLoad.dismissDialog();
                            ToastUtil.show("清除缓存完成");
                            right_text[1] = "已清空";
                            adapter.setData(getData());
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    VersionResponse resp;

    @Override
    public void onResponse(BaseResponse response) {
        if (response instanceof VersionResponse) {
            if ("0".equals(response.getResCode())) {
                resp = (VersionResponse) response;
                if (resp != null && resp.getRepBody() != null) {
                    if (Utils.getCurrentCode(this) < resp.getRepBody().getLevel()) {
                        newVersionCode = resp.getRepBody().getLevel();
                        newVersion = resp.getRepBody().getVersion();
                        url = resp.getRepBody().getFile_url();
                        right_text[3] = "应用有更新";
                        list = getData();
                        adapter.setData(list);
                    }
                }
            } else {
                ToastUtil.show(response.getResMsg());
            }
        }
    }

    @Override
    public void
    onClick(View v) {
        switch (v.getId()) {
            case R.id.left_view:
                changeUrl.add("0");
                LogUtils.e("内外网环境切换：0");
                if (changeUrl.size() == 7) {
                    LogUtils.e("内外网环境切换：标示" + TextUtils.join("", changeUrl.toArray()));
                    if (TextUtils.join("", changeUrl.toArray()).equals(changeTag)) {
                        toMainActivity();
                    } else if (TextUtils.join("", changeUrl.toArray()).equals(logChangeTag)) {
                        LogUtils.setDebug(!LogUtils.isDebug);
                        SPUtils.instance().put("log_debug", !(boolean) SPUtils.instance().get("log_debug", BuildConfig.LOG_DEBUG));
                        ToastUtil.show("日志切换成功");
                    } else {
                        ToastUtil.show("失败");
                        changeUrl.clear();
                    }
                } else if (changeUrl.size() > 7) {
                    changeUrl.clear();
                }
                break;
            case R.id.right_view:
                changeUrl.add("1");
                LogUtils.e("内外网环境切换：1");
                if (changeUrl.size() == 7) {
                    LogUtils.e("内外网环境切换：标示" + TextUtils.join("", changeUrl.toArray()));
                    if (TextUtils.join("", changeUrl.toArray()).equals(changeTag)) {
                        toMainActivity();
                    } else if (TextUtils.join("", changeUrl.toArray()).equals(logChangeTag)) {
                        LogUtils.setDebug(!LogUtils.isDebug);
                        SPUtils.instance().put("log_debug", !(boolean) SPUtils.instance().get("log_debug", BuildConfig.LOG_DEBUG));
                        ToastUtil.show("日志切换成功");
                    } else {
                        ToastUtil.show("失败");
                        changeUrl.clear();
                    }
                } else if (changeUrl.size() > 7) {
                    changeUrl.clear();
                }
                break;
            case R.id.btn_logout:
                ConfirmHintDialog dialog = new ConfirmHintDialog(this, R.style.loading_dialog, "确定退出",
                        new ConfirmHintDialogListener() {

                            @Override
                            public void OnClick(View view) {
                                switch (view.getId()) {
                                    case R.id.tv_confirm:
                                        RequestManager.request(SettingActivity.this, new LoginOutParams(), BaseResponse.class,
                                                SettingActivity.this, Constant.URL);
                                        SPUtils.instance().unLogin();
                                        btn_logout.setVisibility(View.GONE);
                                        ToastUtil.show("您已退出登录");
                                        setResult(RESULT_OK);
                                        finish();
                                        break;
                                    case R.id.tv_cancel:
                                        break;

                                    default:
                                        break;
                                }
                            }
                        });
                dialog.show();
            default:
                break;
        }
    }

    public void toMainActivity() {
        SPUtils.instance().put("debug", !(boolean) SPUtils.instance().get("debug", BuildConfig.DEBUG));

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("isFinish", true);
        startActivity(intent);
        finish();
    }
//	/**
//	 * 下载apk
//	 * @param url
//	 */
//	private void downloadApk(String url) {
//		final String apkPath = FileUtils.SD_APP_PATH + newVersionCode + "_" + FileUtils.SYSTEM_VERSION_NAME;
//		if (new File(apkPath).exists()) {
//			installAPk(apkPath);
//			return;
//		}
//		final String tmpfileName = System.currentTimeMillis() + ".tmp";
//		dlgLoad.setDialogCancelable(false);
//		new HttpUtils().download(url, FileUtils.SD_APP_PATH + tmpfileName, true, true, new RequestCallBack<File>() {
//
//			private boolean isSuccess = false;
//
//			@Override
//			public void onLoading(long total, long current, boolean isUploading) {
//				if (!isUploading) {
//					dlgLoad.loading("正在下载：" + FileUtils.getFormatSize(current) + "/" + FileUtils.getFormatSize(total));
//					if (current == total) {
//						isSuccess = true;
//					}
//				}
//			}
//
//			@Override
//			public void onFailure(HttpException error, String msg) {
//				FileUtils.deleteFile(FileUtils.SD_APP_PATH + tmpfileName);
//				dlgLoad.dismissDialog();
//				ToastUtil.show("下载失败，请重试！");
//			}
//
//			@Override
//			public void onSuccess(ResponseInfo<File> responseInfo) {
//				dlgLoad.dismissDialog();
//				new File(FileUtils.SD_APP_PATH + tmpfileName).renameTo(new File(apkPath));
//				installAPk(apkPath);
//			}
//		});
//
//	}
//
//	protected void installAPk(String path) {
//		FileUtils.installApk(this, path);
//	}
}
