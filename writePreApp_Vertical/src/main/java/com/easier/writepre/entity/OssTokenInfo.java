package com.easier.writepre.entity;

public class OssTokenInfo {
	private String expiration="";   // UTC 时间字符串
	
	private String access_key_id="";  // 客户端oss访问id
	
	private String security_token="";  // 客户端oss访问密钥
	
	private String access_key_Secret=""; // 客户端oss访问token

	public String getExpiration() {
		return expiration;
	}

	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}

	public String getAccess_key_id() {
		return access_key_id;
	}

	public void setAccess_key_id(String access_key_id) {
		this.access_key_id = access_key_id;
	}

	public String getSecurity_token() {
		return security_token;
	}

	public void setSecurity_token(String security_token) {
		this.security_token = security_token;
	}

	public String getAccess_key_Secret() {
		return access_key_Secret;
	}

	public void setAccess_key_Secret(String access_key_Secret) {
		this.access_key_Secret = access_key_Secret;
	}
	
}
