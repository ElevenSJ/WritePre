package com.easier.writepre.param;

/**
 * 广场看帖数
 * 
 * 
 */
public class SquareReadParams extends BaseBodyParams {

	private String post_id;

	public SquareReadParams(String post_id) {
		this.post_id = post_id;
	}

	@Override
	public String getProNo() {
		return "sj_square_post_view_num_add";
	}

	@Override
	public String getUrl() {
		return "login";
	}

}
