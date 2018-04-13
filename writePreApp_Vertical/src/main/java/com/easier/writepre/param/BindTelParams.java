package com.easier.writepre.param;

public class BindTelParams extends BaseBodyParams {
	
	private String tel;
	private String code;
	
	
	public BindTelParams(String tel) {
		super();
		this.tel = tel;
	}


	public BindTelParams(String tel,String code) {
		super();
		this.tel = tel;
		this.code = code;
	}


	@Override
	public String getProNo() {
		// TODO Auto-generated method stub
		return "app_tel_bind";
	}
	
	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		return "/writePieWeb/app";
	}

}
