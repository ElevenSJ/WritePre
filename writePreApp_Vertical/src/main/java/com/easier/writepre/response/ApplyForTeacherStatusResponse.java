package com.easier.writepre.response;

import com.easier.writepre.entity.ApplyForTeacherStatusInfo;

public class ApplyForTeacherStatusResponse extends BaseResponse {

	private ApplyForTeacherStatusInfo repBody;

	public ApplyForTeacherStatusInfo getRepBody() {
		return repBody;
	}

	public void setRepBody(ApplyForTeacherStatusInfo repBody) {
		this.repBody = repBody;
	}

}