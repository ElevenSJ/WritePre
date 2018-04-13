package com.easier.writepre.response;

import java.util.List;

import com.easier.writepre.entity.AttentionInfo;
import com.easier.writepre.entity.RongYunTokenInfo;
import com.easier.writepre.response.AttentionListResponse.Repbody;

/**
 * 融云Token返回结构体
 * 
 * @author zhoulu
 * 
 */
public class RongYunTokenResponse extends BaseResponse {
	private RongYunTokenInfo repBody;

	public RongYunTokenInfo getRepBody() {
		return repBody;
	}

	public void setRepBody(RongYunTokenInfo repBody) {
		this.repBody = repBody;
	}

}