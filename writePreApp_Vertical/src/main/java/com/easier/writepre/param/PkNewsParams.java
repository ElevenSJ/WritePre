package com.easier.writepre.param;

public class PkNewsParams extends BaseBodyParams {

	private String pk_id, last_id, count;
	
//	public String getPk_id() {
//		return pk_id;
//	}
//
//	public void setPk_id(String pk_id) {
//		this.pk_id = pk_id;
//	}
//
//	public String getLast_id() {
//		return last_id;
//	}
//
//	public void setLast_id(String last_id) {
//		this.last_id = last_id;
//	}
//
//	public String getCount() {
//		return count;
//	}
//
//	public void setCount(String count) {
//		this.count = count;
//	}
	
	public PkNewsParams(String pk_id, String last_id, String count) {
		this.pk_id = pk_id;
		this.last_id = last_id;
		this.count = count;
	}

	@Override
	public String getProNo() {
		return "sj_pk_news_list";
	}
	
	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		//return UserConfig.getInstance().isLogin() ? "app" : "login";
		return "login";
	}

	
}
