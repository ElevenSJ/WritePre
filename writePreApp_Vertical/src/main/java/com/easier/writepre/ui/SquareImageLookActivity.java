package com.easier.writepre.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.easier.writepre.R;
import com.easier.writepre.fragment.SquareAllEssenceImageDetailFragment;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.utils.Utils;
import com.easier.writepre.widget.SquareImageLookViewPager;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 广场（全部、精华点击照片大图查看）
 *
 * @author kai.zhong
 */
public class SquareImageLookActivity extends BaseActivity {

    private String[] urls;

    private int pagerPosition;

    private TextView indicator;

    private RelativeLayout downLoadLayout;
    private TextView downLoadProTxt;
    private ImageView downLoadCancle;

    // private String IMAGE_SUFFIX;

    private Button fab;

    private boolean flag; // true表示查看个人头像大图

    private SquareImageLookViewPager mPager;

    private ImagePagerAdapter mAdapter;

    public static final String EXTRA_IMAGE_URLS = "image_urls";

    public static final String EXTRA_IMAGE_INDEX = "image_index";

    private static final String STATE_POSITION = "STATE_POSITION";

    private int maxDownloadThread = 3;
    private HttpHandler<File> handler;
    private HttpUtils http;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail_pager);

        http = new HttpUtils();
        http.configRequestThreadPoolSize(maxDownloadThread);

        pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
        urls = getIntent().getStringArrayExtra(EXTRA_IMAGE_URLS); // xzpapp/usr/5680e376e4b018200af616fa/image/1459233283752_0.png
        flag = getIntent().getBooleanExtra("flag", false);
        mPager = (SquareImageLookViewPager) findViewById(R.id.pager);
        mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), urls);
        mPager.setAdapter(mAdapter);
        indicator = (TextView) findViewById(R.id.indicator);
        if (flag) {
            indicator.setVisibility(View.GONE);
        } else {
            indicator.setVisibility(View.VISIBLE);
        }
        fab = (Button) findViewById(R.id.fab);

        downLoadLayout = (RelativeLayout) findViewById(R.id.down_layout);
        downLoadProTxt = (TextView) findViewById(R.id.download_txt);
        downLoadCancle = (ImageView) findViewById(R.id.down_cancle);

        downLoadProTxt.setOnClickListener(this);
        downLoadCancle.setOnClickListener(this);
        downLoadLayout.bringToFront();
        String fileName = "";
        if (urls[0].startsWith("http://rong")) {
            fileName = StringUtil.getMD5(urls[0]);
            downLoadLayout.setVisibility(View.GONE);
        } else {
            fileName = urls[0].substring(urls[0].lastIndexOf("/") + 1);
        }

        if (FileUtils.fileIsExists(FileUtils.SD_IMAGES_PATH, fileName)) {
            downLoadLayout.setVisibility(View.GONE);
        }

        CharSequence text = getString(R.string.viewpager_indicator, 1, mPager.getAdapter().getCount());
        indicator.setText(text);
        // 更新下标
        mPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int arg0) {
                CharSequence text = getString(R.string.viewpager_indicator, arg0 + 1, mPager.getAdapter().getCount());
                indicator.setText(text);
                pagerPosition = arg0;
                String fileName = "";
                if (urls[arg0].startsWith("http://rong")) {
                    fileName = StringUtil.getMD5(urls[arg0]);
                } else {
                    fileName = urls[arg0].substring(urls[arg0].lastIndexOf("/") + 1);
                }
                if (FileUtils.fileIsExists(FileUtils.SD_IMAGES_PATH, fileName)) {
                    downLoadLayout.setVisibility(View.GONE);
                } else {
                    downLoadLayout.setVisibility(View.VISIBLE);
                }
                downLoadCancle.setVisibility(View.INVISIBLE);
                downLoadProTxt.setText("查看原图");
                if (handler != null) {
                    handler.cancel();
                }
            }

        });
        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }

        mPager.setCurrentItem(pagerPosition);

        // fab.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        //
        // String imageNet_Path = urls != null && urls.length > 0 ?
        // urls[pagerPosition] : ""; //
        // xzposs/569c463244f4f14f39c3d678/image/1453435131294_0.png
        //
        // if (imageNet_Path.equals("")) {
        // return;
        // }
        // final String fileName =
        // imageNet_Path.substring(imageNet_Path.lastIndexOf("/") + 1);
        //
        // if (FileUtils.fileIsExists(FileUtils.SD_IMAGES_PATH, fileName)) {
        // insertToDB(FileUtils.SD_IMAGES_PATH, fileName);
        // } else {
        // new HttpUtils().download(StringUtil.getImgeUrl(urls[pagerPosition]),
        // FileUtils.SD_IMAGES_PATH + fileName + "tmp", true, true, new
        // RequestCallBack<File>() {
        //
        // @Override
        // public void onLoading(long total, long current, boolean isUploading)
        // {
        // // TODO Auto-generated method stub
        // super.onLoading(total, current, isUploading);
        // }
        //
        // @Override
        // public void onSuccess(ResponseInfo<File> arg0) {
        // new File(FileUtils.SD_IMAGES_PATH + fileName + "tmp")
        // .renameTo(new File(FileUtils.SD_IMAGES_PATH + fileName));
        // insertToDB(FileUtils.SD_IMAGES_PATH, fileName);
        // }
        //
        // @Override
        // public void onFailure(HttpException arg0, String arg1) {
        // String tip = arg1.contains("completely") ? "已存在该文件" : "保存失败";
        // ToastUtil.show(tip);
        // LogUtils.e("image-sava-info", arg1);
        // if (!arg1.contains("completely")) {
        // new File(FileUtils.SD_IMAGES_PATH + fileName + "tmp").delete();
        // }
        // }
        // });
        // }
        //
        // }
        // });
    }

    protected void insertToDB(String filePath, String fileName) {
        try {
            MediaStore.Images.Media.insertImage(this.getContentResolver(), filePath + fileName, fileName, null);
        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            // 最后通知图库更新
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath + fileName)));
        }
    }

    protected void downLoadImage(String url) {
        String fileName = "";
        if (url.startsWith("http://rong")) {
            fileName = StringUtil.getMD5(url);
        } else {
            fileName = url.substring(url.lastIndexOf("/") + 1);
        }
        if (FileUtils.fileIsExists(FileUtils.SD_IMAGES_PATH, fileName)) {
            SquareAllEssenceImageDetailFragment f = (SquareAllEssenceImageDetailFragment) mAdapter
                    .instantiateItem(mPager, pagerPosition);
            f.loadLocalImag(FileUtils.SD_IMAGES_PATH + fileName);
            downLoadLayout.setVisibility(View.GONE);
            downLoadCancle.setVisibility(View.INVISIBLE);
            downLoadProTxt.setText("查看原图");
        } else {
            handler = http.download(url, FileUtils.SD_IMAGES_PATH + fileName + "tmp", true, false,
                    new DownloadRequestCallBack(fileName));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, mPager.getCurrentItem());
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        public String[] fileList;

        public ImagePagerAdapter(FragmentManager fm, String[] fileList) {
            super(fm);
            this.fileList = fileList;
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO Auto-generated method stub
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return fileList == null ? 0 : fileList.length;
        }

        @Override
        public Fragment getItem(int position) {
            return SquareAllEssenceImageDetailFragment.newInstance(fileList[position]);
        }

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        switch (v.getId()) {
            case R.id.download_txt:
                String netUrl = "";
                if (urls[pagerPosition].startsWith("http://rong")) {
                    netUrl = StringUtil.getMD5(urls[pagerPosition]);
                } else {
                    netUrl = StringUtil.getImgeUrl(urls[pagerPosition]);
                }
                downLoadImage(netUrl);
                break;
            case R.id.down_cancle:
                if (handler != null) {
                    handler.cancel();
                }
                downLoadCancle.setVisibility(View.INVISIBLE);
                downLoadProTxt.setText("查看原图");
                break;

            default:
                break;
        }
    }

    private class DownloadRequestCallBack extends RequestCallBack<File> {
        private String fileName;

        public DownloadRequestCallBack(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public void onStart() {

        }

        @Override
        public void onLoading(long total, long current, boolean isUploading) {
            if (!isUploading) {
                setProgress(current, total);
            }
        }

        @Override
        public void onFailure(HttpException error, String msg) {
            FileUtils.deleteFile(FileUtils.SD_IMAGES_PATH + fileName + "tmp");
            ToastUtil.show("下载失败!");
        }

        @Override
        public void onSuccess(ResponseInfo<File> responseInfo) {
            new File(FileUtils.SD_IMAGES_PATH + fileName + "tmp")
                    .renameTo(new File(FileUtils.SD_IMAGES_PATH + fileName));
            SquareAllEssenceImageDetailFragment f = (SquareAllEssenceImageDetailFragment) mAdapter
                    .instantiateItem(mPager, pagerPosition);
            f.loadLocalImag(FileUtils.SD_IMAGES_PATH + fileName);
            if (new File(FileUtils.SD_IMAGES_PATH + fileName).length() < 3 * 1024 * 1024f) {
                insertToDB(FileUtils.SD_IMAGES_PATH, fileName);
            }
        }
    }

    public void setProgress(long current, long total) {
        downLoadCancle.setVisibility(View.VISIBLE);
        if (total > 0) {
            LogUtils.e("图片下载：" + current + "/" + total + ";" + Utils.accuracy(current, total, 0));
            downLoadProTxt.setText(Utils.accuracy(current, total, 0));
        }
        if (current == total) {
            downLoadLayout.setVisibility(View.GONE);
            downLoadCancle.setVisibility(View.INVISIBLE);
            downLoadProTxt.setText("查看原图");
        }
    }

}