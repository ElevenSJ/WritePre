package com.easier.writepre.param;

/**
 * 机构报名查询
 */
public class CalligraphyCommitParams extends BaseBodyParams {
	private String org_name;
	private String business;
	private String city;
	private String email;
	private String tel;

	public CalligraphyCommitParams(String org_name,String business,String city,String email,String tel){
		this.org_name = org_name;
		this.business = business;
		this.city = city;
		this.email = email;
		this.tel = tel;
	}
	@Override
	public String getProNo() {
		return "sj_org_req";
	}

	@Override
	public String getUrl() {
		return  "app";
	}

}
