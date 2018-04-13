package com.easier.writepre.param;

import com.easier.writepre.utils.SPUtils;

/**
 * 最近的书院新闻（取最近10条记录）
 */
public class TecSchoolNewsListParams extends BaseBodyParams {

	@Override
	public String getProNo() {
		// TODO Auto-generated method stub
		return "tec_school_news_list";
	}
	
	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		return (boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false) ? "app" : "login";
	}
}
