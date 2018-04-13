package com.easier.writepre.param;

public class VerifyForgetParams extends BaseBodyParams {

	private String tel;
	private String code;
	private String new_pwd;
	
	public VerifyForgetParams(String tel,String code,String new_pwd) {
		this.tel = tel;
		this.code=code;
		this.new_pwd = new_pwd;
	}
	
	@Override
	public String getProNo() {
		return "app_pwd_bycode";
	}
	
	@Override
	public String getUrl() {
		return "/writePieWeb/login";
	}

}
