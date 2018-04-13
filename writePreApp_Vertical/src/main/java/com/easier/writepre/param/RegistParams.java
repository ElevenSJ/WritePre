package com.easier.writepre.param;

public class RegistParams extends BaseBodyParams {

	private String tel;
	
	
	public RegistParams(String tel) {
		this.tel = tel;
	}
	
	@Override
	public String getProNo() {
		return "app_tel_code";
	}
	
	@Override
	public String getUrl() {
		return "/writePieWeb/login";
	}

}
