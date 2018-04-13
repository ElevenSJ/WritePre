package com.easier.writepre.param;

import com.easier.writepre.utils.SPUtils;

/**
 * 圈子动态
 * 
 * @author kai.zhong
 * 
 */
public class CircleMsgListParams extends BaseBodyParams {

	private String circle_id,last_id, count;

	public CircleMsgListParams(String circle_id,String last_id, String count) {
		this.count = count;
		this.last_id = last_id;
		this.circle_id = circle_id;
	}

	@Override
	public String getProNo() {
		return "sj_circle_post_query";
	}

	@Override
	public String getUrl() {
		return (boolean) SPUtils.instance().get(SPUtils.IS_LOGIN,false) ? "app" : "login";
	}

}
