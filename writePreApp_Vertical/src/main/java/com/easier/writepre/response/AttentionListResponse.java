package com.easier.writepre.response;

import java.util.List;

import com.easier.writepre.entity.AttentionInfo;

/**
 * 关注列表返回结构体
 * 
 * @author zhoulu
 * 
 */
public class AttentionListResponse extends BaseResponse {
	private Repbody repBody;

	public Repbody getRepBody() {
		return repBody;
	}

	public void setRepBody(Repbody repBody) {
		this.repBody = repBody;
	}

	public class Repbody {

		private List<AttentionInfo> list;

		public void setList(List<AttentionInfo> list) {
			this.list = list;
		}

		public List<AttentionInfo> getList() {
			return list;
		}

	}
}