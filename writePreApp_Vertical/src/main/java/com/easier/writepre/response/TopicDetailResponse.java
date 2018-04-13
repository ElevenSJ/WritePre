package com.easier.writepre.response;

import com.easier.writepre.entity.ContentInfo;

public class TopicDetailResponse extends BaseResponse {
	
	private ContentInfo repBody;
	
	public ContentInfo getRepBody() {
		return repBody;
	}

	public void setRepBody(ContentInfo repBody) {
		this.repBody = repBody;
	}
}
