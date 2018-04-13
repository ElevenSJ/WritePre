package com.easier.writepre.param;

/**
 * 
 * 点赞
 * @author kai.zhong
 * 
 */
public class GiveOkParams extends BaseBodyParams {
	
	private String post_id;
	
	public GiveOkParams(String post_id) {
		this.post_id = post_id;
	}
	
	@Override
	public String getProNo() {
		return "sj_square_post_ok";
	}

	@Override
	public String getUrl() {
		return "app";
	}

}
