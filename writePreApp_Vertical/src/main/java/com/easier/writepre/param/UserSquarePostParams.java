package com.easier.writepre.param;

/**
 * 他人广场贴参数
 * 
 * @author zhaomaohan
 * 
 */
public class UserSquarePostParams extends BaseBodyParams {

	private String user_id,last_id, count;

	public UserSquarePostParams(String user_id,String last_id, String count) {
		this.user_id = user_id;
		this.count = count;
		this.last_id = last_id;
	}

	@Override
	public String getProNo() {
		return "sj_square_post_others";
	}

	@Override
	public String getUrl() {
		return "login";
	}

}
