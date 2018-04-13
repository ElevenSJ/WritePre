package com.easier.writepre.response;

import java.util.List;

import com.easier.writepre.entity.CircleCommentsInfo;

public class CircleMsgCommentsResponse extends BaseResponse {

	private CircleCommentsBody repBody;

	public CircleCommentsBody getRepBody() {
		return repBody;
	}

	public void setRepBody(CircleCommentsBody repBody) {
		this.repBody = repBody;
	}

	public class CircleCommentsBody {

		private List<CircleCommentsInfo> list;

		public List<CircleCommentsInfo> getList() {
			return list;
		}

		public void setList(List<CircleCommentsInfo> list) {
			this.list = list;
		}

	}
}
