package com.easier.writepre.param;

public class PkCategoryParams extends BaseBodyParams {
	
	private String role;
	
//	public String getRole() {
//		return role;
//	}
//
//	public void setRole(String role) {
//		this.role = role;
//	}

	
	public PkCategoryParams(String role) {
		this.role = role;
	}

	@Override
	public String getProNo() {
		return "sj_pk_cata_list";
	}
	
	@Override
	public String getUrl() {
		//return UserConfig.getInstance().isLogin() ? "app" : "login";
		return "login";
	}

}
