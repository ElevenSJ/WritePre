package com.easier.writepre.response;

import com.easier.writepre.entity.RecommendInfo;
import com.easier.writepre.entity.SearchInfo;

import java.util.List;

/**
 * 搜索返回数据
 * @author zhoulu
 */
public class SearchResponse extends BaseResponse {

	private Repbody repBody;

	public Repbody getRepBody() {
		return repBody;
	}

	public void setRepBody(Repbody repBody) {
		this.repBody = repBody;
	}

	public class Repbody {

		private List<SearchInfo> list;

		public void setList(List<SearchInfo> list) {
			this.list = list;
		}

		public List<SearchInfo> getList() {
			return list;
		}

	}

}