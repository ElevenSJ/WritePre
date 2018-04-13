package com.easier.writepre.param;

import com.easier.writepre.utils.SPUtils;

/**
 * 圈子详情
 * 
 * 
 */
public class CircleGetDetailParams extends BaseBodyParams {
	
	private String circle_id;

	

	public CircleGetDetailParams(String circle_id) {
		this.circle_id = circle_id;
	}

	@Override
	public String getProNo() {
		return "sj_circle_detail";
	}

	@Override
	public String getUrl() {
		return (boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false) ? "app" : "login";
	}

}
