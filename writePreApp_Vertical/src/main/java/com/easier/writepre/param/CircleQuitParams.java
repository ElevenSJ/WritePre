package com.easier.writepre.param;

/**
 * 退出圈子
 * 
 * 
 */
public class CircleQuitParams extends BaseBodyParams {

	private String circle_id;

	
	public CircleQuitParams(String circle_id) {
		this.circle_id = circle_id;
	}

	@Override
	public String getProNo() {
		return "sj_circle_user_quit";
	}

	@Override
	public String getUrl() {
		return  "app";
	}

}
