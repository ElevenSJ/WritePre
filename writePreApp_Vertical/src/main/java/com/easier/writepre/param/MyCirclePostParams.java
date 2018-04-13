package com.easier.writepre.param;

/**
 * 我的广场帖子参数
 * 
 * @author zhaomaohan
 * 
 */
public class MyCirclePostParams extends BaseBodyParams {

	private String last_id, count;

	public MyCirclePostParams(String last_id, String count) {
		this.count = count;
		this.last_id = last_id;
	}

	@Override
	public String getProNo() {
		return "sj_circle_post_self_query";
	}

	@Override
	public String getUrl() {
		return "app";
	}

}
