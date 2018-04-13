package com.easier.writepre.param;

import com.easier.writepre.utils.SPUtils;

/**
 * 举报圈子
 * 
 * 
 */
public class CircleReportParams extends BaseBodyParams {

	private String circle_id;
	
	private String title;
	
	private String type;

	public CircleReportParams(String circle_id,String title,String type) {
		this.circle_id = circle_id;
		this.title = title;
		this.type = type;
	}

	@Override
	public String getProNo() {
		return "sj_circle_accuse";
	}

	@Override
	public String getUrl() {
		return (boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false) ? "app" : "login";
	}

}
