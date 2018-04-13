package com.easier.writepre.response;

import com.easier.writepre.entity.ActiveInfo;
import com.easier.writepre.entity.ContentInfo;

import java.util.List;

public class ActiveContentResponse extends BaseResponse {

	private Repbody repBody;

	public Repbody getRepBody() {
		return repBody;
	}

	public void setRepBody(Repbody repBody) {
		this.repBody = repBody;
	}

	public class Repbody {

		private List<ActiveInfo> list;

		public void setList(List<ActiveInfo> list) {
			this.list = list;
		}

		public List<ActiveInfo> getList() {
			return list;
		}

	}

}