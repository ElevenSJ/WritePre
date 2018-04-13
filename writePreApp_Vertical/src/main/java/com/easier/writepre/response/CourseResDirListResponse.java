package com.easier.writepre.response;

import java.util.List;

import com.easier.writepre.entity.CourseResDir;

public class CourseResDirListResponse extends BaseResponse {

	private CourseResDirList repBody;

	public CourseResDirList getRepBody() {
		return repBody;
	}

	public void setRepBody(CourseResDirList repBody) {
		this.repBody = repBody;
	}

	public class CourseResDirList {

		public List<CourseResDir> getList() {
			return list;
		}

		public void setList(List<CourseResDir> list) {
			this.list = list;
		}

		private List<CourseResDir> list;

	}
}
