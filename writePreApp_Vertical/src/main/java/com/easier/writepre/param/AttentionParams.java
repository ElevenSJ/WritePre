package com.easier.writepre.param;

/**
 * 关注请求参数
 * 
 * @author zhoulu
 * 
 */
public class AttentionParams extends BaseBodyParams {

	private String user_id;

	public AttentionParams(String user_id) {
		this.user_id = user_id;
	}

	@Override
	public String getProNo() {
		return "sj_care_user_add";
	}

	@Override
	public String getUrl() {
		return "app";
	}

}
