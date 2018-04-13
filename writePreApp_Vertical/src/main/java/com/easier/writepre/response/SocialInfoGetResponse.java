package com.easier.writepre.response;

import com.easier.writepre.entity.SocialPropEntity;

/**
 * 获取社交版参数配置项
 * 
 * @author kai.zhong
 * 
 */
public class SocialInfoGetResponse extends BaseResponse {

	private SocialPropEntity repBody;

	public void setRepBody(SocialPropEntity repBody) {
		this.repBody = repBody;
	}

	public SocialPropEntity getRepBody() {
		return repBody;
	}

}