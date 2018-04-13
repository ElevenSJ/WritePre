package com.easier.writepre.param;

public class CategoryParams extends BaseBodyParams {

	private final String ref_repo_id;

	@Override
	public String getProNo() {
		return "2013";
	}

	public CategoryParams(String ref_repo_id) {
		this.ref_repo_id = ref_repo_id;
	}

	@Override
	public String getUrl() {
		return "/writePieWeb/login";
	}

}
