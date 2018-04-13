package com.easier.writepre.param;

/**
 * 融云好友关系验证请求参数
 * 
 * 
 */
public class RongYunCheckFriendParams extends BaseBodyParams {
	private String user_id;

	public RongYunCheckFriendParams(String user_id) {
		this.user_id = user_id;
	}

	@Override
	public String getProNo() {
		return "sj_chat_rel_check";
	}

	@Override
	public String getUrl() {
		return "app";
	}

}
