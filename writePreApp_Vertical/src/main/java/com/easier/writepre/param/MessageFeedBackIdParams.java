package com.easier.writepre.param;

import com.easier.writepre.utils.SPUtils;

/**
 * 消息id回值
 */
public class MessageFeedBackIdParams extends BaseBodyParams {

	private String last_msg_usr_id;
	private String last_msg_comm_id;

	public MessageFeedBackIdParams(String last_msg_usr_id,String last_msg_comm_id) {
		this.last_msg_usr_id = last_msg_usr_id;
		this.last_msg_comm_id = last_msg_comm_id;
	}

	@Override
	public String getProNo() {
		return "sj_msg_feedback";
	}

	@Override
	public String getUrl() {
		return (boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false) ? "app" : "login";
	}

}
