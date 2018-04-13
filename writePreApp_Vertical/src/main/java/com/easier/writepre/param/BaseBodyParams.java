package com.easier.writepre.param;

public abstract class BaseBodyParams {
	
	/**
	 * 请求的
	 * @return
	 */
	public String getUrl() {
		return "/writePieWeb/app";
	}
	
	public abstract String getProNo();
}
