package com.easier.writepre.response;

import java.util.List;

import com.easier.writepre.entity.SectionInfo;

public class SectionResponse extends BaseResponse {

	private SectionResponseBody repBody;

	public SectionResponseBody getRepBody() {
		return repBody;
	}

	public void setRepBody(SectionResponseBody repBody) {
		this.repBody = repBody;
	}

	public class SectionResponseBody {

		public List<SectionInfo> getList() {
			return list;
		}

		public void setList(List<SectionInfo> list) {
			this.list = list;
		}

		private List<SectionInfo> list;

	}
}
