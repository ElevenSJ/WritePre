package com.easier.writepre.param;

public class DelFavParams extends BaseBodyParams {

	private String fav_id;
	
	public DelFavParams(String fav_id) {
		this.fav_id = fav_id;
	}
	
	@Override
	public String getProNo() {
		return "1011";
	}

}
