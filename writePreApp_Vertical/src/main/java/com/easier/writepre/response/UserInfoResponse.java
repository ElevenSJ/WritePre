package com.easier.writepre.response;

import com.easier.writepre.entity.UserInfo;

/**
 * 个人资料
 * 
 * @author kai.zhong
 * 
 */
public class UserInfoResponse extends BaseResponse {

	private UserInfo repBody;

	public void setRepBody(UserInfo repBody) {
		this.repBody = repBody;
	}

	public UserInfo getRepBody() {
		return repBody;
	}

}