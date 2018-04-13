package com.easier.writepre.response;

import java.util.List;

import com.easier.writepre.entity.CircleInfo;

public class CircleMyResponse extends BaseResponse {

	private Repbody repBody;

	public Repbody getRepBody() {
		return repBody;
	}

	public void setRepBody(Repbody repBody) {
		this.repBody = repBody;
	}

	public class Repbody {

		private List<CircleInfo> list;

		public void setList(List<CircleInfo> list) {
			this.list = list;
		}

		public List<CircleInfo> getList() {
			return list;
		}

	}

}