package com.easier.writepre.response;

/**
 * 
 * 点赞返回
 * @author kai.zhong
 * 
 */
public class NoticeFeedbackResponse extends BaseResponse {

	private Repbody repBody;

	public void setRepBody(Repbody repBody) {
		this.repBody = repBody;
	}

	public Repbody getRepBody() {
		return repBody;
	}

	public class Repbody {
		
	}
}