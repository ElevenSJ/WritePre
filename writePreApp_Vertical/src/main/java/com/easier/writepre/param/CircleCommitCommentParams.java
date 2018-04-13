package com.easier.writepre.param;

import com.easier.writepre.utils.SPUtils;

public class CircleCommitCommentParams extends BaseBodyParams {

	private String circle_id; // 圈id
	private String post_id; // 帖子id
	private String reply_to; // 回复填：评论id,新增评论填空串
	private String reply_to_user; // 回复给谁, 新增时填空串
	private String title; // 回复内容
	private String voice_url="";//音频网络url
	private String voice_len="0";//音频时长
	public CircleCommitCommentParams(String circle_id, String post_id, String reply_to, String reply_to_user,
			String title, String voice_url, String voice_len) {
		// super();
		this.circle_id = circle_id;
		this.post_id = post_id;
		this.reply_to = reply_to;
		this.reply_to_user = reply_to_user;
		this.title = title;
		this.voice_url = voice_url;
		this.voice_len = voice_len;
	}

	@Override
	public String getProNo() {
		return "sj_circle_post_remark";
	}

	@Override
	public String getUrl() {
		return (boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false) ? "app" : "login";
	}
}
