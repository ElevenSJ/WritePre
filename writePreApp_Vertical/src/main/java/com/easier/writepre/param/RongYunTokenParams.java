package com.easier.writepre.param;

/**
 * 获取融云Token请求参数
 * 
 * 
 */
public class RongYunTokenParams extends BaseBodyParams {

	public RongYunTokenParams() {
	}

	@Override
	public String getProNo() {
		return "sj_chat_token";
	}

	@Override
	public String getUrl() {
		return "app";
	}

}
