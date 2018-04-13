package com.easier.writepre.param;

public class CircleMsgCommentParams extends BaseBodyParams {

	private String circle_id, post_id, last_id, count;
	private boolean zanOrComment;


	public CircleMsgCommentParams(String circle_id, String post_id, String last_id,
			String count,boolean zanOrComment) {
		this.circle_id = circle_id;
		this.post_id = post_id;
		this.last_id = last_id;
		this.count = count;
		this.zanOrComment = zanOrComment;
	}

	@Override
	public String getProNo() {
		return zanOrComment ? "sj_circle_post_ok_query"
				: "sj_circle_post_remark_query";
	}

	@Override
	public String getUrl() {
		return "login";
	}
}
