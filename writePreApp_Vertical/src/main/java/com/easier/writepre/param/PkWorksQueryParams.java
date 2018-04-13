package com.easier.writepre.param;

/**
 * 
 * 教师学生人气前三
 * 
 * @author kai.zhong
 * 
 */
public class PkWorksQueryParams extends BaseBodyParams {

	private String pk_id;

	private String last_id;

	private String status;

	private String role;

	private String pk_cata_id;

	private String city;

	private String count;

	public PkWorksQueryParams(String pk_id,String last_id, String status, String role,
			String pk_cata_id, String city, String count) {
		this.pk_id = pk_id;
		this.last_id = last_id;
		this.status = status;
		this.role = role;
		this.pk_cata_id = pk_cata_id;
		this.city = city;
		this.count = count;
	}

	@Override
	public String getProNo() {
		return "sj_pk_works_query";
	}

	@Override
	public String getUrl() {
		//return "app";
		return "login";
	}

}
