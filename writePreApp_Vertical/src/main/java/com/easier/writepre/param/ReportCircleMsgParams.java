package com.easier.writepre.param;

import com.easier.writepre.utils.SPUtils;

public class ReportCircleMsgParams extends BaseBodyParams {

	private String circle_id,post_id,title,type;


	public ReportCircleMsgParams(String circle_id,String post_id, String title, String type) {
		this.circle_id = circle_id;
		this.post_id = post_id;
		this.title = title;
		this.type = type;
	}

	@Override
	public String getProNo() {
		return "sj_circle_post_accuse";
	}
	
	@Override
	public String getUrl() {
		return (boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false) ? "app" : "login";
	}

}
