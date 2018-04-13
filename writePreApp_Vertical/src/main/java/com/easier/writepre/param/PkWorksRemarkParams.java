package com.easier.writepre.param;

public class PkWorksRemarkParams extends BaseBodyParams {
	
	private String works_id;		// 帖子id
	private String reply_to;   // 回复填：评论id,新增评论填空串
	private String reply_to_user;  // 回复给谁,  新增时填空串
	private String title;	// 回复内容
	private String voice_url="";//音频网络url
	private String voice_len="0";//音频时长
	
	public PkWorksRemarkParams(String works_id, String reply_to,
			String reply_to_user, String title, String voice_url, String voice_len) {
		super();
		this.works_id = works_id;
		this.reply_to = reply_to;
		this.reply_to_user = reply_to_user;
		this.title = title;
		this.voice_url = voice_url;
		this.voice_len = voice_len;
	}
	
//	public String getWorks_id() {
//		return works_id;
//	}
//
//	public void setWorks_id(String works_id) {
//		this.works_id = works_id;
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
		// TODO Auto-generated method stub
		return "sj_pk_works_remark";
	}
	
	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		return "app" ;
	}
}
