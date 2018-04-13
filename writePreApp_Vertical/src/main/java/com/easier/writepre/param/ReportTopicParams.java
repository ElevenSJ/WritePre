package com.easier.writepre.param;

public class ReportTopicParams extends BaseBodyParams {

	private String post_id,title,type;

	public ReportTopicParams(String post_id, String title, String type) {
		this.post_id = post_id;
		this.title = title;
		this.type = type;
	}

	@Override
	public String getProNo() {
		return "sj_square_post_accuse";
	}
	
	@Override
	public String getUrl() {
		return "app";
	}

}
