package com.easier.writepre.param;

/**
 * 获取个人消息
 */
public class MessageUserParams extends BaseBodyParams {

	private String last_id;

	public MessageUserParams(String last_id) {
		this.last_id = last_id;
	}

	@Override
	public String getProNo() {
		return "sj_msg_usr";
	}

	@Override
	public String getUrl() {
		return "app";
	}

}
