package com.easier.writepre.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.easier.writepre.R;
import com.easier.writepre.adapter.PCDAdapter;
import com.easier.writepre.entity.CourseInfo;
import com.easier.writepre.entity.Reminder;
import com.easier.writepre.entity.ScreenInfo;
import com.easier.writepre.entity.WheelTime;
import com.easier.writepre.http.Constant;
import com.easier.writepre.http.RequestManager;
import com.easier.writepre.param.MyCouresListParams;
import com.easier.writepre.response.BaseResponse;
import com.easier.writepre.response.MyCourseListResponse;
import com.easier.writepre.response.MyCourseListResponse.MyCourseListBody;
import com.easier.writepre.utils.PollingUtils;
import com.easier.writepre.utils.ToastUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyReminderActivity extends BaseActivity implements OnItemClickListener {

	private ImageView iv_empty;
	private ListView lv_clock;
	private Button btn_cancel;
	private Button btn_save;
	private TextView tv_title;
	private LinearLayout ll_default;
	private LinearLayout timePicker1;

	private int screenHeight, screenWidth;

	private final List<CourseInfo> courseInfoList = new ArrayList<CourseInfo>();

	private List<Reminder> list;

	private ListView lv_pop_course;
	private TextView tv_repeat;
	private TextView tv_course;

	private int flag;// 1:重复2：课程

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_my_reminder);
		init();
	}

	private void init() {

		screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		screenWidth = getWindowManager().getDefaultDisplay().getWidth();

		iv_empty = (ImageView) findViewById(R.id.iv_empty);
		lv_clock = (ListView) findViewById(R.id.lv_clock);

		findAllReminder();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_add:
			showTimeDialog();
			break;
		default:
			break;
		}
	}

	// 时间弹出框
	@SuppressLint("SimpleDateFormat")
	private void showTimeDialog() {

		LayoutInflater inflater = LayoutInflater.from(this);
		View dateview = inflater.inflate(R.layout.dialog_time_choice, null);
		ScreenInfo screenInfo = new ScreenInfo(this);
		final WheelTime wheelMain = new WheelTime(dateview);
		wheelMain.screenheight = screenInfo.getHeight();
		Calendar calendar = Calendar.getInstance();

		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int mini = calendar.get(Calendar.MINUTE);
		wheelMain.initTimePicker(hour, mini);

		final AlertDialog dlg = new AlertDialog.Builder(this).create();
		dlg.show();

		Window window = dlg.getWindow();
		window.setContentView(dateview);
		window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

		WindowManager.LayoutParams params = dlg.getWindow().getAttributes();
		params.width = screenWidth * 2 / 5;
		params.height = screenHeight * 4 / 5;
		// params.x = -120;
		// params.y = 120;
		dlg.getWindow().setAttributes(params);

		lv_pop_course = (ListView) dateview.findViewById(R.id.lv_pop_course);
		lv_pop_course.setOnItemClickListener(this);
		ll_default = (LinearLayout) dateview.findViewById(R.id.ll_default);
		timePicker1 = (LinearLayout) dateview.findViewById(R.id.timePicker1);

		RelativeLayout rl_repeat = (RelativeLayout) dateview
				.findViewById(R.id.rl_repeat);
		tv_repeat = (TextView) dateview.findViewById(R.id.tv_repeat);

		RelativeLayout rl_course = (RelativeLayout) dateview
				.findViewById(R.id.rl_course);
		tv_course = (TextView) dateview.findViewById(R.id.tv_course);

		final EditText et_remark = (EditText) dateview
				.findViewById(R.id.et_remark);

		btn_cancel = (Button) dateview.findViewById(R.id.btn_cancel);
		btn_save = (Button) dateview.findViewById(R.id.btn_save);
		tv_title = (TextView) dateview.findViewById(R.id.tv_title);

		btn_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ("取消".equals(btn_cancel.getText().toString())) {
					dlg.dismiss();
				} else {
					tv_title.setText("添加提醒");
					btn_cancel.setText("取消");
					btn_save.setVisibility(View.VISIBLE);
					ll_default.setVisibility(View.VISIBLE);
					timePicker1.setVisibility(View.VISIBLE);
					lv_pop_course.setVisibility(View.GONE);
				}
			}
		});

		btn_save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// tv_time.setText(wheelMain.getTime());
				if (TextUtils.isEmpty(tv_course.getText().toString())) {
					ToastUtil.show("请选择课程");
					return;
				}
				dlg.dismiss();
				Reminder reminder = new Reminder();
				int x = (int) (Math.random() * 100);
				reminder.setId(x);
				reminder.setTitle(tv_course.getText().toString());
				reminder.setTime(wheelMain.getTime());
				reminder.setWeek(tv_repeat.getText().toString());
				reminder.setRemark(et_remark.getText().toString());
				reminder.setToggle("1");
				reminder.setIsDeleteButtonVisible("0");
				PollingUtils.triggerAlarm(MyReminderActivity.this, reminder);
//				try {
//					BaseApplication.db.save(reminder);
//				} catch (DbException e) {
//					e.printStackTrace();
//				}
				//保存数据库

				findAllReminder();

				// triggerAlarm();

			}
		});

		rl_course.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				flag = 2;
				tv_title.setText("课程");
				btn_cancel.setText("返回");
				btn_save.setVisibility(View.GONE);
				ll_default.setVisibility(View.GONE);
				timePicker1.setVisibility(View.GONE);
				lv_pop_course.setVisibility(View.VISIBLE);
				RequestManager.request(MyReminderActivity.this,new MyCouresListParams("10", ""),
						MyCourseListResponse.class, MyReminderActivity.this,Constant.URL);

			}
		});

		rl_repeat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				flag = 1;
				tv_title.setText("重复");
				btn_cancel.setText("返回");
				btn_save.setVisibility(View.GONE);
				ll_default.setVisibility(View.GONE);
				timePicker1.setVisibility(View.GONE);
				lv_pop_course.setVisibility(View.VISIBLE);
				courseInfoList.clear();
				courseInfoList.add(new CourseInfo("每周日"));
				courseInfoList.add(new CourseInfo("每周一"));
				courseInfoList.add(new CourseInfo("每周二"));
				courseInfoList.add(new CourseInfo("每周三"));
				courseInfoList.add(new CourseInfo("每周四"));
				courseInfoList.add(new CourseInfo("每周五"));
				courseInfoList.add(new CourseInfo("每周六"));
				PCDAdapter mCourseAdapter = new PCDAdapter(
						MyReminderActivity.this, courseInfoList);
				lv_pop_course.setAdapter(mCourseAdapter);

			}
		});

	}

	@Override
	public void onResponse(BaseResponse response) {

		if ("0".equals(response.getResCode())) {
			if (response instanceof MyCourseListResponse) {
				MyCourseListResponse myCourseListResponse = (MyCourseListResponse) response;
				MyCourseListBody myCourseListBody = myCourseListResponse
						.getRepBody();
				courseInfoList.clear();
				courseInfoList.addAll(myCourseListBody.getList());
				if (courseInfoList != null && courseInfoList.size() > 0) {
					PCDAdapter mCourseAdapter = new PCDAdapter(this,
							courseInfoList);
					lv_pop_course.setAdapter(mCourseAdapter);
				}

			}
		} else {
			ToastUtil.show(response.getResMsg());
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long arg3) {
		if (flag == 1) {
			tv_repeat.setText(courseInfoList.get(position).getTitle());
		} else {
			tv_course.setText(courseInfoList.get(position).getTitle());
		}
		tv_title.setText("添加提醒");
		btn_cancel.setText("取消");
		btn_save.setVisibility(View.VISIBLE);
		ll_default.setVisibility(View.VISIBLE);
		timePicker1.setVisibility(View.VISIBLE);
		lv_pop_course.setVisibility(View.GONE);
	}

	private void findAllReminder() {

//		try {
//			list = BaseApplication.db.findAll(Reminder.class);
//			if (list != null && list.size() > 0) {
//				lv_clock.setVisibility(View.VISIBLE);
//				iv_empty.setVisibility(View.GONE);
//
//				lv_clock.setAdapter(new ReminderAdapter(this, list));
//
//			} else {
//				lv_clock.setVisibility(View.GONE);
//				iv_empty.setVisibility(View.VISIBLE);
//			}
//		} catch (DbException e) {
//			e.printStackTrace();
//		}

	}

}
