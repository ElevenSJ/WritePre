package com.easier.writepre.param;

/**
 * 
 * 个人资料
 * 
 * @author kai.zhong
 * 
 */
public class UserInfoParams extends BaseBodyParams {

	private String user_id;

	public UserInfoParams(String user_id) {
		this.user_id = user_id;
	}

	@Override
	public String getProNo() {
		return "sj_user_info";
	}

	@Override
	public String getUrl() {
		//return "app";
		return "login";
	}

}
