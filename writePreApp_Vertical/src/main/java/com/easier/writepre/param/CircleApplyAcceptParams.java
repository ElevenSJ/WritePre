package com.easier.writepre.param;

import com.easier.writepre.utils.SPUtils;

/**
 * 同意加入圈子
 * 
 * 
 */
public class CircleApplyAcceptParams extends BaseBodyParams {
	
	private String circle_id;

	private String req_id;

	public CircleApplyAcceptParams(String circle_id,String req_id) {
		this.circle_id = circle_id;
		this.req_id = req_id;
	}

	@Override
	public String getProNo() {
		return "sj_circle_user_accept";
	}

	@Override
	public String getUrl() {
		return (boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false) ? "app" : "login";
	}

}
