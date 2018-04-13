package com.easier.writepre.response;

/**
 * 
 * 优酷access_token返回
 * 
 * @author kai.zhong
 * 
 */
public class YouKuTokenResponse extends BaseResponse {

	private Repbody repBody;

	public void setRepBody(Repbody repBody) {
		this.repBody = repBody;
	}

	public Repbody getRepBody() {
		return repBody;
	}

	public class Repbody {

		private String token;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}
	}
}