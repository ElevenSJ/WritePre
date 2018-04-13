package com.easier.writepre.response;

import com.easier.writepre.entity.ActiveInfo;

import java.util.List;

public class ActiveDetailResponse extends BaseResponse {

	private ActiveInfo repBody;

	public ActiveInfo getRepBody() {
		return repBody;
	}

	public void setRepBody(ActiveInfo repBody) {
		this.repBody = repBody;
	}


}