package com.easier.writepre.param;

import com.easier.writepre.utils.DeviceUtils;
import com.easier.writepre.utils.SPUtils;

public class LoginParams extends BaseBodyParams {

	public enum LoginType {
		NORMAL("4"), WB("1"), WX("2"), QQ("3");

		private String value;

		private LoginType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	private String device_id;

	private String email;

	private String pwd;

	private String last_msg_id = "";

	private String third_id;

	private String login_name;

	private String access_token;

	private String type;

	private String qq_head;

	private String qq_name;

	public LoginParams(String email, String pwd) {
		this.login_name = email;
		this.pwd = pwd;
		this.device_id = DeviceUtils.getDeviceId();
		this.type = LoginType.NORMAL.getValue();
		this.last_msg_id = "";
		this.third_id = "";
		this.access_token = "";
		SPUtils.instance().put(SPUtils.LOGIN_NAME, email);
	}

	/**
	 * 特殊处理qq 获取qq头像地址和昵称至服务端
	 */
	public LoginParams(LoginType type, String third_id, String access_token,
			String qq_head, String qq_name) {
		this.device_id = DeviceUtils.getDeviceId();
		this.type = type.value;
		this.third_id = third_id;
		this.access_token = access_token;
		this.qq_head = qq_head;
		this.qq_name = qq_name;
//		SPUtils.instance().put(SPUtils.LOGIN_NAME, third_id);
	}

	public LoginParams(LoginType type, String third_id, String access_token) {
		// this.email = Constant.EMPTY;
		// this.pwd = Constant.EMPTY;
		this.device_id = DeviceUtils.getDeviceId();
		this.type = type.value;
		this.third_id = third_id;
		this.access_token = access_token;
//		SPUtils.instance().put(SPUtils.LOGIN_NAME, third_id);
	}

	@Override
	public String getUrl() {
		return "/writePieWeb/login";
	}

	@Override
	public String getProNo() {
		return "1001";
	}

	public String getLoginName() {
		return login_name;
	}

	public String getPwd() {
		return pwd;
	}

	public String getType() {
		return type;
	}

	public String getThird_id() {
		return third_id;
	}

	public void setThird_id(String third_id) {
		this.third_id = third_id;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

}
