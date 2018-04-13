package com.easier.writepre.response;

import com.easier.writepre.entity.RecommendInfo;

import java.util.List;

/**
 * 推荐返回数据
 * @author zhoulu
 */
public class RecommendResponse extends BaseResponse {

	private Repbody repBody;

	public Repbody getRepBody() {
		return repBody;
	}

	public void setRepBody(Repbody repBody) {
		this.repBody = repBody;
	}

	public class Repbody {

		private List<RecommendInfo> list;

		public void setList(List<RecommendInfo> list) {
			this.list = list;
		}

		public List<RecommendInfo> getList() {
			return list;
		}

	}

}