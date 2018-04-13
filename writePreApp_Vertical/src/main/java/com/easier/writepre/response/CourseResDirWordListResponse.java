package com.easier.writepre.response;

import java.util.List;

import com.easier.writepre.entity.CourseResDirWord;

public class CourseResDirWordListResponse extends BaseResponse {

	private CourseResDirWordList repBody;

	public CourseResDirWordList getRepBody() {
		return repBody;
	}

	public void setRepBody(CourseResDirWordList repBody) {
		this.repBody = repBody;
	}

	public class CourseResDirWordList {

		public List<CourseResDirWord> getList() {
			return list;
		}

		public void setList(List<CourseResDirWord> list) {
			this.list = list;
		}

		private List<CourseResDirWord> list;

	}
}
