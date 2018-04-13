package com.easier.writepre.param;

/**
 * 申请加入圈子
 * 
 * 
 */
public class CircleJoinParams extends BaseBodyParams {

	private String circle_id;

	public CircleJoinParams(String circle_id) {
		this.circle_id = circle_id;
	}

	@Override
	public String getProNo() {
		return "sj_circle_user_req";
	}

	@Override
	public String getUrl() {
		return  "app";
	}

}
