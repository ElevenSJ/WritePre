package com.easier.writepre.param;

/**
 * 获取融云 相互关注的好友列表 请求参数
 * 
 * 
 */
public class RongYunJointAttentionListParams extends BaseBodyParams {

	public RongYunJointAttentionListParams() {
	}

	@Override
	public String getProNo() {
		return "sj_chat_friends";
	}

	@Override
	public String getUrl() {
		return "app";
	}

}
