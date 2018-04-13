package com.easier.writepre.utils;

import java.util.ArrayList;
import java.util.List;

import com.easier.writepre.entity.Reminder;
import com.easier.writepre.receive.Alarmreceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * 
 * 轮询工具类
 * 
 * @author chenhong
 * @version [DrivingTraining, 2014-10-27]
 */
public class PollingUtils {

	static AlarmManager manager;

	static ArrayList<PendingIntent> mPendingIntentList = new ArrayList<PendingIntent>();

	/**
	 * 
	 * @param context
	 * @param flag
	 *            true:周期性提醒 false：单次提醒
	 * @param mReminder
	 * @param time
	 * @param cls
	 */
	public static void startPollingService(Context context, boolean flag,
			Reminder mReminder, long time, Class<?> cls) {
		// 获取AlarmManager系统服务
		manager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		Intent alarmIntent = new Intent(context, cls);
		alarmIntent.putExtra("name", mReminder.getTitle());
		alarmIntent.putExtra("remark", mReminder.getRemark());
		alarmIntent.putExtra("time", mReminder.getTime());
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
				mReminder.getId(), alarmIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		if (flag) {
			// 周期性闹钟
			manager.setRepeating(AlarmManager.RTC_WAKEUP, time,
					AlarmManager.INTERVAL_DAY * 7, pendingIntent);
		} else {
			// 单次闹钟
			manager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
		}

		mPendingIntentList.add(pendingIntent);
	}

	// public static void cancelClock(Context context, Reminder mReminder) {
	// Intent i = new Intent(context, Alarmreceiver.class);
	// i.putExtra("name", mReminder.getTitle());
	// i.putExtra("remark", mReminder.getRemark());
	// PendingIntent pi = PendingIntent.getBroadcast(context,
	// mReminder.getId(), i, PendingIntent.FLAG_UPDATE_CURRENT);
	// if (manager == null) {
	// manager = (AlarmManager) context
	// .getSystemService(Context.ALARM_SERVICE);
	// }
	// manager.cancel(pi);// 取消闹钟
	// }

	public static void stopPollingService() {

		// 取消正在执行的服务
		if (mPendingIntentList.size() > 0) {
			for (PendingIntent mPendingIntent : mPendingIntentList) {
				manager.cancel(mPendingIntent);
			}
			mPendingIntentList.clear();
		}

	}

	public static void triggerAlarm(Context context, Reminder mReminder) {

		if (mReminder.getWeek().startsWith("每")
				&& "1".equals(mReminder.getToggle())) {
			if ("每周一".equals(mReminder.getWeek())) {

				PollingUtils
						.startPollingService(
								context,
								true,
								mReminder,
								AlarmUtil.getNextAlarmTime(2, "1",
										mReminder.getTime()),
								Alarmreceiver.class);
			} else if ("每周二".equals(mReminder.getWeek())) {
				PollingUtils
						.startPollingService(
								context,
								true,
								mReminder,
								AlarmUtil.getNextAlarmTime(2, "2",
										mReminder.getTime()),
								Alarmreceiver.class);
			} else if ("每周三".equals(mReminder.getWeek())) {
				PollingUtils
						.startPollingService(
								context,
								true,
								mReminder,
								AlarmUtil.getNextAlarmTime(2, "3",
										mReminder.getTime()),
								Alarmreceiver.class);
			} else if ("每周四".equals(mReminder.getWeek())) {
				PollingUtils
						.startPollingService(
								context,
								true,
								mReminder,
								AlarmUtil.getNextAlarmTime(2, "4",
										mReminder.getTime()),
								Alarmreceiver.class);
			} else if ("每周五".equals(mReminder.getWeek())) {
				PollingUtils
						.startPollingService(
								context,
								true,
								mReminder,
								AlarmUtil.getNextAlarmTime(2, "5",
										mReminder.getTime()),
								Alarmreceiver.class);
			} else if ("每周六".equals(mReminder.getWeek())) {
				PollingUtils
						.startPollingService(
								context,
								true,
								mReminder,
								AlarmUtil.getNextAlarmTime(2, "6",
										mReminder.getTime()),
								Alarmreceiver.class);
			} else if ("每周日".equals(mReminder.getWeek())) {
				PollingUtils
						.startPollingService(
								context,
								true,
								mReminder,
								AlarmUtil.getNextAlarmTime(2, "0",
										mReminder.getTime()),
								Alarmreceiver.class);
			}
		} else if (mReminder.getWeek().startsWith("永")
				&& "1".equals(mReminder.getToggle())) {
			final long now = System.currentTimeMillis();
			final long trrigeAtTime = AlarmUtil.getAlarmTime(mReminder
					.getTime());
			final long ss = trrigeAtTime - now;
			if (ss >= 0) {
				PollingUtils.startPollingService(context, false, mReminder,
						trrigeAtTime, Alarmreceiver.class);
			}
		}

	}

	public static void triggerAllAlarm(Context context, List<Reminder> list) {

		for (Reminder mReminder : list) {
			if (mReminder.getWeek().startsWith("每")
					&& "1".equals(mReminder.getToggle())) {
				if ("每周一".equals(mReminder.getWeek())) {

					PollingUtils.startPollingService(
							context,
							true,
							mReminder,
							AlarmUtil.getNextAlarmTime(2, "1",
									mReminder.getTime()), Alarmreceiver.class);
				} else if ("每周二".equals(mReminder.getWeek())) {
					PollingUtils.startPollingService(
							context,
							true,
							mReminder,
							AlarmUtil.getNextAlarmTime(2, "2",
									mReminder.getTime()), Alarmreceiver.class);
				} else if ("每周三".equals(mReminder.getWeek())) {
					PollingUtils.startPollingService(
							context,
							true,
							mReminder,
							AlarmUtil.getNextAlarmTime(2, "3",
									mReminder.getTime()), Alarmreceiver.class);
				} else if ("每周四".equals(mReminder.getWeek())) {
					PollingUtils.startPollingService(
							context,
							true,
							mReminder,
							AlarmUtil.getNextAlarmTime(2, "4",
									mReminder.getTime()), Alarmreceiver.class);
				} else if ("每周五".equals(mReminder.getWeek())) {
					PollingUtils.startPollingService(
							context,
							true,
							mReminder,
							AlarmUtil.getNextAlarmTime(2, "5",
									mReminder.getTime()), Alarmreceiver.class);
				} else if ("每周六".equals(mReminder.getWeek())) {
					PollingUtils.startPollingService(
							context,
							true,
							mReminder,
							AlarmUtil.getNextAlarmTime(2, "6",
									mReminder.getTime()), Alarmreceiver.class);
				} else if ("每周日".equals(mReminder.getWeek())) {
					PollingUtils.startPollingService(
							context,
							true,
							mReminder,
							AlarmUtil.getNextAlarmTime(2, "0",
									mReminder.getTime()), Alarmreceiver.class);
				}

			} else if (mReminder.getWeek().startsWith("永")
					&& "1".equals(mReminder.getToggle())) {
				final long now = System.currentTimeMillis();
				final long trrigeAtTime = AlarmUtil.getAlarmTime(mReminder
						.getTime());
				final long ss = trrigeAtTime - now;
				if (ss >= 0) {
					PollingUtils.startPollingService(context, false, mReminder,
							trrigeAtTime, Alarmreceiver.class);
				}
			}
		}

	}

}
