package com.easier.writepre.param;

/**
 * 
 * 大赛详情
 * 
 * @author kai.zhong
 * 
 */
public class PKInfoParams extends BaseBodyParams {

	private String pk_id;

	public PKInfoParams(String pk_id){
		this.pk_id = pk_id;
	}
	@Override
	public String getProNo() {
		return "sj_pk_info";
	}

	@Override
	public String getUrl() {
		// return "app";
		return "login";
	}

}
