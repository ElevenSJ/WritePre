package com.easier.writepre.response;

import java.util.List;

import com.easier.writepre.entity.JointAttentionInfo;

/**
 * 关注列表返回结构体
 * 
 * @author zhoulu
 * 
 */
public class RongYunJointAttentionListResponse extends BaseResponse {
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