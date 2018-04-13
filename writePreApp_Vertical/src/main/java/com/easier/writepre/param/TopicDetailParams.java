package com.easier.writepre.param;

/**
 * 帖子详情请求参数
 * 
 * @author zhaomaohan
 *
 */
public class TopicDetailParams extends BaseBodyParams {

	private final String post_id;

	@Override
	public String getProNo() {
		return "sj_sh_square_post_one";
	}

	public TopicDetailParams(String post_id) {
		this.post_id = post_id;
	}

	@Override
	public String getUrl() {
		return "login";
	}

}
