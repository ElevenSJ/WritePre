package com.easier.writepre.response;

import java.util.List;

import com.easier.writepre.entity.ContentInfo;

public class SquareContentGetResponse extends BaseResponse {

	private Repbody repBody;

	public Repbody getRepBody() {
		return repBody;
	}

	public void setRepBody(Repbody repBody) {
		this.repBody = repBody;
	}

	public class Repbody {

		private List<ContentInfo> list;

		public void setList(List<ContentInfo> list) {
			this.list = list;
		}

		public List<ContentInfo> getList() {
			return list;
		}

	}

}