package com.easier.writepre.param;

/**
 * 圈子看帖数
 * 
 * 
 */
public class CircleReadParams extends BaseBodyParams {

	private String circle_id;
	private String post_id;

	public CircleReadParams(String circle_id, String post_id) {
		this.post_id = post_id;
		this.circle_id = circle_id;
	}

	@Override
	public String getProNo() {
		return "sj_circle_post_view_num_add";
	}

	@Override
	public String getUrl() {
		return "login";
	}

}
