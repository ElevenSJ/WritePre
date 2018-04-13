package com.easier.writepre.param;

public class CirclePostDelParams extends BaseBodyParams {
	
	private String circle_id;
	private String post_id ;

	public CirclePostDelParams(String circle_id, String post_id) {
		this.circle_id = circle_id;
		this.post_id = post_id;
	}

	@Override
	public String getProNo() {
		return "sj_circle_post_del";
	}
	
	@Override
	public String getUrl() {
		return "app";
	}

}
