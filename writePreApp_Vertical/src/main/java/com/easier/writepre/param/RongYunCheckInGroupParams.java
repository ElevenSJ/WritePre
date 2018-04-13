package com.easier.writepre.param;

/**
 * 融云是否是群内成员关系验证请求参数
 * 
 * 
 */
public class RongYunCheckInGroupParams extends BaseBodyParams {
	private String circle_id;

	public RongYunCheckInGroupParams(String circle_id) {
		this.circle_id = circle_id;
	}

	@Override
	public String getProNo() {
		return "sj_circle_chat_rel_check";
	}

	@Override
	public String getUrl() {
		return "app";
	}

}
