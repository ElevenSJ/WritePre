package com.easier.writepre.param;

/**
 * 
 * 优酷access_token查询
 * 
 */
public class YouKuTokenParams extends BaseBodyParams {

	private String device;

	public YouKuTokenParams(String device) {
		this.device = device;
	}

	@Override
	public String getProNo() {
		return "sj_yk_token";
	}

	@Override
	public String getUrl() {
		return "app";
	}

}
