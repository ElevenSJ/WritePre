package com.easier.writepre.param;

public class FavOrNotParams extends BaseBodyParams {

	private final String ref_repo_cata_id;

	@Override
	public String getProNo() {
		return "1020";
	}

	public FavOrNotParams(String ref_repo_cata_id) {
		this.ref_repo_cata_id = ref_repo_cata_id;
	}

	@Override
	public String getUrl() {
		return "/writePieWeb/app";
	}

}
