package com.easier.writepre.response;

import com.easier.writepre.entity.TecSchoolNewsInfo;

import java.util.List;

/**
 * 最近的书院新闻（取最近10条记录）
 */
public class TecSchoolNewsListResponse extends BaseResponse {
	
	private TecSchoolNewsBody repBody;
	
	public TecSchoolNewsBody getRepBody() {
		return repBody;
	}

	public void setRepBody(TecSchoolNewsBody repBody) {
		this.repBody = repBody;
	}

	public class TecSchoolNewsBody{
		private List<TecSchoolNewsInfo> list;

		public List<TecSchoolNewsInfo> getList() {
			return list;
		}

		public void setList(List<TecSchoolNewsInfo> list) {
			this.list = list;
		}
	}
}
