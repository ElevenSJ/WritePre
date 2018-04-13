package com.easier.writepre.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.easier.writepre.R;
import com.easier.writepre.adapter.MyListAdapter;
import com.easier.writepre.entity.LoginEntity;
import com.easier.writepre.entity.MyListData;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.mainview.SocialMainView;
import com.easier.writepre.param.EditInfoParam;
import com.easier.writepre.param.PkSignUpParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.EditMyInfoResponse;
import com.easier.writepre.response.PkSignUpResponse;
import com.easier.writepre.ui.myinfo.EditCityActivity;
import com.easier.writepre.ui.myinfo.EditEmailActivity;
import com.easier.writepre.ui.myinfo.EditInfoReuseActivity;
import com.easier.writepre.ui.myinfo.EditRealNameActivity;
import com.easier.writepre.ui.myinfo.EditTeacherActivity;
import com.easier.writepre.ui.myinfo.EditTelActivity;
import com.easier.writepre.utils.LogUtils;
import com.easier.writepre.utils.SPUtils;
import com.easier.writepre.utils.ToastUtil;
import com.google.gson.Gson;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 大赛报名_确认报名信息页面
 * 
 * @author zhaomaohan
 * 
 */
public class PkSignUpActivity extends BaseActivity implements
		OnItemClickListener {

	private TextView tv_my_info;// 个人信息按钮，转到个人信息界面。

	private LoginEntity info;// 保存个人信息的对象

	private TextView tv_next;// 下一步，转到上传作品界面。
	private String selected_role;
	private String selected_work;
	private String my_info_name;

	private String pk_id;
	private String pk_type;
	private String role;
	private String selected_work_id;

	private String works_no;

	private String works_id;

	private String teacher;

	private ListView lv_list;
	private List<MyListData> list;
	private MyListAdapter adapter;
	public static String left_text[] = new String[] { "姓名", "年龄", "电话", "地区",
			"邮箱", "学校", "指导老师", "参会组别" };
	public String center_text[] = new String[] { null, null, null, null, null,
			null, "", null };

	final private int REAL_NAME = 0, AGE = 1, TEL = 2, CITY = 3, EMAIL = 4,
			SCHOOL = 5, TEACHER = 6, GROUP = 7;
	private final static int DATE_DIALOG = 0;
	private Calendar c = null;

	protected String birthday;

	public static String OLD_TEACHER = "old_teacher";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pk_signup);
		pk_id = getIntent().getStringExtra("pk_id");
		pk_type= getIntent().getStringExtra("pk_type");
		role = getIntent().getStringExtra("role");
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
		center_text[0] = info.getReal_name();
		center_text[1] = info.getAge();
		center_text[2] = info.getLogin_tel();
		center_text[3] = info.getAddr();
		center_text[4] = info.getEmail0();
		center_text[5] = info.getSchool();

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
					|| TextUtils.isEmpty(center_text[TEL])
					|| TextUtils.isEmpty(center_text[CITY])
					|| TextUtils.isEmpty(center_text[EMAIL])
					|| TextUtils.isEmpty(center_text[SCHOOL])) {
				ToastUtil.show("请完善个人信息");
			} else {
				dlgLoad.loading();
				RequestManager.request(
						this,
						new EditInfoParam(info.getUname(), birthday,
								center_text[AGE], info.getSex(),
								center_text[CITY], info.getFav(), info
										.getInterest(), info.getQq(), info
										.getBei_tie(), info.getFav_font(), info
										.getStu_time(), center_text[SCHOOL],
								info.getCompany(), info.getProfession(), info
										.getSignature(), center_text[EMAIL],
								center_text[REAL_NAME], info.getCoord(), info
										.getGood_at()),
						EditMyInfoResponse.class, this, Constant.URL);
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
		case TEL:
			if (resultCode == RESULT_OK) {
				center_text[TEL] = data.getStringExtra("tel");
				updateData();
			}
			break;
		case CITY:
			if (resultCode == RESULT_OK) {
				center_text[CITY] = data.getStringExtra("city");
				updateData();
			}
			break;
		case EMAIL:
			if (resultCode == RESULT_OK) {
				center_text[EMAIL] = data.getStringExtra("email");
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
				updateData();
			}
			break;
		case GROUP:
			switch (resultCode) {
			case RESULT_OK:
				selected_role = data.getExtras().getString("selected_role");// 得到新Activity
																			// 关闭后返回的数据
				selected_work = data.getExtras().getString("selected_work");// 得到新Activity
																			// 关闭后返回的数据
				selected_work_id = data.getExtras().getString(
						"selected_work_id");
				center_text[GROUP] = selected_role + " " + selected_work;
				updateData();
				// ToastUtil.show("参会组别改动成功");
				if (selected_role.equals("教师专区")) {
					role = "2";
				} else {
					role = "1";
				}

				break;
			case RESULT_CANCELED:
				// ToastUtil.show("参会组别没有改动");
				break;
			default:
				break;
			}

			break;

		default:
			break;
		}
	}

	@Override
	public void onResponse(BaseResponse response) {
		// dlgLoad.dismissDialog();

		if ("0".equals(response.getResCode())) {

			if (response instanceof PkSignUpResponse) {
				dlgLoad.dismissDialog();
				PkSignUpResponse mPkSignUpResponse = (PkSignUpResponse) response;
				works_no = mPkSignUpResponse.getRepBody().getWorks_no();
				works_id = mPkSignUpResponse.getRepBody().getWorks_id();
				dialog();
			} else if (response instanceof EditMyInfoResponse) {
				info.setReal_name(center_text[REAL_NAME]);
				info.setBirth_day(birthday);
				info.setAge(center_text[AGE]);
				info.setAddr(center_text[CITY]);
				info.setEmail0(center_text[EMAIL]);
				info.setSchool(center_text[SCHOOL]);
				SPUtils.instance().put(SPUtils.LOGIN_DATA,
						new Gson().toJson(info));
				if (selected_work_id != null) {
					teacher = center_text[TEACHER];
					// dlgLoad.loading();
					RequestManager.request(this, new PkSignUpParams(pk_id,
									"＊", role, teacher,new PkSignUpParams.ExtInfo(center_text[REAL_NAME],center_text[AGE],center_text[SCHOOL],center_text[EMAIL])),
							PkSignUpResponse.class, this, SPUtils.instance()
									.getSocialPropEntity()
									.getApp_socail_server());
				} else {
					dlgLoad.dismissDialog();
					ToastUtil.show("参会组别不能为空");
				}
			}
		} else {
			// ToastUtil.show(response.getResCode());
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
						Intent intent = new Intent(PkSignUpActivity.this,
								PkUpdateWorksActivity.class);
						intent.putExtra("pk_id", pk_id);
						intent.putExtra("pk_type", pk_type);
						intent.putExtra("works_id", works_id);
						intent.putExtra("works_no", works_no);
						intent.putExtra("teacher", teacher);
						intent.putExtra("selected_work", selected_work);
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
						dialog.dismiss();
						finish();
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
			showDialog(DATE_DIALOG);
			break;
		case TEL:
			Intent intent2 = new Intent(this, EditTelActivity.class);
			startActivityForResult(intent2, TEL);
			break;
		case CITY:
			Intent intent3 = new Intent(this, EditCityActivity.class);
			startActivityForResult(intent3, CITY);
			break;
		case EMAIL:
			Intent intent4 = new Intent(this, EditEmailActivity.class);
			intent4.putExtra(MyInfoActivity.OLD_EMAIL, center_text[EMAIL]);
			startActivityForResult(intent4, EMAIL);
			break;
		case SCHOOL:
			Intent intent5 = new Intent(this, EditInfoReuseActivity.class);
			intent5.putExtra(MyInfoActivity.EDIT_TYPE, MyInfoActivity.SCHOOL);
			intent5.putExtra(MyInfoActivity.OLD_SCHOOL, center_text[SCHOOL]);
			startActivityForResult(intent5, SCHOOL);
			break;
		case TEACHER:
			Intent intent6 = new Intent(this, EditTeacherActivity.class);
			intent6.putExtra(OLD_TEACHER, center_text[TEACHER]);
			startActivityForResult(intent6, TEACHER);
			break;
		case GROUP:
			Intent intent7 = new Intent(this, SignUpSelectGroupActivity.class);
			startActivityForResult(intent7, GROUP);
			break;
		default:
			break;
		}

	}

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
							birthday = new StringBuffer().append(year)
									.append("-").append(month + 1).append("-")
									.append(dayOfMonth).toString();
							Date date;
							try {
								date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
										.parse(birthday);
								LogUtils.e("出生日期:"+new
										 SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).format(date));
//								 String now = new
//								 SimpleDateFormat("yyyy年MM月dd日").format(date);
								LogUtils.e("年龄 " + getAge(date));
								// 根据出生年月动态设置年龄
								center_text[AGE] = "" + getAge(date);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
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
