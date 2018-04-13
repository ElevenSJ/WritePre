package com.easier.writepre.param;

/**
 * 他人圈子参数
 * 
 * @author zhaomaohan
 * 
 */
public class UserCircleParams extends BaseBodyParams {

	private String user_id;

	public UserCircleParams(String user_id) {
		this.user_id = user_id;
	}

	@Override
	public String getProNo() {
		return "sj_circle_others";
	}

	@Override
	public String getUrl() {
		return "login";
	}

}
