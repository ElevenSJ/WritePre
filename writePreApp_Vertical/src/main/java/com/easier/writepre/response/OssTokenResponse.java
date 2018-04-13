package com.easier.writepre.response;

import com.easier.writepre.entity.OssTokenInfo;

/**
 * oss sts token 获取
 * 
 * @author kai.zhong
 * 
 */
public class OssTokenResponse extends BaseResponse {

	private OssTokenInfo repBody;

	public void setRepBody(OssTokenInfo repBody) {
		this.repBody = repBody;
	}

	public OssTokenInfo getRepBody() {
		return repBody;
	}
}