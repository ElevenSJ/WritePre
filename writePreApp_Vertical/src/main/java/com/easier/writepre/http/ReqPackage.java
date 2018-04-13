package com.easier.writepre.http;

import com.easier.writepre.app.WritePreApp;
import com.easier.writepre.param.BaseBodyParams;
import com.easier.writepre.utils.DeviceUtils;
import com.easier.writepre.utils.NetWorkUtils;
import com.google.gson.Gson;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.WindowManager;

public class ReqPackage {

	private BaseParams base;

	private BaseBodyParams body;

	public ReqPackage(BaseBodyParams body) {
		this.body = body;
		this.base = new BaseParams(body.getProNo());
	}

	public String packetParam() {
		return new Gson().toJson(this);
	}

	public class BaseParams {

		private String reqTime;

		private String proNo;
		private Ext ext;

		public BaseParams(String proNo) {
			this.proNo = proNo;
			this.reqTime = DateFormat.format("yyyyMMddHHmmss", System.currentTimeMillis()).toString();
			ext = new Ext();
		}
	}

	public class Ext {
		private String dev_type;
		private String dev_v;
		private String dev_id;
		private String sys_v;
		private String version;
		private String net;
		private String provider;
		private String screen;
		private String city;
		private String channel;

		public Ext() {
			this.dev_type = "1";

			TelephonyManager tm = (TelephonyManager) WritePreApp.getApp().getApplication()
					.getSystemService(Context.TELEPHONY_SERVICE);
			this.dev_v = Build.MODEL;
			this.dev_id = DeviceUtils.getDeviceId();
			this.sys_v = Build.VERSION.RELEASE;
			this.net = NetWorkUtils.GetNetworkType();
			this.provider = tm.getSimOperator();
			this.city = "";

			// 方法1 Android获得屏幕的宽和高
			WindowManager windowManager = (WindowManager) WritePreApp.getApp().getApplication().getSystemService(Context.WINDOW_SERVICE);
			Display display = windowManager.getDefaultDisplay();
			int screenWidth = display.getWidth();
			int screenHeight = display.getHeight();
			this.screen = screenWidth + "*" + screenHeight;
			PackageManager manager = WritePreApp.getApp().getApplication().getPackageManager();
			try {
				PackageInfo info;
				info = manager.getPackageInfo(WritePreApp.getApp().getApplication().getPackageName(), 0);
				this.version = info.versionName;

				ApplicationInfo ai = manager.getApplicationInfo(WritePreApp.getApp().getApplication().getPackageName(),
						PackageManager.GET_META_DATA);
				Object value = ai.metaData.get("UMENG_CHANNEL");
				this.channel = value.toString();
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}

		}

		public String getSys_v() {
			return sys_v;
		}

		public void setSys_v(String sys_v) {
			this.sys_v = sys_v;
		}

		public String getDev_type() {
			return dev_type;
		}

		public void setDev_type(String dev_type) {
			this.dev_type = dev_type;
		}

		public String getDev_v() {
			return dev_v;
		}

		public void setDev_v(String dev_v) {
			this.dev_v = dev_v;
		}

		public String getDev_id() {
			return dev_id;
		}

		public void setDev_id(String dev_id) {
			this.dev_id = dev_id;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getNet() {
			return net;
		}

		public void setNet(String net) {
			this.net = net;
		}

		public String getProvider() {
			return provider;
		}

		public void setProvider(String provider) {
			this.provider = provider;
		}

		public String getScreen() {
			return screen;
		}

		public void setScreen(String screen) {
			this.screen = screen;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getChannel() {
			return channel;
		}

		public void setChannel(String channel) {
			this.channel = channel;
		}

	}
}
