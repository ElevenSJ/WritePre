package com.easier.writepre.response;

import com.easier.writepre.entity.LoginEntity;

public class LoginResponse extends BaseResponse {

	private LoginEntity repBody;

	public LoginEntity getRepBody() {
		return repBody;
	}

	public void setRepBody(LoginEntity repBody) {
		this.repBody = repBody;
	}

}
