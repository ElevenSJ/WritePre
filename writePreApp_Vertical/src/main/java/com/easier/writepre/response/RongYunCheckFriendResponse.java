package com.easier.writepre.response;

import com.easier.writepre.entity.RongYunCheckInfo;

/**
 * 检查好友关系返回结构体
 * 
 * @author zhoulu
 * 
 */
public class RongYunCheckFriendResponse extends BaseResponse {
	private RongYunCheckInfo repBody;

	public RongYunCheckInfo getRepBody() {
		return repBody;
	}

	public void setRepBody(RongYunCheckInfo repBody) {
		this.repBody = repBody;
	}
}