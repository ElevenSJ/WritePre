package com.easier.writepre.param;

/**
 * 书法家相关碑帖查询
 * 
 */
public class ResBeiTieListParams extends BaseBodyParams {

	private String penmen_id;

	@Override
	public String getProNo() {
		return "res_beitie_list";
	}

	public ResBeiTieListParams(String penmen_id) {
		this.penmen_id = penmen_id;
	}

	@Override
	public String getUrl() {
		return "/writePieWeb/login";
	}

}
