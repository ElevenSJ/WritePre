package com.easier.writepre.param;

/**
 * 用户粉丝
 * 
 * 
 */
public class UserCareMeParams extends BaseBodyParams {

	private String user_id;
	private String start;
	private String count;

	public UserCareMeParams(String user_id, String start, String count) {
		this.user_id = user_id;
		this.start = start;
		this.count = count;
	}

	@Override
	public String getProNo() {
		return "sj_user_care_me_list";
	}

	@Override
	public String getUrl() {
		return "login";
	}

}
