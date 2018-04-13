package com.easier.writepre.param;

/**
 * 推荐请求参数
 * 
 * @author zhoulu
 * 
 */
public class RecommendParams extends BaseBodyParams {

	private String start;
	private String count;

	public RecommendParams(String start, String count) {
		this.start = start;
		this.count = count;
	}

	@Override
	public String getProNo() {
		return "sj_user_rec_query";
	}

	@Override
	public String getUrl() {
		return "login";
	}
}
