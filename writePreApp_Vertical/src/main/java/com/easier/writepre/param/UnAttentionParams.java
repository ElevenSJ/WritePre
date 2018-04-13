package com.easier.writepre.param;

/**
 * 取消关注请求参数
 * 
 * @author zhoulu
 * 
 */
public class UnAttentionParams extends BaseBodyParams {

	private String user_id;

	public UnAttentionParams(String user_id) {
		this.user_id = user_id;
	}

	@Override
	public String getProNo() {
		return "sj_care_user_cancel";
	}

	@Override
	public String getUrl() {
		return "app";
	}

}
