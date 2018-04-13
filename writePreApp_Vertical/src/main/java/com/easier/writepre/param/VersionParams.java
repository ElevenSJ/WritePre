package com.easier.writepre.param;

import com.easier.writepre.app.WritePreApp;

public class VersionParams extends BaseBodyParams {

	private String type;
	
	private String role;
	
	public VersionParams() {
		this.type = "2";
		this.role = WritePreApp.channelId;
	}
	
	@Override
	public String getProNo() {
		return "3002";
	}

	@Override
	public String getUrl() {
		return "/writePieWeb/login";
	}
}
