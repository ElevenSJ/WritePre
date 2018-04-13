package com.easier.writepre.param;

import com.easier.writepre.utils.SPUtils;

public class CircleCommentsDeleteParams extends BaseBodyParams {
	
	private String remark_id;
	private String circle_id;
	

	public CircleCommentsDeleteParams(String circle_id,String remark_id) {
		this.remark_id = remark_id;
		this.circle_id=circle_id;
	}

	@Override
	public String getProNo() {
		return "sj_circle_post_remark_del";
	}
	
	@Override
	public String getUrl() {
		return (boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false) ? "app" : "login";
	}
}
