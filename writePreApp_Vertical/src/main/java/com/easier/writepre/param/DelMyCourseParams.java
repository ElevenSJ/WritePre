package com.easier.writepre.param;

public class DelMyCourseParams extends BaseBodyParams {

	private final String cs_id;

	@Override
	public String getProNo() {
		return "2008";
	}

	public DelMyCourseParams(String cs_id) {
		this.cs_id = cs_id;
	}

	@Override
	public String getUrl() {
		return "/writePieWeb/app";
	}

}
