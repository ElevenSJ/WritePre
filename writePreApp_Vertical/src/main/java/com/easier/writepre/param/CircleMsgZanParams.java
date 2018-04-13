package com.easier.writepre.param;

import com.easier.writepre.utils.SPUtils;

public class CircleMsgZanParams extends BaseBodyParams {

	private String circle_id, post_id, last_id, count;


	public CircleMsgZanParams(String circle_id, String post_id, String last_id,
			String count) {
		this.circle_id = circle_id;
		this.post_id = post_id;
		this.last_id = last_id;
		this.count = count;
	}

	@Override
	public String getProNo() {
		return "sj_circle_post_ok_query";
	}

	@Override
	public String getUrl() {
		return "login";
	}
}
