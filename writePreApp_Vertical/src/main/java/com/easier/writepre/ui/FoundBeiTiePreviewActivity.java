package com.easier.writepre.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.db.DatabaseCityHelper;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.fragment.FoundBeiTieImageDetailFragment;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.mainview.FoundMainView;
import com.easier.writepre.param.ResBeiTiePicsParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.ResBeiTieListSearchResponse.ResBeiTieListSearchInfo;
import com.easier.writepre.response.ResBeiTiePicsResponse;
import com.easier.writepre.response.ResBeiTiePicsResponse.ResBeiTiePicsBody;
import com.easier.writepre.response.ResBeiTiePicsResponse.ResBeiTiePicsInfo;
import com.easier.writepre.utils.AnimationsUtil;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.PopUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.FloatViewGroup;
import com.easier.writepre.widget.SquareImageLookViewPager;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 碑帖预览
 */
public class FoundBeiTiePreviewActivity extends BaseActivity {

    private RelativeLayout rlTip;   // 滑动方向提示

    private int index;

    private String FaceUrl;

    public boolean FaceFlag; // 下载过封面不进行下载

    private int pagerPosition;

    public TextView indicator;

    public RelativeLayout top;

    public TextView tv_del;

    public static String photoFile; // 拍照后图片sd路径

//    private PopupWindow popupWindow;

//    private PopUtils popUtils = new PopUtils();

    private FloatViewGroup viewArea;//对比view

    private DatabaseCityHelper helper;

    private SquareImageLookViewPager mPager;

    private ResBeiTieListSearchInfo rbtlsInfo;

    private FoundBeiTieImageDetailFragment fragment;

    private String photoName = "contrast_photo.png"; // 拍照后图片名

    private static final int PHOTO_REQUEST_CODE = 0x000200;

//    private ImageView iv_show, iv_cancel;  //iv_down

    public TextView tv_down, tv_profiles;

    public boolean fromType; //  判断是下载还是删除(离线碑帖)     true表示删除

    private static final String STATE_POSITION = "STATE_POSITION";

    private List<ResBeiTiePicsInfo> listUrl = new ArrayList<ResBeiTiePicsInfo>(); // 碑帖图片集

    //将数据倒序
    private LinkedList<ResBeiTiePicsInfo> orderListUrl = new LinkedList<ResBeiTiePicsInfo>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.found_beitie_preview_pager);
        init();
        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
            if (!TextUtils.isEmpty(savedInstanceState.getString("photoName"))) {
                photoName = savedInstanceState.getString("photoName");
                showPop(FileUtils.SD_IMAGES_PATH + photoName);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, mPager.getCurrentItem());
        if (!TextUtils.isEmpty(photoName)) {
            outState.putString("photoName", photoName);
        }
        super.onSaveInstanceState(outState);
    }

    public void init() {
        helper = new DatabaseCityHelper(this);
        index = getIntent().getIntExtra("index", 0);
        rbtlsInfo = (ResBeiTieListSearchInfo) getIntent().getSerializableExtra(
                "beitie_entity");
        setTopTitle(rbtlsInfo.getTitle()); // 碑帖名
        FaceUrl = rbtlsInfo.getFace_url();
        rlTip = (RelativeLayout) findViewById(R.id.rl_tip);
        mPager = (SquareImageLookViewPager) findViewById(R.id.pager);
        tv_profiles = (TextView) findViewById(R.id.tv_profiles);
//        iv_cancel = (ImageView) findViewById(R.id.iv_cancel);
//        iv_show = (ImageView) findViewById(R.id.iv_show);
        // iv_down = (ImageView) findViewById(R.id.iv_down);
        indicator = (TextView) findViewById(R.id.indicator);
        top = (RelativeLayout) findViewById(R.id.top);
        tv_down = (TextView) findViewById(R.id.tv_down);
        tv_del = (TextView) findViewById(R.id.tv_del);
        findViewById(R.id.tv_camera).setOnClickListener(this);
//        iv_cancel.bringToFront();
//        iv_show.bringToFront();
        if ((int) SPUtils.instance().get(SPUtils.SHOW_HIDE, 0) < 3) {
            // 显示的时候 禁止横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏设置
            rlTip.setVisibility(View.VISIBLE);
        }
        if (rbtlsInfo.getPenmen_name().equals("离线碑帖")) {
            fromType = true;
            tv_down.setVisibility(View.GONE);
            //iv_down.setVisibility(View.GONE);
            // findViewById(R.id.iv_del).setVisibility(View.VISIBLE);
            tv_del.setVisibility(View.VISIBLE);
            querySubPic(rbtlsInfo.get_id());
        } else {
            if (!TextUtils.isEmpty(rbtlsInfo.getLink_url()))
                tv_profiles.setVisibility(View.VISIBLE);
            RequestManager.request(this,
                    new ResBeiTiePicsParams(rbtlsInfo.get_id()),
                    ResBeiTiePicsResponse.class, this, Constant.URL);
        }

        viewArea = (FloatViewGroup) findViewById(R.id.viewArea);
    }

    private void querySubPic(String id) { // 离线碑帖
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select sub_url from pic where id = '" + id
                + "'", null);
        // Cursor cursor = DBHelper.instance().query(
        // "select sub_url from pic where id = '" + id + "'");
        while (cursor.moveToNext()) {
            ResBeiTiePicsInfo rbtpInfo = new ResBeiTiePicsResponse().new ResBeiTiePicsInfo();
            rbtpInfo.setPic_url(cursor.getString(0));
            orderListUrl.addFirst(rbtpInfo);   //orderListUrl.add(rbtpInfo);
        }
        showImage(orderListUrl, false);
        cursor.close();
        db.close();
        // db.close();
    }


    /**
     * 最好定义成共用的函数(描红比对)
     *
     * @param listUrl
     * @param flag    标识离线碑帖
     */
    @SuppressWarnings("deprecation")
    public void showImage(final List<ResBeiTiePicsInfo> listUrl,
                          final boolean flag) {
        indicator.setVisibility(View.VISIBLE);
        ImagePagerAdapter mAdapter = new ImagePagerAdapter(
                getSupportFragmentManager(), listUrl);
        mPager.setAdapter(mAdapter);
        CharSequence text = getString(R.string.viewpager_indicator, 1, mPager
                .getAdapter().getCount());
        indicator.setText(text);
        IsAlydownPic(0, flag); // 判断第一个是否已经下载过
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
                        mPager.getAdapter().getCount() - arg0, mPager.getAdapter().getCount());   //arg0+1
                indicator.setText(text);
                pagerPosition = arg0;
                IsAlydownPic(pagerPosition, flag);
            }
        });
        mPager.setCurrentItem(listUrl.size() - 1); // pagerPosition
    }

    /**
     * 是否下载相同碑帖图片查询 (每次需要查库操作、待改造成放在内存对象中)
     */
    @SuppressWarnings("deprecation")
    private void IsAlydownPic(int position, boolean flag) {
        if (!flag)
            return;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from pic where sub_id = '"
                + orderListUrl.get(position).get_id() + "'", null);
        if (cursor.getCount() == 1) {
            //iv_down.setAlpha(100);// 透明100 不可以点击(表示下载过)
            //iv_down.setEnabled(false);
            //tv_down.setTextColor(Color.GRAY);
            //tv_down.getBackground().setAlpha(100);
            // tv_down.setVisibility(View.GONE);
            // tv_down.setEnabled(false);
        } else {
            //iv_down.setAlpha(255); // 不透明设置 可以点击(表示未下载过)
            //iv_down.setEnabled(true);
            //tv_down.setTextColor(Color.parseColor("#333333"));
            // tv_down.getBackground().setAlpha(255);
            if ((tv_down.getVisibility()) == 0) {
                tv_down.setVisibility(View.VISIBLE);
                tv_down.setEnabled(true);
            }
        }
        cursor.close();
        db.close();
    }

    public void insertPic(String sub_url, String face_url) { // sd卡保存图片路径
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from pic where sub_id = '"
                + orderListUrl.get(pagerPosition).get_id() + "'", null);
        // Cursor cursor = DBHelper.instance().query(
        // "select * from pic where sub_id = '"
        // + listUrl.get(pagerPosition).get_id() + "'");
        if (cursor.getCount() == 0) {
            ToastUtil.show("下载成功");
            db.execSQL("insert into pic(id, title, face_url, sub_id, sub_url, sub_index) values('"
                    + rbtlsInfo.get_id()
                    + "','"
                    + rbtlsInfo.getTitle()
                    + "','"
                    + face_url // rbtlsInfo.getFace_url()
                    + "','"
                    + orderListUrl.get(pagerPosition).get_id()
                    + "','"
                    + sub_url // listUrl.get(pagerPosition).getPic_url()
                    + "'," + orderListUrl.get(pagerPosition).getSort() + ")");
            // DBHelper.instance().insert(
            // "insert into pic(id, title, face_url, sub_id, sub_url, sub_index) values('"
            // + rbtlsInfo.get_id() + "','" + rbtlsInfo.getTitle()
            // + "','" + rbtlsInfo.getFace_url() + "','"
            // + listUrl.get(pagerPosition).get_id() + "','"
            // + listUrl.get(pagerPosition).getPic_url() + "',"
            // + listUrl.get(pagerPosition).getSort() + ")");
        } else {
            ToastUtil.show("已下载");
        }
        db.close();
        // db.close();
    }

    public void delPic(String sub_url) {
        SQLiteDatabase db = helper.getReadableDatabase();
        db.delete("pic", "sub_url = ?", new String[]{sub_url});
        // DBHelper.instance().delete("pic", "sub_url = ?",
        // new String[] { sub_url });
        db.close();
        // db.close();
    }

    /**
     * 打开系统相机拍照
     */
    public void photoSys() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(FileUtils.SD_IMAGES_PATH, photoName)));
            this.startActivityForResult(intent, PHOTO_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            ToastUtil.show("没有找到储存目录");
        }
    }


    public void showPop(String photoFile) {
//        popupWindow = popUtils.showPop(this, photoFile, true, 0, 0);
//        popupWindow.showAsDropDown(findViewById(R.id.view_line));
        showOrHideBar();
        if (TextUtils.isEmpty(photoFile)) {
            ToastUtil.show("拍照失败，请重试");
            return;
        }
        viewArea.setVisibility(View.VISIBLE);
        viewArea.setImageRes(photoFile);
        viewArea.addSeekBar();
        viewArea.addTopBar();
    }

    /**
     * 点击下载入库并更改图片状态
     */
    @SuppressWarnings("deprecation")
    private void ivState(String sub_url, String face_url) {
        insertPic(sub_url, face_url);
        //iv_down.setAlpha(100);// 透明100 不可以点击(表示下载过)
        //iv_down.setEnabled(false);
        // tv_down.setTextColor(Color.GRAY);
        //tv_down.getBackground().setAlpha(100);
        //tv_down.setVisibility(View.GONE);
        //tv_down.setEnabled(false);
    }

    /**
     * 下载封面
     *
     * @param sub_url
     */
    private void downloadFace(final String sub_url) {
        new HttpUtils().download(
                StringUtil.getImgeUrl(FaceUrl) + Constant.BIG_IMAGE_SUFFIX,
                FileUtils.SD_IMAGES_PATH
                        + FaceUrl.substring(FaceUrl.lastIndexOf("/") + 1),
                true, true, new RequestCallBack<File>() {

                    @Override
                    public void onSuccess(ResponseInfo<File> arg0) {
                        // ToastUtil.show("下载成功");
                        ivState(sub_url,
                                FileUtils.SD_IMAGES_PATH
                                        + FaceUrl.substring(FaceUrl
                                        .lastIndexOf("/") + 1));
                        FaceFlag = true;
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        String tip = arg1.contains("completely") ? "已下载"
                                : "保存失败";
                        if (tip.equals("已下载")) {
                            ivState(sub_url,
                                    FileUtils.SD_IMAGES_PATH
                                            + FaceUrl.substring(FaceUrl
                                            .lastIndexOf("/") + 1));
                        } else
                            ToastUtil.show(tip);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_tip:      // 保存在sp中，便于隐藏显示
                // 设置可以 横竖屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);//默认设置
                SPUtils.instance().put(SPUtils.SHOW_HIDE, ((int) SPUtils.instance().get(SPUtils.SHOW_HIDE, 0)) + 1);    // 用int类型便于后面扩展(例如：3次后进行隐藏)
                rlTip.setVisibility(View.INVISIBLE);
                break;
            case R.id.tv_down:// 碑帖下载  iv_down
                final String imageOssPath = orderListUrl != null && orderListUrl.size() > 0 ? orderListUrl
                        .get(pagerPosition).getPic_url() : "";
                if (TextUtils.isEmpty(imageOssPath)) {
                    return;
                }
                umengDownload();
                new HttpUtils().download(
                        StringUtil.getImgeUrl(imageOssPath),
                        FileUtils.SD_IMAGES_PATH
                                + imageOssPath.substring(imageOssPath
                                .lastIndexOf("/") + 1), true, true,
                        new RequestCallBack<File>() {

                            @Override
                            public void onSuccess(ResponseInfo<File> arg0) {
                                if (FaceFlag) { // 封面下载过 直接入库
                                    // ToastUtil.show("下载成功");
                                    ivState(FileUtils.SD_IMAGES_PATH
                                                    + imageOssPath.substring(imageOssPath
                                                    .lastIndexOf("/") + 1),
                                            FileUtils.SD_IMAGES_PATH
                                                    + FaceUrl.substring(FaceUrl
                                                    .lastIndexOf("/") + 1));
                                } else
                                    downloadFace(FileUtils.SD_IMAGES_PATH
                                            + imageOssPath.substring(imageOssPath
                                            .lastIndexOf("/") + 1));
                            }

                            @Override
                            public void onFailure(HttpException arg0, String arg1) {
                                String tip = arg1.contains("completely") ? "已下载"
                                        : "保存失败";
                                if (tip.equals("已下载")) {
                                    if (FaceFlag) { // 封面下载过 直接入库
                                        // ToastUtil.show("下载成功");
                                        ivState(FileUtils.SD_IMAGES_PATH
                                                        + imageOssPath.substring(imageOssPath
                                                        .lastIndexOf("/") + 1),
                                                FileUtils.SD_IMAGES_PATH
                                                        + FaceUrl.substring(FaceUrl
                                                        .lastIndexOf("/") + 1));
                                    } else
                                        downloadFace(FileUtils.SD_IMAGES_PATH
                                                + imageOssPath.substring(imageOssPath
                                                .lastIndexOf("/") + 1));
                                } else
                                    ToastUtil.show(tip);
                            }
                        });
                break;
            case R.id.tv_camera:// 拍照对比  iv_camera
                if (FileUtils.isExitsSdcard()) {
//                    if (popupWindow != null) {
//                        popupWindow.dismiss();
//                    }
                    photoSys();
                    umengCompare();
                } else
                    ToastUtil.show("没有储存卡");
                break;
            case R.id.tv_profiles:   // 碑帖简介
                Intent intent = new Intent(this, BannerLinkActivity.class);
                intent.putExtra("name", rbtlsInfo.getTitle());
                intent.putExtra("url", rbtlsInfo.getLink_url());
                startActivity(intent);
                break;
//            case R.id.iv_cancel:
//                if (popupWindow != null) {
//                    popupWindow.dismiss();
//                }
//                break;
//            case R.id.iv_show:
//                if (popUtils != null&&popUtils.getFvg()!=null) {
//                    if (popUtils.getFvg().isShowing()){
//                        popUtils.getFvg().dismiss();
//                    }else{
//                        popUtils.getFvg().show();
//                    }
//                }
//                break;
            case R.id.tv_del: //iv_del
                delPhotoTip();
                break;
            default:
                break;
        }
    }

    /**
     * 友盟统计对比
     */
    private void umengCompare() {
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.TYPE_FOR));
        var.add(FoundMainView.CONTENT[FoundMainView.index]);
        var.add("对比");
        YouMengType.onEvent(this, var, 1, rbtlsInfo.getTitle());
    }

    /**
     * 友盟统计下载
     */
    private void umengDownload() {
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.TYPE_FOR));
        var.add(FoundMainView.CONTENT[FoundMainView.index]);
        var.add("下载");
        YouMengType.onEvent(this, var, 1, rbtlsInfo.getTitle());
    }

    /**
     * 弹出框提醒是否删除图片
     */
    public void delPhotoTip() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_islogin, null);
        ((TextView) view.findViewById(R.id.tv_login_now)).setText("确定是否删除?");
        view.findViewById(R.id.tv_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (orderListUrl.size() > 0) {
                    if (orderListUrl.size() == 1) {
                        delPic(orderListUrl.get(0).getPic_url());
                        orderListUrl.remove(0);
                        setResult(RESULT_OK, new Intent().putExtra("index", index));
                        finish();
                    } else {
                        delPic(orderListUrl.get(pagerPosition).getPic_url());
                        orderListUrl.remove(pagerPosition);
                    }
                    // mPager.setCurrentItem(pagerPosition);
                    showImage(orderListUrl, false);
                }
            }
        });
        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.dismiss();
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @Override
    public void onResponse(BaseResponse response) {
        if ("0".equals(response.getResCode())) {
            if (response instanceof ResBeiTiePicsResponse) {
                ResBeiTiePicsResponse mResBeiTiePicsResponse = (ResBeiTiePicsResponse) response;
                if (mResBeiTiePicsResponse != null) {
                    ResBeiTiePicsBody mBeiTiePicsBody = mResBeiTiePicsResponse
                            .getRepBody();
                    if (mBeiTiePicsBody != null) {
                        listUrl = mBeiTiePicsBody.getList();
                        if (listUrl != null && listUrl.size() > 0) {
                            // indicator.setVisibility(View.VISIBLE);
                            for (int i = 0; i < listUrl.size(); i++) {
                                orderListUrl.addFirst(listUrl.get(i));
                            }
                            showImage(orderListUrl, true);
                        } else {
                            // ToastUtil.show("未查到数据");
                        }
                    }
                }
            }
        } else
            ToastUtil.show(response.getResMsg());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (isDestroyed) {
            return;
        }
        switch (requestCode) {
            case PHOTO_REQUEST_CODE:
                if (resultCode == RESULT_OK) { // /storage/emulated/0/writepre/images/null
                    photoFile = FileUtils.SD_IMAGES_PATH + photoName;
                    showPop(photoFile);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (popupWindow != null) {
//            popupWindow.dismiss();
//        }
    }

    public void showOrHideBar() {
        if (indicator.getVisibility() == View.VISIBLE) {
            indicator.setVisibility(View.GONE);
            top.setVisibility(View.INVISIBLE);
            indicator.startAnimation(AnimationsUtil.hideAnimation(1.0f));
            top.startAnimation(AnimationsUtil.hideAnimation(-1.0f));
            if (fromType) {     //表示离线碑帖  即删除
                //ToastUtil.show("GONE DEL");
                tv_del.setVisibility(View.GONE);
                tv_del.startAnimation(AnimationsUtil.hideAnimation(1.0f));
            } else {
                //ToastUtil.show("GONE DOWN");
                tv_down.setVisibility(View.GONE);
                tv_down.startAnimation(AnimationsUtil.hideAnimation(1.0f));
            }
        } else {
            if (viewArea.getVisibility() == View.GONE) {
                indicator.setVisibility(View.VISIBLE);
                top.setVisibility(View.VISIBLE);
                indicator.startAnimation(AnimationsUtil.showAnimation(1.0f));
                top.startAnimation(AnimationsUtil.showAnimation(-1.0f));
                if (fromType) {     //表示离线碑帖  即删除
                    //ToastUtil.show("VISIBLE DEL");
                    tv_del.setVisibility(View.VISIBLE);
                    tv_del.startAnimation(AnimationsUtil.showAnimation(1.0f));
                } else {
                    //ToastUtil.show("VISIBLE DOWN");
                    tv_down.setVisibility(View.VISIBLE);
                    tv_down.startAnimation(AnimationsUtil.showAnimation(1.0f));
                }
            }
        }
    }

    class ImagePagerAdapter extends FragmentStatePagerAdapter {

        public List<ResBeiTiePicsInfo> listUrl;

        public ImagePagerAdapter(FragmentManager fm,
                                 List<ResBeiTiePicsInfo> listUrl) {
            super(fm);
            this.listUrl = listUrl;
        }

        @Override
        public int getCount() {
            return listUrl == null ? 0 : listUrl.size();
        }

        @Override
        public Fragment getItem(int position) {
            String url = "";
            if (rbtlsInfo.getPenmen_name().equals("离线碑帖")) {
                url = listUrl.get(position).getPic_url().contains("writepre") ? listUrl
                        .get(position).getPic_url() : StringUtil
                        .getImgeUrl(listUrl.get(position).getPic_url()); // 为了上个版本保存网络图片路径而特殊处理
            } else {
                url = StringUtil.getImgeUrl(listUrl.get(position).getPic_url());
            }
            fragment = FoundBeiTieImageDetailFragment.newInstance(url);
            return fragment;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        umeng();
    }

    private void umeng() {
        if (rbtlsInfo != null) {
            List<String> var = new ArrayList<String>();
            var.add(YouMengType.getName(MainActivity.TYPE_FOR));
            var.add(FoundMainView.CONTENT[FoundMainView.index]);
            var.add(rbtlsInfo.getPeriod());
            var.add(rbtlsInfo.getPenmen_name());
            var.add(rbtlsInfo.getTitle());
            YouMengType.onEvent(this, var, getShowTime(), rbtlsInfo.getTitle());
        }
    }
}