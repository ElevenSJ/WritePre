package com.easier.writepre.param;

public class AppPathParams extends BaseBodyParams {
	private String version="";

	public AppPathParams(String version){
		this.version = version;
	}
	@Override
	public String getProNo() {
		return "app_android_pkg_query";
	}

	@Override
	public String getUrl() {
		return "/writePieWeb/login";
	}

}
