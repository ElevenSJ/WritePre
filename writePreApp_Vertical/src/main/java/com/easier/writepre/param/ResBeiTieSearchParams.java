package com.easier.writepre.param;

/**
 * 碑帖搜索
 * 
 */
public class ResBeiTieSearchParams extends BaseBodyParams {

	private String words;

	@Override
	public String getProNo() {
		return "res_beitie_search";
	}

	public ResBeiTieSearchParams(String words) {
		this.words = words;
	}

	@Override
	public String getUrl() {
		return "/writePieWeb/login";
	}

}
