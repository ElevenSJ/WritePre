package com.easier.writepre.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.adapter.MiaoHongWordAdapter;
import com.easier.writepre.adapter.TitlePopAdapter;
import com.easier.writepre.entity.CourseContentInfo;
import com.easier.writepre.entity.YouMengType;
import com.easier.writepre.http.Constant;
import com.easier.writepre.utils.BitmapHelp;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.NetWorkUtils;
import com.easier.writepre.utils.PopUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.FloatViewGroup;
import com.easier.writepre.widget.PaintView;
import com.easier.writepre.widget.TitlePopupWindow;
import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.analytics.MobclickAgent;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Recycler;
import android.support.v7.widget.RecyclerView.State;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 描红
 *
 * @author chenhong
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MiaoHongActivity extends NoSwipeBackActivity {

    public static final String WORD_DATA = "wordList";

    private List<CourseContentInfo> wordList;
    private String fz_id;
    private String videoUrl;
    private SimpleDraweeView iv_large_pic;
    private ImageView iv_bottom_left, iv_bottom_left_back,
            iv_bottom_right, iv_ge;

    private PaintView pv_write;

    private RadioGroup rg_parent;// 笔类型

    private TextView tv_comp, tv_ge, tv_bi, tv_video;// 操作

    private RecyclerView lv_fz;

    private int currIndex;

    private TitlePopupWindow popWindow;

    private TitlePopAdapter popAdapter;

    private int bgArrIndex = 5;

    String[] strArr = {"无", "回宫格", "回宫米字格", "九宫格", "米圆格", "米字格", "田子格"};

    String[] strArr1 = {"逆入", "切入", "顺入", "中锋", "侧锋", "藏锋收笔", "露锋收笔",
            "提笔、按笔、顿笔、驻笔", "折笔", "转笔", "牵丝"};

//    private PopupWindow popupWindow;
//    private PopUtils popUtils = new PopUtils();
    private static final int PHOTO_REQUEST_CODE = 0x000200;

    private MiaoHongWordAdapter wordAdapter;

    public static String photoFile; // 拍照后图片sd路径

    private String photoName = "contrast_photo.png"; // 拍照后图片名

    private int compPicWidth, compPicHight, compPicX, compPicY;

    private String courseName = "描红";

    private FloatViewGroup viewArea;//对比view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miaohong);
        courseName = getIntent().getStringExtra("courseName");

        //友盟统计
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.index));
        var.add("范字学习");
        YouMengType.onEvent(this, var, 1, TextUtils.isEmpty(courseName) ? "范字学习" : courseName);

        fz_id = getIntent().getStringExtra("fz_id");
        wordList = (List<CourseContentInfo>) getIntent().getSerializableExtra(
                WORD_DATA);
        init();
        initWord();

        if (savedInstanceState != null) {
            if (!TextUtils.isEmpty(savedInstanceState.getString("photoName"))) {
                photoName = savedInstanceState.getString("photoName");
                showPopUp(FileUtils.SD_IMAGES_PATH + photoName);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!TextUtils.isEmpty(photoName)) {
            outState.putString("photoName", photoName);
        }
        super.onSaveInstanceState(outState);
    }

    private void initWord() {

        MyLayoutManager layoutManager = new MyLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        lv_fz.setLayoutManager(layoutManager);
        wordAdapter = new MiaoHongWordAdapter(this, wordList);
        wordAdapter
                .setOnItemClickListener(new MiaoHongWordAdapter.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        currIndex = position;
                        lv_fz.smoothScrollToPosition(position);
                        iv_large_pic.setImageURI(StringUtil.getImgeUrl(wordList.get(position)
                                .getPic_url()));
                        videoUrl = wordList.get(position).getVedio_url();
                        if (TextUtils.isEmpty(videoUrl)) {
                            tv_video.setVisibility(View.GONE);
                        } else {
                            tv_video.setVisibility(View.VISIBLE);
                        }

                        iv_bottom_right.setVisibility(View.GONE);
                        if (pv_write.getVisibility() == View.VISIBLE) {
                            pv_write.clear();
                            pv_write.setVisibility(View.GONE);
                        }
                        iv_bottom_left_back.setVisibility(View.GONE);
                        iv_bottom_left.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        // TODO Auto-generated method stub
                    }
                });
        lv_fz.setAdapter(wordAdapter);

        if (wordList != null && wordList.size() > 0) {
            for (int i = 0, j = wordList.size(); i < j; i++) {
                if (fz_id.equals(wordList.get(i).get_id())) {
                    currIndex = i;
                    iv_large_pic.setImageURI(StringUtil.getImgeUrl(wordList.get(i)
                            .getPic_url()));
                    videoUrl = wordList.get(currIndex).getVedio_url();
                    if (TextUtils.isEmpty(videoUrl)) {
                        tv_video.setVisibility(View.GONE);
                    } else {
                        tv_video.setVisibility(View.VISIBLE);
                    }
                    break;
                }
            }

        }
        lv_fz.smoothScrollToPosition(currIndex);
        wordAdapter.setSelectedPosition(currIndex);
    }

    public class MyLayoutManager extends LinearLayoutManager {
        public MyLayoutManager(Context context, int orientation, boolean b) {
            super(context, orientation, b);
        }

        @Override
        public void onMeasure(Recycler recycler, State state, int widthSpec,
                              int heightSpec) {
            if (getItemCount() <= 0) {
                return;
            }
            View view = recycler.getViewForPosition(0);
            if(view != null){
                measureChild(view, widthSpec, heightSpec);
                int measuredWidth = MeasureSpec.getSize(widthSpec);
                int measuredHeight = view.getMeasuredHeight();
                setMeasuredDimension(measuredWidth, measuredHeight);
            }else{
                super.onMeasure(recycler,state,widthSpec,heightSpec);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void init() {
        setTopTitle("描红");

        popWindow = new TitlePopupWindow(this, R.layout.miaohong_popupwindows) {
            @Override
            public void dismiss() {
                findViewById(R.id.img_ge_line).setVisibility(View.INVISIBLE);
                findViewById(R.id.img_bi_line).setVisibility(View.INVISIBLE);
                super.dismiss();
            }
        };
        popWindow.setAnimationStyle(R.style.miao_hong_popwin_anim_style);
        popAdapter = new TitlePopAdapter(this);
        popWindow.setAdapater(popAdapter);
        popWindow.setOnItemClick(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (findViewById(R.id.img_ge_line).getVisibility() == View.VISIBLE) {
                    int res = 0;
                    bgArrIndex = position;
                    switch (position) {
                        case 0:
                            res = 0;
                            break;
                        case 1:
                            res = R.drawable.ge_1;
                            break;
                        case 2:
                            res = R.drawable.ge_2;
                            break;
                        case 3:
                            res = R.drawable.ge_3;
                            break;
                        case 4:
                            res = R.drawable.ge_4;
                            break;
                        case 5:
                            res = R.drawable.ge_5;
                            break;
                        case 6:
                            res = R.drawable.ge_6;
                            break;
                    }
                    iv_ge.setImageResource(res);
                } else {
                    Intent intent = new Intent(MiaoHongActivity.this,
                            MediaActivity.class);
                    ArrayList<String> var = new ArrayList<String>();
                    var.add(YouMengType.getName(MainActivity.index));
                    var.add("范字学习");
                    var.add("视频示范笔法");
                    intent.putExtra("lable", courseName);
                    intent.putStringArrayListExtra(MediaActivity.U_MENG_DATA, var);

                    switch (position) {
                        case 0:
                            intent.putExtra(MediaActivity.URL, Constant.PREFIX_URL
                                    + "dvr/1.mp4");
                            break;
                        case 1:
                            intent.putExtra(MediaActivity.URL, Constant.PREFIX_URL
                                    + "dvr/2.mp4");
                            break;
                        case 2:
                            intent.putExtra(MediaActivity.URL, Constant.PREFIX_URL
                                    + "dvr/3.mp4");
                            break;
                        case 3:
                            intent.putExtra(MediaActivity.URL, Constant.PREFIX_URL
                                    + "dvr/4.mp4");
                            break;
                        case 4:
                            intent.putExtra(MediaActivity.URL, Constant.PREFIX_URL
                                    + "dvr/5.mp4");
                            break;
                        case 5:
                            intent.putExtra(MediaActivity.URL, Constant.PREFIX_URL
                                    + "dvr/6.mp4");
                            break;
                        case 6:
                            intent.putExtra(MediaActivity.URL, Constant.PREFIX_URL
                                    + "dvr/7.mp4");
                            break;
                        case 7:
                            intent.putExtra(MediaActivity.URL, Constant.PREFIX_URL
                                    + "dvr/8.mp4");
                            break;
                        case 8:
                            intent.putExtra(MediaActivity.URL, Constant.PREFIX_URL
                                    + "dvr/9.mp4");
                            break;
                        case 9:
                            intent.putExtra(MediaActivity.URL, Constant.PREFIX_URL
                                    + "dvr/10.mp4");
                            break;
                        case 10:
                            intent.putExtra(MediaActivity.URL, Constant.PREFIX_URL
                                    + "dvr/11.mp4");
                            break;
                        default:
                            break;
                    }
                    startActivity(intent);
                }
                popWindow.dismiss();
            }
        });

        findViewById(R.id.iv_left).setOnClickListener(this);
        findViewById(R.id.iv_right).setOnClickListener(this);

        viewArea = (FloatViewGroup) findViewById(R.id.viewArea);
        iv_large_pic = (SimpleDraweeView) findViewById(R.id.iv_large_pic);
        iv_bottom_left = (ImageView) findViewById(R.id.iv_bottom_left);
        iv_bottom_left_back = (ImageView) findViewById(R.id.iv_bottom_left_back);
        iv_bottom_right = (ImageView) findViewById(R.id.iv_bottom_right);
        iv_ge = (ImageView) findViewById(R.id.iv_ge);
        pv_write = (PaintView) findViewById(R.id.pv_write);

        // 工具
        tv_comp = (TextView) findViewById(R.id.tv_comp);
        tv_ge = (TextView) findViewById(R.id.tv_ge);
        tv_bi = (TextView) findViewById(R.id.tv_bi);
        tv_video = (TextView) findViewById(R.id.tv_video);

        lv_fz = (RecyclerView) findViewById(R.id.word_view);

        iv_bottom_left.setOnClickListener(this);
        iv_bottom_left_back.setOnClickListener(this);
        iv_bottom_right.setOnClickListener(this);

        tv_comp.setOnClickListener(this);
        tv_ge.setOnClickListener(this);
        tv_bi.setOnClickListener(this);
        tv_video.setOnClickListener(this);

        // 笔型选择
        rg_parent = (RadioGroup) findViewById(R.id.rg_parent);
        rg_parent.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int checkId) {

                switch (checkId) {
                    case R.id.rb_red:
                        pv_write.setPaintColor(R.drawable.fountainpenred);
                        break;
                    case R.id.rb_black:
                        pv_write.setPaintColor(R.drawable.fountainpen);
                        break;
                    default:
                        break;
                }

            }
        });
    }

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

    /**
     * 最好定义成共用的函数(碑帖)
     *
     * @param photoFile
     */
    @SuppressWarnings("deprecation")
    public void showPopUp(String photoFile) {
//        popupWindow = popUtils.showPop(this, photoFile, false,
//                compPicWidth, compPicHight);
//        popupWindow.showAtLocation(iv_ge, Gravity.NO_GRAVITY, compPicX,
//                compPicY);
        if (TextUtils.isEmpty(photoFile)){
            ToastUtil.show("拍照失败，请重试");
            return;
        }
        viewArea.setVisibility(View.VISIBLE);
        viewArea.setImageRes(photoFile);
        viewArea.addSeekBar();
        viewArea.addTopBar();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (isDestroyed) {
            return;
        }
        switch (requestCode) {
            case PHOTO_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    photoFile = FileUtils.SD_IMAGES_PATH + photoName;
                    showPopUp(photoFile);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
            case R.id.tv_comp:
                int[] position = new int[2];
                iv_ge.getLocationInWindow(position);
                compPicHight = iv_ge.getHeight();
                compPicWidth = iv_ge.getWidth();
                compPicX = position[0];
                compPicY = position[1];
                if (FileUtils.isExitsSdcard()) {
//                    if (popupWindow != null) {
//                        popupWindow.dismiss();
//                    }
                    photoSys();
                } else
                    ToastUtil.show("没有储存卡");
                break;

            case R.id.tv_bi:
                findViewById(R.id.img_ge_line).setVisibility(View.INVISIBLE);
                findViewById(R.id.img_bi_line).setVisibility(View.VISIBLE);
                popAdapter.setData(strArr1);
                popAdapter.setChecked(-1);
                popWindow.showAsDropDown(findViewById(R.id.img_bi_line), 0, 0);
                break;
            case R.id.tv_ge:
                findViewById(R.id.img_ge_line).setVisibility(View.VISIBLE);
                findViewById(R.id.img_bi_line).setVisibility(View.INVISIBLE);
                popAdapter.setData(strArr);
                popAdapter.setChecked(bgArrIndex);
                popWindow.showAsDropDown(findViewById(R.id.img_ge_line), 0, 0);
                break;
            case R.id.tv_video:
                if (!TextUtils.isEmpty(videoUrl)) {
                    //友盟统计
                    ArrayList<String> var = new ArrayList<String>();
                    var.add(YouMengType.getName(MainActivity.index));
                    var.add("范字学习");
                    var.add("视频示范");
                    Intent intent = new Intent(this, MediaActivity.class);
                    intent.putExtra("lable", courseName);
                    intent.putStringArrayListExtra(MediaActivity.U_MENG_DATA, var);
                    intent.putExtra(MediaActivity.URL,
                            StringUtil.getImgeUrl(videoUrl));
                    startActivity(intent);
                } else {
                    ToastUtil.show("无视频");
                }
                break;
            case R.id.iv_bottom_left:
                iv_bottom_left_back.setVisibility(View.VISIBLE);
                iv_bottom_left.setVisibility(View.GONE);

                iv_bottom_right.setVisibility(View.VISIBLE);
                pv_write.setVisibility(View.VISIBLE);
                if (rg_parent.getCheckedRadioButtonId() == R.id.rb_red) {
                    pv_write.setPaintColor(R.drawable.fountainpenred);
                } else if (rg_parent.getCheckedRadioButtonId() == R.id.rb_black) {
                    pv_write.setPaintColor(R.drawable.fountainpen);
                }

                rg_parent.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_bottom_left_back:
                iv_bottom_left_back.setVisibility(View.GONE);
                iv_bottom_left.setVisibility(View.VISIBLE);

                iv_bottom_right.setVisibility(View.GONE);
                pv_write.setVisibility(View.GONE);
                rg_parent.setVisibility(View.INVISIBLE);
                break;
            case R.id.iv_bottom_right:
                pv_write.clear();
                break;
            case R.id.iv_left:
                if (wordList != null && wordList.size() > 0) {
                    if (currIndex > 0) {
                        if (!NetWorkUtils.isNetworkConnected()) {
                            ToastUtil.show("网络异常，请设置网络");
                            break;
                        }
                        currIndex--;
                        lv_fz.smoothScrollToPosition(currIndex);
                        wordAdapter.setSelectedPosition(currIndex);
                        iv_large_pic.setImageURI(StringUtil.getImgeUrl(wordList.get(currIndex)
                                .getPic_url()));
                        videoUrl = wordList.get(currIndex).getVedio_url();
                        if (TextUtils.isEmpty(videoUrl)) {
                            tv_video.setVisibility(View.GONE);
                        } else {
                            tv_video.setVisibility(View.VISIBLE);
                        }

                        iv_bottom_right.setVisibility(View.GONE);
                        if (pv_write.getVisibility() == View.VISIBLE) {
                            pv_write.clear();
                            pv_write.setVisibility(View.GONE);
                        }
                        iv_bottom_left_back.setVisibility(View.GONE);
                        iv_bottom_left.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.iv_right:
                if (wordList != null && wordList.size() > 0) {
                    if (currIndex < wordList.size() - 1) {
                        if (!NetWorkUtils.isNetworkConnected()) {
                            ToastUtil.show("网络异常，请设置网络");
                            break;
                        }
                        currIndex++;
                        lv_fz.smoothScrollToPosition(currIndex);
                        wordAdapter.setSelectedPosition(currIndex);
                        iv_large_pic.setImageURI(StringUtil.getImgeUrl(wordList.get(currIndex)
                                .getPic_url()));
                        videoUrl = wordList.get(currIndex).getVedio_url();
                        if (TextUtils.isEmpty(videoUrl)) {
                            tv_video.setVisibility(View.GONE);
                        } else {
                            tv_video.setVisibility(View.VISIBLE);
                        }

                        iv_bottom_right.setVisibility(View.GONE);
                        if (pv_write.getVisibility() == View.VISIBLE) {
                            pv_write.clear();
                            pv_write.setVisibility(View.GONE);
                        }
                        iv_bottom_left_back.setVisibility(View.GONE);
                        iv_bottom_left.setVisibility(View.VISIBLE);
                    }
                }
                break;

        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (popupWindow != null) {
//            popupWindow.dismiss();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //友盟统计
        List<String> var = new ArrayList<String>();
        var.add(YouMengType.getName(MainActivity.index));
        var.add("范字学习");
        var.add("描红");
        YouMengType.onEvent(this, var, getShowTime(), TextUtils.isEmpty(courseName) ? "描红" : courseName);

    }
}
