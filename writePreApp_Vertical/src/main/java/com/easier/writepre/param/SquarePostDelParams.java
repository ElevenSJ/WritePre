package com.easier.writepre.param;

public class SquarePostDelParams extends BaseBodyParams {
	
	private String post_id ;
	
	public SquarePostDelParams(String post_id) {
		this.post_id = post_id;
	}
	

	@Override
	public String getProNo() {
		return "sj_square_post_del";
	}
	
	@Override
	public String getUrl() {
		return "app";
	}

}
