package com.easier.writepre.receive;

import java.util.Calendar;

import com.easier.writepre.R;
import com.easier.writepre.widget.ReminderDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.WindowManager;

public class Alarmreceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		String time = intent.getStringExtra("time");

		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		String temp = hour + ":" + minute;

		if (time.equals(temp)) {

			Uri notification = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_ALARM);
			Ringtone r = RingtoneManager.getRingtone(context, notification);
			r.play();

			// 弹出全局对话框
			ReminderDialog reminderDialog = new ReminderDialog(context, r,
					R.style.MyDialog, intent.getStringExtra("name"),
					intent.getStringExtra("remark"));
			// 设置它的ContentView
			reminderDialog.setContentView(R.layout.reminder_dialog);
			reminderDialog.getWindow().setType(
					WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			reminderDialog.show();

		}

	}
}
