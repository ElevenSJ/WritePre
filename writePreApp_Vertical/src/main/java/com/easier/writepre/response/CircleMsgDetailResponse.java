package com.easier.writepre.response;

import com.easier.writepre.entity.CircleMsgInfo;

public class CircleMsgDetailResponse extends BaseResponse {
	
	private CircleMsgInfo repBody;
	
	public CircleMsgInfo getRepBody() {
		return repBody;
	}

	public void setRepBody(CircleMsgInfo repBody) {
		this.repBody = repBody;
	}
}
