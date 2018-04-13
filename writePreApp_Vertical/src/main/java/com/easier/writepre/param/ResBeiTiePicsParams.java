package com.easier.writepre.param;

/**
 * 碑帖图片列表
 * 
 */
public class ResBeiTiePicsParams extends BaseBodyParams {

	private String beitie_id;

	@Override
	public String getProNo() {
		return "res_beitie_pics";
	}

	public ResBeiTiePicsParams(String beitie_id) {
		this.beitie_id = beitie_id;
	}

	@Override
	public String getUrl() {
		return "/writePieWeb/login";
	}

}
