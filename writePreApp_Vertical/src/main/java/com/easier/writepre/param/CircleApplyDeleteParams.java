package com.easier.writepre.param;

/**
 * 删除加入圈子
 * 
 * 
 */
public class CircleApplyDeleteParams extends BaseBodyParams {
	

	private String req_id;

	public CircleApplyDeleteParams(String req_id) {
		this.req_id = req_id;
	}

	@Override
	public String getProNo() {
		return "sj_circle_user_req_del";
	}

	@Override
	public String getUrl() {
		return "app";
	}

}
