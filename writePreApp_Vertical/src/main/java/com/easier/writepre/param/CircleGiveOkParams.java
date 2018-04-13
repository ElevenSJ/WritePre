package com.easier.writepre.param;

/**
 * 
 * 圈点赞
 * 
 */
public class CircleGiveOkParams extends BaseBodyParams {
	
	private String post_id;
	private String circle_id;
	
	public CircleGiveOkParams(String circle_id,String post_id) {
		this.post_id = post_id;
		this.circle_id = circle_id;
	}
	
	@Override
	public String getProNo() {
		return "sj_circle_post_ok";
	}

	@Override
	public String getUrl() {
		return "app";
	}

}
