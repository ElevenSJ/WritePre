package com.easier.writepre.response;

import java.util.List;

import com.easier.writepre.entity.PushMessageEntity;

public class UserPushMessageResponse extends BaseResponse {

	private Repbody repBody;

	public Repbody getRepBody() {
		return repBody;
	}

	public void setRepBody(Repbody repBody) {
		this.repBody = repBody;
	}

	public class Repbody {

		private List<PushMessageEntity> list;

		public void setList(List<PushMessageEntity> list) {
			this.list = list;
		}

		public List<PushMessageEntity> getList() {
			return list;
		}

	}

}