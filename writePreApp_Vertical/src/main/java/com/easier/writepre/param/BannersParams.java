package com.easier.writepre.param;

public class BannersParams extends BaseBodyParams {

	private String loc;

	public BannersParams(String loc) {
		this.loc = loc;
	}

	@Override
	public String getProNo() {
		return "sj_ad_banners";
	}

	@Override
	public String getUrl() {
		return "login";
	}

}
