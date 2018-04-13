package com.easier.writepre.param;

public class InstallCountParams extends BaseBodyParams {

	private final String device;

	@Override
	public String getProNo() {
		return "3001";
	}

	public InstallCountParams() {
		this.device = "1";
	}

	@Override
	public String getUrl() {
		return "/writePieWeb/login";
	}

}
