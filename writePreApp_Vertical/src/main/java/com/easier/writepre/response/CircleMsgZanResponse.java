package com.easier.writepre.response;

import java.util.List;

import com.easier.writepre.entity.CircleCommentsInfo;

public class CircleMsgZanResponse extends BaseResponse {

	private CircleZanBody repBody;

	public CircleZanBody getRepBody() {
		return repBody;
	}

	public void setRepBody(CircleZanBody repBody) {
		this.repBody = repBody;
	}

	public class CircleZanBody {

		private List<CircleCommentsInfo> list;

		public List<CircleCommentsInfo> getList() {
			return list;
		}

		public void setList(List<CircleCommentsInfo> list) {
			this.list = list;
		}

	}
}
