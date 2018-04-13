package com.easier.writepre.param;

/**
 * 
 * 查询推荐优秀大赛作品
 * 
 * @author zhaomaohan
 * 
 */
public class PkWorksRecommendParams extends BaseBodyParams {

	private String pk_id;

	public PkWorksRecommendParams(String pk_id){
		this.pk_id = pk_id;
	}

	@Override
	public String getProNo() {
		return "sj_pk_works_rec";
	}

	@Override
	public String getUrl() {
		//return "app";
		return "login";
	}

}
