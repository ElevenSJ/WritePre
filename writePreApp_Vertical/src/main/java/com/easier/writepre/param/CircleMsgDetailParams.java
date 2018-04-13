package com.easier.writepre.param;

/**
 * 圈子帖子详情请求参数
 * 
 * @author zhaomaohan
 *
 */
public class CircleMsgDetailParams extends BaseBodyParams {

	private final String circle_id;
	private final String post_id;

	@Override
	public String getProNo() {
		return "sj_sh_circle_post_one";
	}

	public CircleMsgDetailParams(String circle_id,String post_id) {
		this.circle_id = circle_id;
		this.post_id = post_id;
	}

	@Override
	public String getUrl() {
		return "login";
	}

}
