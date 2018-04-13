package com.easier.writepre.utils;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;

public class ShareContentCustomize implements ShareContentCustomizeCallback {

	private String url, txt;

	public ShareContentCustomize(String url, String txt) {
		this.url = url;
		this.txt = txt;
	}

	@Override
	public void onShare(Platform platform, ShareParams paramsToShare) {
		String from;
		if (QQ.NAME.equals(platform.getName())) {
			from = "&from=qq";
			// paramsToShare.setText(url + from);
			paramsToShare.setTitleUrl(url + from);
		} else if (SinaWeibo.NAME.equals(platform.getName())) {
			from = "&from=sina";
			paramsToShare.setText(txt + " " + url + from);
			// paramsToShare.setTitleUrl(url + from);
		} else {
			from = "&from=weixin";
			// paramsToShare.setText(url + from);
			paramsToShare.setTitleUrl(url + from);
		}
	}
}
