package com.easier.writepre.response;

import java.util.List;

import com.easier.writepre.entity.CourseType;

public class CourseTypeListResponse extends BaseResponse {

	private CourseTypeResponseBody repBody;

	public CourseTypeResponseBody getRepBody() {
		return repBody;
	}

	public void setRepBody(CourseTypeResponseBody repBody) {
		this.repBody = repBody;
	}

	public class CourseTypeResponseBody {

		public List<CourseType> getList() {
			return list;
		}

		public void setList(List<CourseType> list) {
			this.list = list;
		}

		private List<CourseType> list;

	}
}
