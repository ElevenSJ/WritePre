package com.easier.writepre.param;

/**
 * 
 * @author
 * 
 */
public class HeadUpdataParams extends BaseBodyParams {

	private String head_img;

	public HeadUpdataParams(String head_img) {
		this.head_img = head_img;
	}

	@Override
	public String getProNo() {
		return "app_head_update";
	}

	@Override
	public String getUrl() {
		return "/writePieWeb/app";
	}

}
