package com.easier.writepre.response;

import java.util.List;

import com.easier.writepre.entity.CourseInfo;

public class CourseResponse extends BaseResponse {

	private CourseResponseBody repBody;

	public CourseResponseBody getRepBody() {
		return repBody;
	}

	public void setRepBody(CourseResponseBody repBody) {
		this.repBody = repBody;
	}

	public class CourseResponseBody {

		public List<CourseInfo> getList() {
			return list;
		}

		public void setList(List<CourseInfo> list) {
			this.list = list;
		}

		private List<CourseInfo> list;

	}
}
