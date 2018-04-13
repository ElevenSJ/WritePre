package com.easier.writepre.param;

/**
 * 我的广场帖子参数
 * 
 * @author zhaomaohan
 * 
 */
public class MySquarePostParams extends BaseBodyParams {

	private String last_id, count;

	public MySquarePostParams(String last_id, String count) {
		this.count = count;
		this.last_id = last_id;
	}

	@Override
	public String getProNo() {
		return "sj_square_self_post_query";
	}

	@Override
	public String getUrl() {
		return "app";
	}

}
