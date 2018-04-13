package com.easier.writepre.response;

import java.util.List;

import com.easier.writepre.entity.CircleApplyMember;

/**
 * 圈友申请列表
 * 
 * @author dqt
 *
 */
public class CircleApplyMemberResponse extends BaseResponse {

	private Repbody repBody;

	public Repbody getRepBody() {
		return repBody;
	}

	public void setRepBody(Repbody repBody) {
		this.repBody = repBody;
	}


	public class Repbody {

		private List<CircleApplyMember> list;

		public void setList(List<CircleApplyMember> list) {
			this.list = list;
		}

		public List<CircleApplyMember> getList() {
			return list;
		}

	}
}