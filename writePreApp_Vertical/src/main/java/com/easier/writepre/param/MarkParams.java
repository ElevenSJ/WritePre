package com.easier.writepre.param;

import com.easier.writepre.utils.SPUtils;

/**
 * 标签
 * 
 * 
 */
public class MarkParams extends BaseBodyParams {

	@Override
	public String getProNo() {
		return "sj_post_mark_query";
	}

	@Override
	public String getUrl() {
		return (boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false) ? "app" : "login";
	}

}
