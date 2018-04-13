package com.easier.writepre.param;

/**
 * 
 * V展请求
 * 
 * @author zhaomaohan
 * 
 */
public class VShowUserParams extends BaseBodyParams {

	private String start;

	private String count;

	public VShowUserParams(String start, String count) {
		this.start = start;
		this.count = count;
	}

	@Override
	public String getProNo() {
		return "sj_vshow_user_query";
	}

	@Override
	public String getUrl() {
		//return "app";
		return "login";
	}

}
