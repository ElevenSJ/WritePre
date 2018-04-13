package com.easier.writepre.response;

import com.easier.writepre.entity.CircleDetail;
/**
 * 圈子详情
 * @author dqt
 *
 */
public class CircleDetailResponse extends BaseResponse{

	private CircleDetail repBody;

	public CircleDetail getRepBody() {
		return repBody;
	}

	public void setRepBody(CircleDetail repBody) {
		this.repBody = repBody;
	}

}