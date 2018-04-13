package com.easier.writepre.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlarmManager;

public class AlarmUtil {

	/**
	 * 闹钟三种设置模式（dateMode）： 1、DATE_MODE_FIX：指定日期，如20120301 ,
	 * 参数dateValue格式：2012-03-01 2、DATE_MODE_WEEK：按星期提醒，如星期一、星期三 ,
	 * 参数dateValue格式：1,3 3、DATE_MODE_MONTH：按月提醒，如3月2、3号，4月2、3号,
	 * 参数dateValue格式：3,4|2,3
	 * 
	 * startTime:为当天开始时间，如上午9点, 参数格式为09:00
	 */
	@SuppressLint("SimpleDateFormat")
	public static long getNextAlarmTime(int dateMode, String dateValue,
			String startTime) {
		final SimpleDateFormat fmt = new SimpleDateFormat();
		final Calendar c = Calendar.getInstance();
		final long now = System.currentTimeMillis();

		// 设置开始时间
		try {
			// 按固定日期提醒
			if (1 == dateMode) {
				fmt.applyPattern("yyyy-MM-dd");
				Date d = fmt.parse(dateValue);
				c.setTimeInMillis(d.getTime());
			}

			fmt.applyPattern("HH:mm");
			Date d = fmt.parse(startTime);
			c.set(Calendar.HOUR_OF_DAY, d.getHours());
			c.set(Calendar.MINUTE, d.getMinutes());
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		long nextTime = 0;
		if (1 == dateMode) { // 按指定日期
			nextTime = c.getTimeInMillis();
			// 指定日期已过
			if (now >= nextTime)
				nextTime = 0;
		} else if (2 == dateMode) { // 按星期提醒
			final long[] checkedWeeks = parseDateWeeks(dateValue);
			if (null != checkedWeeks) {
				for (long week : checkedWeeks) {
					c.set(Calendar.DAY_OF_WEEK, (int) (week + 1));

					long triggerAtTime = c.getTimeInMillis();
					if (triggerAtTime <= now) { // 下周
						triggerAtTime += AlarmManager.INTERVAL_DAY * 7;
					}
					// 保存最近闹钟时间
					if (0 == nextTime) {
						// nextTime = triggerAtTime - now;
						nextTime = triggerAtTime;
					} else {
						// nextTime = Math.min(triggerAtTime, nextTime);
						nextTime = Math.min(triggerAtTime, nextTime);
					}
				}
			}
		} else if (3 == dateMode) { // 按月提醒
			final long[][] items = parseDateMonthsAndDays(dateValue);
			final long[] checkedMonths = items[0];
			final long[] checkedDays = items[1];

			if (null != checkedDays && null != checkedMonths) {
				boolean isAdd = false;
				for (long month : checkedMonths) {
					c.set(Calendar.MONTH, (int) (month - 1));
					for (long day : checkedDays) {
						c.set(Calendar.DAY_OF_MONTH, (int) day);

						long triggerAtTime = c.getTimeInMillis();
						if (triggerAtTime <= now) { // 下一年
							c.add(Calendar.YEAR, 1);
							triggerAtTime = c.getTimeInMillis();
							isAdd = true;
						} else {
							isAdd = false;
						}
						if (isAdd) {
							c.add(Calendar.YEAR, -1);
						}
						// 保存最近闹钟时间 www.2cto.com
						if (0 == nextTime) {
							nextTime = triggerAtTime;
						} else {
							nextTime = Math.min(triggerAtTime, nextTime);
						}
					}
				}
			}
		}
		return nextTime;
	}

	@SuppressLint("SimpleDateFormat")
	public static long getAlarmTime(String startTime) {
		final SimpleDateFormat fmt = new SimpleDateFormat();
		final Calendar c = Calendar.getInstance();
		// final long now = System.currentTimeMillis();

		fmt.applyPattern("HH:mm");
		Date d;
		try {
			d = fmt.parse(startTime);
			c.set(Calendar.HOUR_OF_DAY, d.getHours());
			c.set(Calendar.MINUTE, d.getMinutes());
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		long triggerAtTime = c.getTimeInMillis();

		return triggerAtTime;

	}

	public static long[] parseDateWeeks(String value) {
		long[] weeks = null;
		try {
			final String[] items = value.split(",");
			weeks = new long[items.length];
			int i = 0;
			for (String s : items) {
				weeks[i++] = Long.valueOf(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return weeks;
	}

	public static long[][] parseDateMonthsAndDays(String value) {
		long[][] values = new long[2][];
		try {
			final String[] items = value.split("\\|");
			final String[] monthStrs = items[0].split(",");
			final String[] dayStrs = items[1].split(",");
			values[0] = new long[monthStrs.length];
			values[1] = new long[dayStrs.length];

			int i = 0;
			for (String s : monthStrs) {
				values[0][i++] = Long.valueOf(s);
			}
			i = 0;
			for (String s : dayStrs) {
				values[1][i++] = Long.valueOf(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}

}
