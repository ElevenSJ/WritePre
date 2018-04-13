package com.easier.writepre.param;

/**
 * 
 * 人气排序作品列表
 * 
 * @author kai.zhong
 * 
 */
public class PkWorksGoodedParams extends BaseBodyParams {

	private String pk_id;

	private String status;

	private String role;

	private String pk_cata_id;

	private String city;

	private int start;
	
	private int count;



	public PkWorksGoodedParams(String pk_id,String status, String role, String pk_cata_id,
			String city, int start, int count) {
		this.pk_id = pk_id;
		this.status = status;
		this.role = role;
		this.pk_cata_id = pk_cata_id;
		this.city = city;
		this.start = start;
		this.count = count;
	}

	@Override
	public String getProNo() {
		return "sj_pk_works_gooded";
	}

	@Override
	public String getUrl() {
		//return "app";
		return "login";
	}

}
