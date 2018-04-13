package com.easier.writepre.ui;

import java.util.ArrayList;

import com.easier.writepre.R;
import com.easier.writepre.fragment.ImageDetailFragment;
import com.easier.writepre.utils.Bimp;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.SquareImageLookViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageDetailActivity extends BaseActivity {

    public static final int TYPE_VIEW = 0;
    public static final int TYPE_PREVIEW = 1;
    public static final int TYPE_CAMERA = 2;
    public static final int TYPE_DELETE = 3;
    public static final int REQUEST_CODE_VIEW = 100;
    public static final int REQUEST_CODE_PREVIEW = 100;
    public static final int REQUEST_CODE_CAMERA = 200;
    public static final int REQUEST_CODE_DELETE = 300;

    private int pagerPosition = 0;

    private TextView indicator, txtOk;

    private CheckBox okCheck;

    private SquareImageLookViewPager mPager;

    private ArrayList<String> mFiles = new ArrayList<String>();
    private ArrayList<String> mSelectedPath = new ArrayList<String>();

    private ImagePagerAdapter mAdapter;

    private int MAX_SIZE = 6;

    private static final int SHOW_DIALOG = -1;
    private static final int HIDE_DIALOG = 0;
    private int type = 0;

    /**
     * 添加聚光灯效果
     */
    private ImageView ivSpotLight;
    private boolean YNAddSpotLight;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SHOW_DIALOG:
                    dlgLoad.loading();
                    break;
                case HIDE_DIALOG:
                    dlgLoad.dismissDialog();
                    break;
                default:
                    break;
            }

        }

        ;
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail_selected_pager);
        init();
        iniView();
    }

    private void iniView() {
        setTopTitle("图片详情");
        indicator = (TextView) findViewById(R.id.indicator);
        txtOk = (TextView) findViewById(R.id.tv_ok);
        ivSpotLight = (ImageView) findViewById(R.id.iv_spot_light);
        ivSpotLight.setVisibility(View.VISIBLE);
        ivSpotLight.setOnClickListener(this);
        txtOk.setOnClickListener(this);
        okCheck = (CheckBox) findViewById(R.id.checkBox1);
        ivSpotLight.bringToFront();
        mPager = (SquareImageLookViewPager) findViewById(R.id.pager);
        mAdapter = new ImagePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);

        CharSequence text = getString(R.string.viewpager_indicator,
                pagerPosition + 1, mPager.getAdapter().getCount());
        indicator.setText(text);
        mPager.setCurrentItem(pagerPosition);
        //mPager.setOffscreenPageLimit(5);
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
                CharSequence text = getString(R.string.viewpager_indicator,
                        arg0 + 1, mPager.getAdapter().getCount());
                indicator.setText(text);
                pagerPosition = arg0;
                if (!mSelectedPath.isEmpty()
                        && mSelectedPath.contains(mFiles.get(arg0))) {
                    okCheck.setChecked(true);
                    okCheck.setText("删除");
                } else {
                    okCheck.setChecked(false);
                    okCheck.setText("选择");
                }
                if (Bimp.map.get(mFiles == null || mFiles.size() == 0 ? mSelectedPath
                        .get(pagerPosition) : mFiles.get(pagerPosition)) != null
                        && Bimp.map.get(mFiles == null || mFiles.size() == 0 ? mSelectedPath
                        .get(pagerPosition) : mFiles.get(pagerPosition))) {
                    ivSpotLight.setImageResource(R.drawable.spotlight_remove);
                } else {
                    ivSpotLight.setImageResource(R.drawable.spotlight_add);
                }
            }

        });
        if (mSelectedPath.isEmpty()) {
            okCheck.setChecked(false);
            okCheck.setText("选择");
        } else {
            if (mSelectedPath.contains(mFiles.get(pagerPosition))) {
                okCheck.setChecked(true);
                okCheck.setText("删除");
            } else {
                okCheck.setChecked(false);
                okCheck.setText("选择");
            }
        }
        okCheck.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).getText().equals("删除")) {
                    txtOk.setEnabled(true);
                    mSelectedPath.remove(mFiles.get(pagerPosition));
                    okCheck.setText("选择");
                } else {
                    if (mSelectedPath.size() >= MAX_SIZE) {
                        ToastUtil.show("您至多选择" + MAX_SIZE + "张图片");
                    } else {
                        txtOk.setEnabled(true);
                        mSelectedPath.add(mFiles.get(pagerPosition));
                        okCheck.setText("删除");
                    }
                }
                txtOk.setText("完成（" + mSelectedPath.size() + "/" + MAX_SIZE
                        + ")");
            }
        });
        txtOk.setText("完成（" + mSelectedPath.size() + "/" + MAX_SIZE + ")");
        /**
         * 判断选择的图片是否已经添加聚光灯效果，更改icon状态
         */
        if (Bimp.map.get(mFiles == null || mFiles.size() == 0 ? mSelectedPath
                .get(pagerPosition) : mFiles.get(pagerPosition)) != null
                && Bimp.map
                .get(mFiles == null || mFiles.size() == 0 ? mSelectedPath
                        .get(pagerPosition) : mFiles.get(pagerPosition))) {
            ivSpotLight.setImageResource(R.drawable.spotlight_remove);
        }
    }

    private void init() {
        type = getIntent().getIntExtra("type", 0);
        MAX_SIZE = getIntent().getIntExtra("MAX_SIZE", MAX_SIZE);
        pagerPosition = getIntent().getIntExtra("position", 0);
        if (getIntent().getStringArrayListExtra("selected_data") != null) {
            mSelectedPath.addAll(getIntent().getStringArrayListExtra(
                    "selected_data"));
        }
        if (getIntent().getStringArrayListExtra("data") == null) {
            mFiles.addAll(ImageSelectionActivity.data1);
        } else {
            mFiles.addAll(getIntent().getStringArrayListExtra("data"));
        }
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        public ImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFiles == null || mFiles.size() == 0 ? mSelectedPath.size()
                    : mFiles.size();
        }

        @Override
        public Fragment getItem(int position) {
            String url = mFiles == null || mFiles.size() == 0 ? mSelectedPath
                    .get(position) : mFiles.get(position);
            return ImageDetailFragment.newInstance(url);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ok:
                handler.sendEmptyMessage(SHOW_DIALOG);
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                handler.sendEmptyMessage(HIDE_DIALOG);
                                Intent intent = new Intent();
                                intent.putStringArrayListExtra("data1",
                                        mSelectedPath);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        });
                    }
                }).start();
                break;
            case R.id.iv_spot_light:
                txtOk.setEnabled(true);
                ImageDetailFragment f = (ImageDetailFragment) mAdapter
                        .instantiateItem(mPager, pagerPosition);
                if (YNAddSpotLight) { // 不添加聚光灯效果
                    YNAddSpotLight = false;
                    Bimp.map.put(
                            mFiles == null || mFiles.size() == 0 ? mSelectedPath
                                    .get(pagerPosition) : mFiles.get(pagerPosition),
                            YNAddSpotLight);
                    ivSpotLight.setImageResource(R.drawable.spotlight_add);
                    f.mImageView.setImageBitmap(f.bitmap);
                } else { // 添加聚光灯效果
                    YNAddSpotLight = true;
                    Bimp.map.put(
                            mFiles == null || mFiles.size() == 0 ? mSelectedPath
                                    .get(pagerPosition) : mFiles.get(pagerPosition),
                            YNAddSpotLight);
                    ivSpotLight.setImageResource(R.drawable.spotlight_remove);
                    f.mImageView.setImageBitmap(Bimp.addFilterBitmap(f.bitmap,
                            ImageDetailActivity.this));
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mFiles != null) {
            mFiles.clear();
        }
        if (mSelectedPath != null) {
            mSelectedPath.clear();
        }
    }
}