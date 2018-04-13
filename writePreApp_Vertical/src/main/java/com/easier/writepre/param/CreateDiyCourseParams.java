package com.easier.writepre.param;

import java.util.List;

import com.easier.writepre.entity.ClassHour;
import com.easier.writepre.entity.ClassInfo;

public class CreateDiyCourseParams extends BaseBodyParams {

	private final String title;
	private final String author;
	private final String memo;
	private final String face_url;
	private final List<ClassHour> child;

	@Override
	public String getProNo() {
		return "2010";
	}

	public CreateDiyCourseParams(ClassInfo body) {
		this.title = body.getTitle();
		this.author = body.getAuthor();
		this.face_url = body.getFace_url();
		this.memo = "";
		this.child = body.getChild();
	}

	@Override
	public String getUrl() {
		return "/writePieWeb/app";
	}

}
