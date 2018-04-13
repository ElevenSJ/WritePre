package com.easier.writepre.param;

import com.easier.writepre.utils.SPUtils;

/**
 * 圈子动态
 * 
 * @author kai.zhong
 * 
 */
public class CircleMsgGetParams extends BaseBodyParams {

	private String last_id, count;


	public CircleMsgGetParams(String last_id, String count) {
		this.count = count;
		this.last_id = last_id;
	}

	@Override
	public String getProNo() {
		return "sj_circle_user_post_query";
	}

	@Override
	public String getUrl() {
		return (boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false) ? "app" : "login";
	}

}
