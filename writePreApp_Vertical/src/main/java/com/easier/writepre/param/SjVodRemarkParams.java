package com.easier.writepre.param;

public class SjVodRemarkParams extends BaseBodyParams {
	
	private String group_id;		// id
	private String reply_to;   // 回复填：评论id,新增评论填空串
	private String reply_to_user;  // 回复给谁,  新增时填空串
	private String title;	// 回复内容
	
	
	public SjVodRemarkParams(String group_id, String reply_to,
			String reply_to_user, String title) {
		super();
		this.group_id = group_id;
		this.reply_to = reply_to;
		this.reply_to_user = reply_to_user;
		this.title = title;
	}
	
//	public String getGroup_id() {
//		return group_id;
//	}
//
//	public void setGroup_id(String group_id) {
//		this.group_id = group_id;
//	}
//
//	public String getReply_to() {
//		return reply_to;
//	}
//
//	public void setReply_to(String reply_to) {
//		this.reply_to = reply_to;
//	}
//
//	public String getReply_to_user() {
//		return reply_to_user;
//	}
//
//	public void setReply_to_user(String reply_to_user) {
//		this.reply_to_user = reply_to_user;
//	}
//
//	public String getTitle() {
//		return title;
//	}
//
//	public void setTitle(String title) {
//		this.title = title;
//	}

	@Override
	public String getProNo() {
		return "sj_vod_remark";
	}
	
	@Override
	public String getUrl() {
		return "app" ;
	}
}
