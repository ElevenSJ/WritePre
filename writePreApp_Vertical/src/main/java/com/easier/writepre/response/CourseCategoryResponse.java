package com.easier.writepre.response;

import com.easier.writepre.entity.CourseCategoryList;

public class CourseCategoryResponse extends BaseResponse {

	private CourseCategoryList repBody;

	public CourseCategoryList getRepBody() {
		return repBody;
	}

	public void setRepBody(CourseCategoryList repBody) {
		this.repBody = repBody;
	}

}
