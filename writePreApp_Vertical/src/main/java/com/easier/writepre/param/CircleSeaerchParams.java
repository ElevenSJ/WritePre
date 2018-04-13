package com.easier.writepre.param;

import com.easier.writepre.utils.SPUtils;

/**
 * 搜索圈子
 * 
 * 
 */
public class CircleSeaerchParams extends BaseBodyParams {

	private String key_words;
	private int  start;
	private int count;
	private String type;

	public CircleSeaerchParams(String key_words,int start,int count,String type) {
		this.key_words = key_words;
		this.start = start;
		this.count = count;
		this.type = type;
	}

	@Override
	public String getProNo() {
		return "sj_circle_query";
	}

	@Override
	public String getUrl() {
		return (boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false) ? "app" : "login";
	}

}
