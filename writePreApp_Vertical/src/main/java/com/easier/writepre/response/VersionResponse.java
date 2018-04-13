package com.easier.writepre.response;

import com.easier.writepre.entity.VersionInfo;

public class VersionResponse extends BaseResponse {

	private VersionInfo repBody;

	public VersionInfo getRepBody() {
		return repBody;
	}

	public void setRepBody(VersionInfo repBody) {
		this.repBody = repBody;
	}

}
