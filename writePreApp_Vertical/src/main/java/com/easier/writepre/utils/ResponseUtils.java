package com.easier.writepre.utils;

import android.text.TextUtils;

public class ResponseUtils {

	public static String findToken(String cookies) {
		if (TextUtils.isEmpty(cookies)) {
			return null;
		}
		String[] strs = cookies.split(" ");
		StringBuffer sb = new StringBuffer();
		for (String str : strs) {
			if (str.startsWith("JSESSIONID")) {
				sb.append(str);
			} else if (str.startsWith("HttpOnlyuserToken")) {
				sb.append(str.replace("HttpOnlyuserToken", "userToken"));
			}
		}
		return sb.toString();
	}
}
