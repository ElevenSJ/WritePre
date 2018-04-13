package com.easier.writepre.param;

public class TopicCommentsDeleteParams extends BaseBodyParams {
	
	private String remark_id;
	
	public TopicCommentsDeleteParams(String remark_id) {
		this.remark_id = remark_id;
	}

	@Override
	public String getProNo() {
		return "sj_square_post_remark_del";
	}
	
	@Override
	public String getUrl() {
		return "app";
	}
}
