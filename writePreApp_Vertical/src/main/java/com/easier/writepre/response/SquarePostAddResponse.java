package com.easier.writepre.response;

/**
 * 
 * 廣場發帖
 * @author kai.zhong
 * 
 */
public class SquarePostAddResponse extends BaseResponse {

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