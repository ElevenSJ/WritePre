package com.easier.writepre.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.easier.writepre.R;
import com.easier.writepre.adapter.MyListAdapter;
import com.easier.writepre.entity.LoginEntity;
import com.easier.writepre.entity.LuoYangSchool;
import com.easier.writepre.entity.MyListData;
import com.easier.writepre.entity.SchoolClass;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.EditInfoParam;
import com.easier.writepre.param.PkSignUpParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.EditMyInfoResponse;
import com.easier.writepre.response.PkSignUpResponse;
import com.easier.writepre.ui.myinfo.EditRealNameActivity;
import com.easier.writepre.ui.myinfo.EditTeacherActivity;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.google.gson.Gson;
import com.jock.pickerview.view.OptionsPickerView;
import com.jock.pickerview.view.TimePickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 展赛报名
 *
 * @author zhaomaohan
 */
public class PkViewSignUpActivity extends BaseActivity implements
        OnItemClickListener {

    private LoginEntity info;// 保存个人信息的对象

    private TextView tv_next;// 下一步，转到上传作品界面。

    private String pk_id;
    private String pk_type;
    private String role;

    private String works_no;

    private String works_id;

    private ListView lv_list;
    private List<MyListData> list;
    private MyListAdapter adapter;
    public static String left_text[] = new String[]{"姓名", "年龄", "区县", "学校",
            "年级", "指导老师"};
    public String center_text[] = new String[]{null, null, null, null, null,
            null};

    final private int REAL_NAME = 0, AGE = 1, CITY = 2, SCHOOL = 3, GRADE = 4,
            TEACHER = 5;

    TimePickerView pvTime;
    OptionsPickerView pvOptions;

    OptionsPickerView pvSchool;

    private ArrayList<SchoolClass> item1 = new ArrayList<>();

    private LuoYangSchool schools;

    private ArrayList<LuoYangSchool.SchoolValue> schoolValue1 = new ArrayList<>();

    private ArrayList<ArrayList<LuoYangSchool.SchoolValue>> schoolValue2 = new ArrayList<>();


    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            dlgLoad.dismissDialog();
            switch (msg.what) {
                case 0x123:
                    pvOptions.setPicker(item1);
                    pvOptions.setCyclic(true);
                    pvOptions.setSelectOptions(0);
                    pvOptions.show();
                    break;
                case 0x124:
                    pvSchool.setPicker(schoolValue1, schoolValue2, true);
                    pvSchool.setCyclic(true, true, true);
                    pvSchool.setSelectOptions(0, 0);
                    pvSchool.show();
                    break;
                case 0x125:
                    ToastUtil.show("加载数据异常");
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pk_signup);
        pk_id = getIntent().getStringExtra("pk_id");
        pk_type= getIntent().getStringExtra("pk_type");
        init();
    }

    private void init() {
        setTopTitle("报名参赛");
        tv_next = (TextView) findViewById(R.id.tv_next);

        lv_list = (ListView) findViewById(R.id.lv_list);

        list = getData();

        adapter = new MyListAdapter(this, list);
        lv_list.setAdapter(adapter);

        lv_list.setOnItemClickListener(this);
        // tv_my_info.setOnClickListener(this);
        tv_next.setOnClickListener(this);
        initinfo();

        // 时间选择器
        pvTime = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        pvTime.setTitle("选择出生日期");
        // 控制时间范围
        Calendar calendar = Calendar.getInstance();
        pvTime.setRange(calendar.get(Calendar.YEAR) - 100,
                calendar.get(Calendar.YEAR));
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        // 时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                center_text[AGE] = "" + getAge(date);
                updateData();
            }
        });
        // 选项选择器
        pvOptions = new OptionsPickerView(this);
        pvOptions.setTitle("选择年级");
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                // 返回的分别是三个级别的选中位置
                String tx = item1.get(options1).getPickerViewText();
                center_text[GRADE] = tx;
                updateData();
            }
        });

        // 选项选择器
        pvSchool = new OptionsPickerView(this);
        pvSchool.setTitle("选择学校(洛阳市)");
        pvSchool.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                // 返回的分别是三个级别的选中位置
                center_text[CITY] = schoolValue1.get(options1).getPickerViewText();
                center_text[SCHOOL] = schoolValue2.get(options1).get(option2).getPickerViewText();
                updateData();
            }
        });
    }

    // 获取动态数组数据 可以由其他地方传来(json等)
    private List<MyListData> getData() {
        List<MyListData> list = new ArrayList<MyListData>();
        for (int i = 0; i < left_text.length; i++) {
            MyListData myListData = new MyListData(-1, left_text[i], -1, null,
                    center_text[i]);
            list.add(myListData);
        }
        return list;
    }

    private void initinfo() {
        info = SPUtils.instance().getLoginEntity();
        center_text[REAL_NAME] = info.getReal_name();
        center_text[AGE] = info.getAge();
        center_text[CITY] = info.getAddr();
        center_text[SCHOOL] = info.getSchool();

        updateData();
    }

    private void updateData() {
        list = getData();
        adapter.setData(list);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.tv_next:
                // 判断报名信息
                if (TextUtils.isEmpty(center_text[REAL_NAME])
                        || TextUtils.isEmpty(center_text[AGE])
                        || TextUtils.isEmpty(center_text[CITY])
                        || TextUtils.isEmpty(center_text[GRADE])
                        || TextUtils.isEmpty(center_text[SCHOOL])) {
                    ToastUtil.show("请完善信息");
                } else {
                    dlgLoad.loading();
                    RequestManager.request(this, new PkSignUpParams(pk_id,
                                    "*", "1", center_text[TEACHER], new PkSignUpParams.ExtInfo(center_text[REAL_NAME], center_text[AGE], center_text[SCHOOL], center_text[GRADE])),
                            PkSignUpResponse.class, this, SPUtils.instance()
                                    .getSocialPropEntity()
                                    .getApp_socail_server());
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REAL_NAME:
                if (resultCode == RESULT_OK) {
                    center_text[REAL_NAME] = data.getExtras()
                            .getString("real_name");
                    updateData();
                }
                break;
            case CITY:
                if (resultCode == RESULT_OK) {
                    center_text[CITY] = data.getStringExtra("city");
                    updateData();
                }
                break;
            case SCHOOL:
                if (resultCode == RESULT_OK) {
                    center_text[SCHOOL] = data
                            .getStringExtra(MyInfoActivity.EDITTEXT);
                    updateData();
                }
                break;
            case TEACHER:
                if (resultCode == RESULT_OK) {
                    center_text[TEACHER] = data.getStringExtra("teacher");
                    ;
                    updateData();
                }
                break;
            case GRADE:
                if (resultCode == RESULT_OK) {
                    center_text[GRADE] = data
                            .getStringExtra(MyInfoActivity.EDITTEXT);
                    updateData();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onResponse(BaseResponse response) {
        dlgLoad.dismissDialog();

        if ("0".equals(response.getResCode())) {

            if (response instanceof PkSignUpResponse) {
                dlgLoad.dismissDialog();
                PkSignUpResponse mPkSignUpResponse = (PkSignUpResponse) response;
                works_no = mPkSignUpResponse.getRepBody().getWorks_no();
                works_id = mPkSignUpResponse.getRepBody().getWorks_id();
                RequestManager.request(
                        this,
                        new EditInfoParam(info.getUname(), info.getBirth_day(),
                                center_text[AGE], info.getSex(),
                                center_text[CITY], info.getFav(), info
                                .getInterest(), info.getQq(), info
                                .getBei_tie(), info.getFav_font(), info
                                .getStu_time(), center_text[SCHOOL],
                                info.getCompany(), info.getProfession(), info
                                .getSignature(), info.getEmail(),
                                center_text[REAL_NAME], info.getCoord(), info
                                .getGood_at()),
                        EditMyInfoResponse.class, this, Constant.URL);
                dialog();
            } else if (response instanceof EditMyInfoResponse) {
                info.setReal_name(center_text[REAL_NAME]);
                info.setAge(center_text[AGE]);
                info.setAddr(center_text[CITY]);
                info.setSchool(center_text[SCHOOL]);
                SPUtils.instance().put(SPUtils.LOGIN_DATA,
                        new Gson().toJson(info));
            }
        } else {
            ToastUtil.show(response.getResMsg());
        }
    }

    private void dialog() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog){
            @Override
            public void dismiss() {
                super.dismiss();
                finish();
            }
        };
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_islogin,
                null);
        TextView tv_login_now = (TextView) view.findViewById(R.id.tv_login_now);
        TextView tv_login = (TextView) view.findViewById(R.id.tv_login);
        TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_login_now.setText("提交作品?");
        tv_login.setText("现在提交");
        tv_cancel.setText("稍后提交");
        view.findViewById(R.id.tv_login).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) { // 现在提交
                        Intent intent = new Intent(PkViewSignUpActivity.this,
                                PkUpdateWorksActivity.class);
                        intent.putExtra("pk_id", pk_id);
                        intent.putExtra("pk_type", pk_type);
                        intent.putExtra("works_id", works_id);
                        intent.putExtra("works_no", works_no);
                        intent.putExtra("teacher", center_text[TEACHER]);
                        intent.putExtra("selected_work", "");
                        intent.putExtra("user_nike", center_text[REAL_NAME]);
                        startActivity(intent);
                        dialog.dismiss();
                        finish();
                    }
                });
        view.findViewById(R.id.tv_cancel).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) { // 稍后提交
                        finish();
                        dialog.dismiss();
                    }
                });
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // initinfo();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        switch (position) {
            case REAL_NAME:
                Intent intent0 = new Intent(this, EditRealNameActivity.class);
                intent0.putExtra(MyInfoActivity.OLD_REALNAME,
                        center_text[REAL_NAME]);
                startActivityForResult(intent0, REAL_NAME);
                break;
            case AGE:
                pvTime.show();
                break;
            case CITY:
//                loadCityDate();
                loadSchoolData();
                break;
            case SCHOOL:
//                Intent intent5 = new Intent(this, EditInfoReuseActivity.class);
//                intent5.putExtra(MyInfoActivity.EDIT_TYPE, MyInfoActivity.SCHOOL);
//                intent5.putExtra(MyInfoActivity.OLD_SCHOOL, center_text[SCHOOL]);
//                startActivityForResult(intent5, SCHOOL);
                loadSchoolData();
                break;
            case TEACHER:
                Intent intent6 = new Intent(this, EditTeacherActivity.class);
                intent6.putExtra(PkSignUpActivity.OLD_TEACHER, center_text[TEACHER]);
                startActivityForResult(intent6, TEACHER);
                break;
            case GRADE:
//                Intent intent7 = new Intent(this, EditInfoReuseActivity.class);
//                intent7.putExtra(MyInfoActivity.EDIT_TYPE, MyInfoActivity.GRADE);
//                intent7.putExtra(MyInfoActivity.OLD_GRADE, center_text[GRADE]);
//                startActivityForResult(intent7, GRADE);
                loadClassDate();
                break;
            default:
                break;
        }

    }

    /**
     * 加载年级数据
     */
    private void loadClassDate() {
        dlgLoad.loading();
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (!item1.isEmpty()) {
                    handler.sendEmptyMessage(0x123);
                    return;
                }
                String[] classes = getResources().getStringArray(R.array.school_class);
                for (int i = 0; i < classes.length; i++) {
                    item1.add(new SchoolClass(classes[i]));
                }
                handler.sendEmptyMessage(0x123);
            }
        }).start();
    }

    /**
     * 加载学校数据
     */
    private void loadSchoolData() {
        dlgLoad.loading();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!schoolValue1.isEmpty() && !schoolValue2.isEmpty()) {
                    handler.sendEmptyMessage(0x124);
                    return;
                }
                schools = new Gson().fromJson(getResources().getString(R.string.luoyang_school), LuoYangSchool.class);
                if (schools == null) {
                    handler.sendEmptyMessage(0x125);
                }
                LogUtils.e("schools size:" + schools.getArray().size());
                for (LuoYangSchool.CityValue city : schools.getArray()) {
                    LogUtils.e("schools:" + city.getCity() + "-" + city.getList().size());
                    schoolValue1.add(new LuoYangSchool.SchoolValue(city.getCity()));
                }
                for (LuoYangSchool.SchoolValue value : schoolValue1) {
                    ArrayList<LuoYangSchool.SchoolValue> list = new ArrayList<LuoYangSchool.SchoolValue>();
                    for (LuoYangSchool.CityValue city : schools.getArray()) {
                        if (value.getValue().equals(city.getCity())) {
                            for (String schoolName : city.getList()) {
                                list.add(new LuoYangSchool.SchoolValue(schoolName));
                            }
                        }
                    }
                    schoolValue2.add(list);
                }
                handler.sendEmptyMessage(0x124);
            }
        }).start();
    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
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
            } else {
                age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
                if (now.get(Calendar.DAY_OF_YEAR) < born
                        .get(Calendar.DAY_OF_YEAR)) {
                    age -= 1;
                }
            }
        }
        return age;
    }
}
