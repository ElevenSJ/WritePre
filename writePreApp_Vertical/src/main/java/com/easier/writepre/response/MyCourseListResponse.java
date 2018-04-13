package com.easier.writepre.response;

import java.util.List;

import com.easier.writepre.entity.CourseInfo;

public class MyCourseListResponse extends BaseResponse {

	private MyCourseListBody repBody;

	public MyCourseListBody getRepBody() {
		return repBody;
	}

	public void setRepBody(MyCourseListBody repBody) {
		this.repBody = repBody;
	}

	public class MyCourseListBody {

		public List<CourseInfo> getList() {
			return list;
		}

		public void setList(List<CourseInfo> list) {
			this.list = list;
		}

		private List<CourseInfo> list;

	}
}
