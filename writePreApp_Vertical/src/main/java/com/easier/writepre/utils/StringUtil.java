package com.easier.writepre.utils;

import android.text.TextUtils;

import com.easier.writepre.http.Constant;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @author kai.zhong
 * 
 */
public class StringUtil {

	/**
	 * 截取字符串长度，替换成...
	 * 
	 * @param str
	 *            传入的字符串
	 * @param length
	 *            从多少长度开始截取
	 * @return 返回截取好的字符串
	 */
	public static String FormatStrLength(String str, int length) {
		if (str.length() > length) {
			return str.substring(0, length) + "...";
		} else
			return str;
	}

	/**
	 * 图片所属服务地址
	 * 
	 * @return
	 */
	public static String getImgeUrl(String url) {
		if (TextUtils.isEmpty(url)) {
			return null;
		}
		String[] urlStrings = url.split("/");
		if (urlStrings.length <= 0) {
			return url;
		}
		if (SPUtils.instance().getSocialPropEntity().getImgsrv_pre() != null) {
			if (url.startsWith("group")) {
				return SPUtils.instance().getSocialPropEntity().getImgsrv_pre().get("group") + url;
			} else
				return SPUtils.instance().getSocialPropEntity().getImgsrv_pre().get(urlStrings[0]) == null ? ""
						: SPUtils.instance().getSocialPropEntity().getImgsrv_pre().get(urlStrings[0]) + url;
		} else {
			return url;
		}
	}
	/**
	 * 语音所属服务地址
	 *
	 * @return
	 */
	public static String getVoiceUrl(String url) {
		if (TextUtils.isEmpty(url)) {
			return null;
		}
		String[] urlStrings = url.split("/");
		if (urlStrings.length <= 0) {
			return url;
		}
		if (SPUtils.instance().getSocialPropEntity().getImgsrv_pre() != null) {
			if (url.startsWith("group")) {
				return SPUtils.instance().getSocialPropEntity().getImgsrv_pre().get("group") + url;
			} else
				return SPUtils.instance().getSocialPropEntity().getImgsrv_pre().get(urlStrings[0]) == null ? ""
						: SPUtils.instance().getSocialPropEntity().getImgsrv_pre().get(urlStrings[0]) + url;
		} else {
			return url;
		}
	}
	/**
	 * 语音所属服务地址
	 *
	 * @return
	 */
	public static String getVideoUrl(String url) {
		if (TextUtils.isEmpty(url)) {
			return null;
		}
		String[] urlStrings = url.split("/");
		if (urlStrings.length <= 0) {
			return url;
		}
		if (SPUtils.instance().getSocialPropEntity().getImgsrv_pre() != null) {
			if (url.startsWith("group")) {
				return SPUtils.instance().getSocialPropEntity().getImgsrv_pre().get("group") + url;
			} else
				return SPUtils.instance().getSocialPropEntity().getImgsrv_pre().get(urlStrings[0]) == null ? ""
						: SPUtils.instance().getSocialPropEntity().getImgsrv_pre().get(urlStrings[0]) + url;
		} else {
			return url;
		}
	}
	/**
	 * 个人头像
	 * 
	 * @param context
	 * @param path
	 * @return
	 */
	public static String getHeadUrl(String url) {
		if (TextUtils.isEmpty(url)) {
			return null;
		}
		String[] urlStrings = url.split("/");
		if (SPUtils.instance().getSocialPropEntity().getImgsrv_pre() != null) {
			if (url.startsWith("group")) {
				return SPUtils.instance().getSocialPropEntity().getImgsrv_pre().get("group") + url;
			} else {
				return SPUtils.instance().getSocialPropEntity().getImgsrv_pre().get(urlStrings[0]) == null ? ""
						: SPUtils.instance().getSocialPropEntity().getImgsrv_pre().get(urlStrings[0]) + url
								+ Constant.HEAD_IMAGE_SUFFIX;
			}
		}else{
			return url;
		}
	}
	public static String getMD5(String info)
	{
		try
		{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(info.getBytes("UTF-8"));
			byte[] encryption = md5.digest();

			StringBuffer strBuf = new StringBuffer();
			for (int i = 0; i < encryption.length; i++)
			{
				if (Integer.toHexString(0xff & encryption[i]).length() == 1)
				{
					strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
				}
				else
				{
					strBuf.append(Integer.toHexString(0xff & encryption[i]));
				}
			}

			return strBuf.toString();
		}
		catch (NoSuchAlgorithmException e)
		{
			return "";
		}
		catch (UnsupportedEncodingException e)
		{
			return "";
		}
	}
}
