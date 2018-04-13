package com.easier.writepre.param;

public class MyCouresListParams extends BaseBodyParams {

	private final String count;
	private final String last_id;

	@Override
	public String getProNo() {
		return "2001";
	}

	public MyCouresListParams(String count, String last_id) {
		this.count = count;
		this.last_id = last_id;
	}

	@Override
	public String getUrl() {
		return "/writePieWeb/app";
	}

}
