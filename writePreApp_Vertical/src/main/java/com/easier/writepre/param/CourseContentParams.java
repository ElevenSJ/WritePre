package com.easier.writepre.param;

public class CourseContentParams extends BaseBodyParams {

	private final String cs_cata_id;

	@Override
	public String getProNo() {
		return "2003";
	}

	public CourseContentParams(String cs_cata_id) {
		//2003 课程--课程内容(包含范字)
		this.cs_cata_id = cs_cata_id;
	}

	@Override
	public String getUrl() {
		return "/writePieWeb/login";
	}

}
