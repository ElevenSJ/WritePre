package com.easier.writepre.response;

import com.easier.writepre.entity.CircleMember;
import com.easier.writepre.entity.JointAttentionInfo;

import java.util.List;

/**
 * @某人 成员返回
 * 
 * @author zhoulu
 *
 */
public class TellSomeOneMemberResponse extends BaseResponse {

	private Repbody repBody;

	public Repbody getRepBody() {
		return repBody;
	}

	public void setRepBody(Repbody repBody) {
		this.repBody = repBody;
	}

	public class Repbody {

		private List<JointAttentionInfo> list;

		public void setList(List<JointAttentionInfo> list) {
			this.list = list;
		}

		public List<JointAttentionInfo> getList() {
			return list;
		}

	}
}