package com.easier.writepre.response;

import java.util.List;

import com.easier.writepre.entity.CircleMsgInfo;


public class CircleMsgResponse extends BaseResponse {

	private Repbody repBody;

	public Repbody getRepBody() {
		return repBody;
	}

	public void setRepBody(Repbody repBody) {
		this.repBody = repBody;
	}

	public class Repbody {

		private List<CircleMsgInfo> list;

		public void setList(List<CircleMsgInfo> list) {
			this.list = list;
		}

		public List<CircleMsgInfo> getList() {
			return list;
		}

	}

}