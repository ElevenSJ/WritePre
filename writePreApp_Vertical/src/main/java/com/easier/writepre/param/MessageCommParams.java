package com.easier.writepre.param;

/**
 * 获取系统消息
 */
public class MessageCommParams extends BaseBodyParams {

	private String last_id;

	public MessageCommParams(String last_id) {
		this.last_id = last_id;
	}

	@Override
	public String getProNo() {
		return "sj_msg_comm";
	}

	@Override
	public String getUrl() {
		return "login";
	}

}
