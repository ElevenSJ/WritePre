package com.easier.writepre.param;

public class AddFavParams extends BaseBodyParams {

	private final String ref_repo_cata_id;

	@Override
	public String getProNo() {
		return "2014";
	}

	public AddFavParams(String ref_repo_cata_id) {
		this.ref_repo_cata_id = ref_repo_cata_id;
	}

	@Override
	public String getUrl() {
		return "/writePieWeb/app";
	}

}
