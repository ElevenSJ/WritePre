package com.easier.writepre.param;

public class TopicCommentsParams extends BaseBodyParams {

	private String post_id, last_id;
	private int count;

	private boolean zanOrComment;

	public boolean isZanOrComment() {
		return zanOrComment;
	}

	public void setZanOrComment(boolean zanOrComment) {
		this.zanOrComment = zanOrComment;
	}

	public TopicCommentsParams(String post_id, String last_id, int count,boolean zanOrComment) {
		this.post_id = post_id;
		this.last_id = last_id;
		this.count = count;
		this.zanOrComment = zanOrComment;
	}

	@Override
	public String getProNo() {
		return zanOrComment ? "sj_square_post_ok_query"
				: "sj_square_post_remark_query";
	}

	@Override
	public String getUrl() {
		return "login";
	}
}
