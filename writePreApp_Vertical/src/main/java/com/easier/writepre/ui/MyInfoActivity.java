package com.easier.writepre.ui;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.easier.writepre.R;
import com.easier.writepre.adapter.MyListAdapter;
import com.easier.writepre.entity.LoginEntity;
import com.easier.writepre.entity.MyListData;
import com.easier.writepre.entity.OssTokenInfo;
import com.easier.writepre.entity.SocialPropEntity;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.EditInfoParam;
import com.easier.writepre.param.HeadUpdataParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.EditMyInfoResponse;
import com.easier.writepre.response.HeadUpdateResponse;
import com.easier.writepre.ui.myinfo.EditBriefActivity;
import com.easier.writepre.ui.myinfo.EditCityActivity;
import com.easier.writepre.ui.myinfo.EditEmailActivity;
import com.easier.writepre.ui.myinfo.EditInfoReuseActivity;
import com.easier.writepre.ui.myinfo.EditNameActivity;
import com.easier.writepre.ui.myinfo.EditRealNameActivity;
import com.easier.writepre.ui.myinfo.EditTelActivity;
import com.easier.writepre.utils.FileUtils;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.StringUtil;
import com.easier.writepre.utils.ToastUtil;
import com.easier.writepre.widget.ChoiceSexDialog;
import com.easier.writepre.widget.RoundImageView;
import com.easier.writepre.widget.SelectPicPopupWindow;
import com.google.gson.Gson;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;

import io.rong.imkit.RongIM;

/**
 * 我的个人信息界面
 *
 * @author zhaomaohan
 */
public class MyInfoActivity extends BaseActivity implements OnItemClickListener {
    private ListView lv_list;
    private List<MyListData> list;
    //	public static String left_text[] = new String[] { "昵称", "地区", "个人签名",
//			"姓名", "生日", "性别", "手机号码", "邮箱", "喜欢的碑帖", "喜欢的书体", "兴趣爱好", "学校",
//			"职业", "擅长" };
    public static String left_text[] = new String[]{"昵称", "姓名", "性别", "生日", "地区", "手机号码",
            "邮箱", "学校", "职业", "喜欢的碑帖", "擅长", "喜欢的书体", "兴趣爱好"};
    // public static String center_text[]=new
    // String[]{"头像","昵称","帐号","地区","个人签名",
    // "姓名","生日","性别","手机号码","邮箱",
    // "喜欢的碑帖","喜欢的书体","兴趣爱好","学校","职业"};
    // public static String right_text[]=new
    // String[]{null,null,null,null,null,null,"已经是最新版本",null};
    public static int right_image[] = new int[]{-1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1};

    public static String right_text[] = new String[]{"1", "2", "3", "4", "5",
            "6", "7", "8", "9", "10", "11", "12", "13"};
    private MyListAdapter adapter;

    final public static int HEAD = 0, NAME = 1, REAL_NAME = 2, SEX = 3, BIRTH = 4, CITY = 5,
            TEL = 6, EMAIL = 7, SCHOOL = 8, PROFESSION = 9, BEITIE = 10, GOODAT = 11,
            SHUTI = 12, HOBBY = 13, GRADE = 14;

    private ChoiceSexDialog sexDlg;

    private RoundImageView iv_head;

    private final static int DATE_DIALOG = 0;//
    private Calendar c = null;

    private String headerPath;

    private SelectPicPopupWindow popWindow;

    private static final int PHOTO_GRAPH = 104;// 拍照
    private static final int PHOTO_ZOOM = 105; // 缩放
    private static final int PHOTO_RESOULT = 106;// 结果
    private static final String IMAGE_UNSPECIFIED = "image/*";
    private String photoName;
    private String photoPath;

    private LoginEntity loginEntity;

    private boolean isUserInfoModify = false;

    public static String OLD_NAME = "OLD_NAME";
    public static String OLD_BRIEF = "OLD_BRIEF";
    public static String OLD_REALNAME = "OLD_REALNAME";
    public static String OLD_EMAIL = "OLD_EMAIL";
    public static String OLD_BEITIE = "OLD_BEITIE";
    public static String OLD_SHUTI = "OLD_SHUTI";
    public static String OLD_HOBBY = "OLD_HOBBY";
    public static String OLD_SCHOOL = "OLD_SCHOOL";
    public static String OLD_PROFESSION = "OLD_PROFESSION";
    public static String OLD_GOODAT = "OLD_GOODAT";
    public static String OLD_GRADE = "OLD_GRADE";
    public static String EDIT_TYPE = "EDIT_TYPE";
    public static String EDITTEXT = "EDITTEXT";

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case COMMIT_FILE_OSS_ALL_FAIL:
                    dlgLoad.dismissDialog();
                    StringBuffer bufString = new StringBuffer();
                    bufString.append(",头像上传失败");
                    if (isUserInfoModify) {
                        bufString.append(",开始更新其他信息");
                        uploadMyInfo();
                    }
                    ToastUtil.show(bufString.toString());
                    break;
                case COMMIT_FILE_OSS_ALL_NOT_SUCCESS:
                case COMMIT_FILE_OSS_ALL_SUCCESS:
                    dlgLoad.dismissDialog();
                    if (((ArrayList<String>) msg.obj).size() != 0) {
                        headerPath = ((ArrayList<String>) msg.obj).get(0);
                    }
                    RequestManager.request(MyInfoActivity.this,
                            new HeadUpdataParams(headerPath),
                            HeadUpdateResponse.class, MyInfoActivity.this,
                            Constant.URL);
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_my_list);
        loginEntity = SPUtils.instance().getLoginEntity();
        initView();
    }

    private void initView() {
        setTopTitle("编辑资料");

        // 初始化
        sexDlg = new ChoiceSexDialog(this);

        if ((boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false)) {
            // right_image[HEAD] =.
            // StringUtil.getHeadUrl(loginEntity.getHead_img());

            right_text[NAME - 1] = loginEntity.getUname();
//			right_text[ID - 1] = loginEntity.get_id();
            right_text[CITY - 1] = loginEntity.getAddr();
//			right_text[BRIEF - 1] = loginEntity.getSignature();
            right_text[REAL_NAME - 1] = loginEntity.getReal_name();
            right_text[BIRTH - 1] = loginEntity.getBirth_day();
            right_text[SEX - 1] = loginEntity.getSex();
            right_text[TEL - 1] = loginEntity.getTel();
            right_text[EMAIL - 1] = loginEntity.getEmail0();
            right_text[BEITIE - 1] = loginEntity.getBei_tie();
            right_text[SHUTI - 1] = loginEntity.getFav_font();
            right_text[HOBBY - 1] = loginEntity.getInterest();
            right_text[SCHOOL - 1] = loginEntity.getSchool();
            right_text[PROFESSION - 1] = loginEntity.getProfession();
            right_text[GOODAT - 1] = loginEntity.getGood_at();
        }
        list = getData();

        // 加载控件
        lv_list = (ListView) findViewById(R.id.lv_list);

        addHeader();
        adapter = new MyListAdapter(this, list);
        lv_list.setAdapter(adapter);

        // 监听点击事件
        lv_list.setOnItemClickListener(this);
        sexDlg.setOnClickListener(this);
        popWindow = new SelectPicPopupWindow(this, itemsOnClick);

    }

    // 为弹出窗口实现监听类
    private OnClickListener itemsOnClick = new OnClickListener() {

        public void onClick(View v) {
            popWindow.dismiss();
            switch (v.getId()) {
                case R.id.item_popupwindows_camera:
                    formCamera();
                    break;
                case R.id.item_popupwindows_Photo:
                    formGallerya();
                    break;
                default:
                    break;
            }

        }

    };
    private String age;

    /**
     * 拍照
     */
    private void formCamera() {
        photoName = System.currentTimeMillis() + ".png";
        new File(FileUtils.SD_IMAGES_PATH).mkdirs();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(FileUtils.SD_IMAGES_PATH, photoName)));
        startActivityForResult(intent, PHOTO_GRAPH);
    }

    /**
     * 照片中选择
     */
    private void formGallerya() {
        photoName = System.currentTimeMillis() + ".png";
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                IMAGE_UNSPECIFIED);
        startActivityForResult(intent, PHOTO_ZOOM);
    }

    /**
     * 收缩图片
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");// 调用Android系统自带的一个图片剪裁页面,
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");// 进行修剪
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_RESOULT);
    }

    private void addHeader() {
        lv_list.addHeaderView(View.inflate(MyInfoActivity.this,
                R.layout.list_header_my_info, null));
        iv_head = (RoundImageView) findViewById(R.id.iv_head);
        iv_head.setImageView(StringUtil.getHeadUrl(loginEntity.getHead_img()));
        iv_head.setIconView(loginEntity.getIs_teacher());
    }

    /**
     * 获取动态数组数据 可以由其他地方传来(json等)
     *
     * @return
     */
    private List<MyListData> getData() {
        List<MyListData> list = new ArrayList<MyListData>();
        for (int i = 0; i < left_text.length; i++) {
            MyListData myListData = new MyListData(-1, left_text[i],
                    right_image[i], right_text[i], null);
            list.add(myListData);
        }
        return list;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        switch (position) {
            case HEAD:
                popWindow.showAtLocation(
                        MyInfoActivity.this.findViewById(R.id.main), Gravity.BOTTOM
                                | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case NAME:
                Intent intent1 = new Intent(MyInfoActivity.this,
                        EditNameActivity.class);
                intent1.putExtra(OLD_NAME, right_text[NAME - 1]);
                startActivityForResult(intent1, NAME);
                break;
//		case 2:
//			// ToastUtil.show("帐号");
//			break;
            case CITY:
                Intent intent3 = new Intent(MyInfoActivity.this,
                        EditCityActivity.class);
                startActivityForResult(intent3, CITY);
                break;
//		case BRIEF:
//			Intent intent4 = new Intent(MyInfoActivity.this,
//					EditBriefActivity.class);
//			intent4.putExtra(OLD_BRIEF, right_text[BRIEF - 1]);
//			startActivityForResult(intent4, BRIEF);
//			break;
            case REAL_NAME:
                Intent intent5 = new Intent(MyInfoActivity.this,
                        EditRealNameActivity.class);
                intent5.putExtra(OLD_REALNAME, right_text[REAL_NAME - 1]);
                startActivityForResult(intent5, REAL_NAME);
                break;
            case BIRTH:
                showDialog(DATE_DIALOG);
                break;
            case SEX:
                sexDlg.show();
                break;
            case TEL:
                Intent intent8 = new Intent(MyInfoActivity.this,
                        EditTelActivity.class);
                startActivityForResult(intent8, TEL);
                break;
            case EMAIL:
                Intent intent9 = new Intent(MyInfoActivity.this,
                        EditEmailActivity.class);
                intent9.putExtra(OLD_EMAIL, right_text[EMAIL - 1]);
                startActivityForResult(intent9, EMAIL);
                break;
            case BEITIE:
                Intent intent10 = new Intent(MyInfoActivity.this,
                        EditInfoReuseActivity.class);
                intent10.putExtra(EDIT_TYPE, BEITIE);
                intent10.putExtra(OLD_BEITIE, right_text[BEITIE - 1]);
                startActivityForResult(intent10, BEITIE);
                break;
            case SHUTI:
                Intent intent11 = new Intent(MyInfoActivity.this,
                        EditInfoReuseActivity.class);
                intent11.putExtra(EDIT_TYPE, SHUTI);
                intent11.putExtra(OLD_SHUTI, right_text[SHUTI - 1]);
                startActivityForResult(intent11, SHUTI);
                break;
            case HOBBY:
                Intent intent12 = new Intent(MyInfoActivity.this,
                        EditInfoReuseActivity.class);
                intent12.putExtra(EDIT_TYPE, HOBBY);
                intent12.putExtra(OLD_HOBBY, right_text[HOBBY - 1]);
                startActivityForResult(intent12, HOBBY);
                break;
            case SCHOOL:
                Intent intent13 = new Intent(MyInfoActivity.this,
                        EditInfoReuseActivity.class);
                intent13.putExtra(EDIT_TYPE, SCHOOL);
                intent13.putExtra(OLD_SCHOOL, right_text[SCHOOL - 1]);
                startActivityForResult(intent13, SCHOOL);
                break;
            case PROFESSION:
                Intent intent14 = new Intent(MyInfoActivity.this,
                        EditInfoReuseActivity.class);
                intent14.putExtra(EDIT_TYPE, PROFESSION);
                intent14.putExtra(OLD_PROFESSION, right_text[PROFESSION - 1]);
                startActivityForResult(intent14, PROFESSION);
                break;
            case GOODAT:
                Intent intent15 = new Intent(MyInfoActivity.this,
                        EditInfoReuseActivity.class);
                intent15.putExtra(EDIT_TYPE, GOODAT);
                intent15.putExtra(OLD_GOODAT, right_text[GOODAT - 1]);
                startActivityForResult(intent15, GOODAT);
                break;
            default:
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            setTopRightTxt("完成");

            if (requestCode < 100) {
                isUserInfoModify = true;
            }
            switch (requestCode) {
                // 拍照
                case PHOTO_GRAPH:
                    // 设置文件保存路径
                    File picture = new File(FileUtils.SD_IMAGES_PATH + photoName);
                    startPhotoZoom(Uri.fromFile(picture));
                    break;
                // 读取相册缩放图片
                case PHOTO_ZOOM:
                    if (data == null)
                        return;
                    startPhotoZoom(data.getData());
                    break;
                case PHOTO_RESOULT:
                    if (data == null)
                        return;
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap photo = extras.getParcelable("data");
                        if (photo == null) {
                            ToastUtil.show("处理图片失败,请重试!");
                            return;
                        }
                        File pohotFile = FileUtils.saveBitmap(
                                FileUtils.SD_CLIP_IMAGES_PATH, photoName, photo);
                        // 选择的图片的路径
                        photoPath = pohotFile.getAbsolutePath();
                        // 初始化参数
                        iv_head.setImageDrawable(Drawable.createFromPath(photoPath));
                    }

                    break;
                case NAME:
                    if (data == null)
                        break;
                    right_text[NAME - 1] = data.getStringExtra("name");
                    updateData();
                    break;
                case CITY:
                    if (data == null)
                        break;
                    right_text[CITY - 1] = data.getStringExtra("city");
                    updateData();
                    break;
//			case BRIEF:
//				if (data == null)
//					break;
//				right_text[BRIEF - 1] = data.getStringExtra("brief");
//				updateData();
//				break;
                case REAL_NAME:
                    if (data == null)
                        break;
                    right_text[REAL_NAME - 1] = data.getStringExtra("real_name");
                    updateData();
                    break;
                case TEL:
                    if (data == null)
                        break;
                    right_text[TEL - 1] = data.getStringExtra("tel");
                    loginEntity.setTel(right_text[TEL - 1]);
                    SPUtils.instance().put(SPUtils.LOGIN_DATA,
                            new Gson().toJson(loginEntity));
                    updateData();
                    break;
                case EMAIL:
                    if (data == null)
                        break;
                    right_text[EMAIL - 1] = data.getStringExtra("email");
                    updateData();
                    break;
                case BEITIE:
                    if (data == null)
                        break;
                    right_text[BEITIE - 1] = data.getStringExtra(EDITTEXT);
                    updateData();
                    break;
                case SHUTI:
                    if (data == null)
                        break;
                    right_text[SHUTI - 1] = data.getStringExtra(EDITTEXT);
                    updateData();
                    break;
                case HOBBY:
                    if (data == null)
                        break;
                    right_text[HOBBY - 1] = data.getStringExtra(EDITTEXT);
                    updateData();
                    break;
                case SCHOOL:
                    if (data == null)
                        break;
                    right_text[SCHOOL - 1] = data.getStringExtra(EDITTEXT);
                    updateData();
                    break;
                case PROFESSION:
                    if (data == null)
                        break;
                    right_text[PROFESSION - 1] = data.getStringExtra(EDITTEXT);
                    updateData();
                    break;
                case GOODAT:
                    if (data == null)
                        break;
                    right_text[GOODAT - 1] = data.getStringExtra(EDITTEXT);
                    updateData();
                    break;
                default:
                    break;
            }
        }
    }

    private void updateData() {
        list = getData();
        adapter.setData(list);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_man:
            case R.id.btn_women:
                right_text[SEX - 1] = (String) ((Button) v).getText();
                sexDlg.dismiss();
                updateData();
                break;

            default:
                break;
        }
    }

    @Override
    public void onTopRightTxtClick(View view) {
        dlgLoad.loading();
        if (!TextUtils.isEmpty(photoPath)) {
            commitFilesOss(photoPath, mHandler);
        } else {
            uploadMyInfo();
        }
    }

    /**
     * 更新个人信息
     */
    private void uploadMyInfo() {

        String uname = right_text[NAME - 1];
        String birth_day = right_text[BIRTH - 1];
        age = "";
        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(birth_day);
            // String now = new
            // SimpleDateFormat("yyyy年MM月dd日").format(date);
            LogUtils.e("年龄 " + getAge(date));
            // 根据出生年月动态设置年龄
            age = "" + getAge(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String sex = right_text[SEX - 1];
        String addr = right_text[CITY - 1];
        String fav = "";
        String interest = right_text[HOBBY - 1];
        String qq = "";
        String bei_tie = right_text[BEITIE - 1];
        String fav_font = right_text[SHUTI - 1];
        String stu_time = "";
        String school = right_text[SCHOOL - 1];
        String company = "";
        String profession = right_text[PROFESSION - 1];
//		String signature = right_text[BRIEF - 1];
        String email0 = right_text[EMAIL - 1];
        String real_name = right_text[REAL_NAME - 1];
        String goodat = right_text[GOODAT - 1];
        List<Double> coord = new ArrayList<Double>();
        coord.add(0.0);
        coord.add(0.0);

        RequestManager.request(this, new EditInfoParam(uname, birth_day, age,
                sex, addr, fav, interest, qq, bei_tie, fav_font, stu_time,
                school, company, profession, loginEntity.getSignature(), email0, real_name,
                coord, goodat), EditMyInfoResponse.class, this, Constant.URL);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onResponse(BaseResponse response) {
        super.onResponse(response);
        // ToastUtil.show(response.getResMsg());
        if ("0".equals(response.getResCode())) {
            // 更新头像返回
            if (response instanceof HeadUpdateResponse) {
                ToastUtil.show("头像更新成功");
                loginEntity.setHead_img(headerPath);
                if (isUserInfoModify) {
                    uploadMyInfo();
                } else {
                    dlgLoad.dismissDialog();
                    setTopRightTxt(null);
                }
                SPUtils.instance().put(SPUtils.LOGIN_DATA,
                        new Gson().toJson(loginEntity));
                // 将最新的个人信息同步给融云
                if (RongIM.getInstance() != null) {
                    String headurl = StringUtil
                            .getHeadUrl(SPUtils
                                    .instance()
                                    .getLoginEntity()
                                    .getHead_img());
                    io.rong.imlib.model.UserInfo userInfo = new io.rong.imlib.model.UserInfo(
                            loginEntity.get_id(),
                            SPUtils.instance()
                                    .getLoginEntity()
                                    .getUname(),
                            TextUtils.isEmpty(headurl) ? null : Uri.parse(headurl));
                    RongIM.getInstance()
                            .setCurrentUserInfo(
                                    userInfo);
                    RongIM.getInstance()
                            .setMessageAttachedUserInfo(
                                    true);

                }
            }
            // 更新其他信息返回
            else if (response instanceof EditMyInfoResponse) {
                dlgLoad.dismissDialog();
                isUserInfoModify = false;
                ToastUtil.show("修改成功");
                setTopRightTxt(null);
                loginEntity.setUname(right_text[NAME - 1]);
                loginEntity.setBirth_day(right_text[BIRTH - 1]);
                loginEntity.setAge(age);
                loginEntity.setSex(right_text[SEX - 1]);
                loginEntity.setAddr(right_text[CITY - 1]);
                loginEntity.setInterest(right_text[HOBBY - 1]);
                loginEntity.setBei_tie(right_text[BEITIE - 1]);
                loginEntity.setFav_font(right_text[SHUTI - 1]);
                loginEntity.setSchool(right_text[SCHOOL - 1]);
                loginEntity.setProfession(right_text[PROFESSION - 1]);
//				loginEntity.setSignature(right_text[BRIEF - 1]);
                loginEntity.setEmail0(right_text[EMAIL - 1]);
                loginEntity.setReal_name(right_text[REAL_NAME - 1]);
                loginEntity.setGood_at(right_text[GOODAT - 1]);

                //遗漏了绑定手机号
                loginEntity.setTel(right_text[TEL - 1]);

                SPUtils.instance().put(SPUtils.LOGIN_DATA,
                        new Gson().toJson(loginEntity));
            }
        }
    }

    protected void setResultFinish(int resultCode) {
        SPUtils.instance().put(SPUtils.LOGIN_DATA,
                new Gson().toJson(loginEntity));
        Intent intent = new Intent();
        // intent.putExtra("age", age);
        setResult(resultCode, intent);
        finish();
    }

    // @Override
    // public void onBackPressed() {
    // setResultFinish(RESULT_OK);
    // }

    // @Override
    // public void onTopLeftClick(View view) {
    // setResultFinish(RESULT_OK);
    // }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case DATE_DIALOG:
                c = Calendar.getInstance();
                dialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker dp, int year,
                                                  int month, int dayOfMonth) {
                                String beginTime = new StringBuffer()
                                        .append(year)
                                        .append("-")
                                        .append(month < 10 ? "0" + (month + 1)
                                                : month + 1)
                                        .append("-")
                                        .append(dayOfMonth < 10 ? "0" + dayOfMonth
                                                : dayOfMonth).toString();
                                try {
                                    Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                            .parse(beginTime);
                                    beginTime = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                            .format(date);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                right_text[BIRTH - 1] = beginTime;
                                setTopRightTxt("完成");
                                updateData();
                            }
                        }, c.get(Calendar.YEAR), // 传入年份
                        c.get(Calendar.MONTH), // 传入月份
                        c.get(Calendar.DAY_OF_MONTH) // 传入天数
                );
                break;
        }
        return dialog;
    }

    public static int getAge(Date dateOfBirth) {
        int age = 0;
        Calendar born = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        if (dateOfBirth != null) {
            now.setTime(new Date());
            born.setTime(dateOfBirth);
            if (born.after(now)) {
                // throw new IllegalArgumentException(
                // "Can't be born in the future");
                ToastUtil.show("不能出生在未来");
            }
            age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
            if (now.get(Calendar.DAY_OF_YEAR) < born.get(Calendar.DAY_OF_YEAR)) {
                age -= 1;
            }
        }
        return age;
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 201:
                    ToastUtil.show("上传失败。");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public void uploadHeadToOSS(String locPath) {

        OssTokenInfo ossInfo = SPUtils.instance().getOSSTokenInfo();
        SocialPropEntity propEntity = SPUtils.instance().getSocialPropEntity();
        OSS oss = OSSSTS(ossInfo.getAccess_key_id(),
                ossInfo.getAccess_key_Secret(), ossInfo.getSecurity_token(),
                ossInfo.getExpiration());
        final String ossHeadPath = propEntity.getOss_root() + "/"
                + loginEntity.get_id() + Constant.OSS_IMAGE_PATH
                + System.currentTimeMillis() + ".png";

        PutObjectRequest put = new PutObjectRequest(propEntity.getOss_bucket(),
                ossHeadPath, locPath);

        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {

            @Override
            public void onProgress(PutObjectRequest arg0, long currentSize,
                                   long totalSize) {
            }
        });

        OSSAsyncTask task = oss.asyncPutObject(put,
                new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {

                    @Override
                    public void onSuccess(PutObjectRequest request,
                                          PutObjectResult result) {
                        headerPath = ossHeadPath;
                        RequestManager.request(MyInfoActivity.this,
                                new HeadUpdataParams(ossHeadPath),
                                HeadUpdateResponse.class, MyInfoActivity.this,
                                Constant.URL);
                    }

                    @Override
                    public void onFailure(PutObjectRequest request,
                                          ClientException clientExcepion,
                                          ServiceException serviceException) {
                        StringBuffer bufString = new StringBuffer();
                        bufString.append("头像更新失败");
                        if (isUserInfoModify) {
                            bufString.append(",开始更新其他信息");
                            uploadMyInfo();
                        } else {
                            dlgLoad.dismissDialog();
                        }
                        ToastUtil.show(bufString.toString());
                    }
                });

        // new Thread(new Runnable() {
        // @Override
        // public void run() {
        // while (true) {
        // if (flag == 1) { // 标识头像上传成功
        // RequestManager.request(MyInfoActivity.this,new
        // HeadUpdataParams(ossHeadPath),
        // SquarePostAddResponse.class,
        // MyInfoActivity.this, Constant.URL);
        // break;
        // } else if (flag == 2) {
        // Message message = new Message();
        // message.what = 201;
        // handler.sendMessage(message);
        // break;
        // }
        // }
        // }
        // }).start();

    }
}
