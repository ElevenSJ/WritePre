package com.easier.writepre.param;

public class MiLoginParams extends BaseBodyParams {

	private String open_id;

	private String token;

	public MiLoginParams(String token, String open_id) {
		this.token = token;
		this.open_id = open_id;
	}

	@Override
	public String getUrl() {
		return "/writePieWeb/login";
	}

	@Override
	public String getProNo() {
		return "app_doc_login_xiaomi";
	}


}
