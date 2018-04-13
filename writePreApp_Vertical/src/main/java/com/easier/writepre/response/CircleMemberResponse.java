package com.easier.writepre.response;

import java.util.List;

import com.easier.writepre.entity.CircleMember;

/**
 * 圈友
 * 
 * @author dqt
 *
 */
public class CircleMemberResponse extends BaseResponse {

	private Repbody repBody;

	public Repbody getRepBody() {
		return repBody;
	}

	public void setRepBody(Repbody repBody) {
		this.repBody = repBody;
	}

	public class Repbody {

		private List<CircleMember> list;

		public void setList(List<CircleMember> list) {
			this.list = list;
		}

		public List<CircleMember> getList() {
			return list;
		}

	}
}