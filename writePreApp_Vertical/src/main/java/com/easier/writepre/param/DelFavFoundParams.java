package com.easier.writepre.param;

public class DelFavFoundParams extends BaseBodyParams {

	private String ref_repo_cata_id;
	
	public DelFavFoundParams(String ref_repo_cata_id) {
		this.ref_repo_cata_id = ref_repo_cata_id;
	}
	
	@Override
	public String getProNo() {
		return "1009";
	}

}
