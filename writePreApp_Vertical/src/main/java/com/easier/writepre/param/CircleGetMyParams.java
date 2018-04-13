package com.easier.writepre.param;

import com.easier.writepre.utils.SPUtils;

/**
 * 我的圈子
 * 
 * 
 */
public class CircleGetMyParams extends BaseBodyParams {


	@Override
	public String getProNo() {
		return "sj_circle_self";
	}

	@Override
	public String getUrl() {
		return (boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false) ? "app" : "login";
	}

}
