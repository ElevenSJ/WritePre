package com.easier.writepre.param;

public class DIYCourseDetailParams extends BaseBodyParams {

	private final String ref_lsn_id;

	@Override
	public String getProNo() {
		return "2011";
	}

	public DIYCourseDetailParams(String ref_lsn_id) {
		this.ref_lsn_id = ref_lsn_id;
	}

	@Override
	public String getUrl() {
		return "/writePieWeb/app";
	}

}
