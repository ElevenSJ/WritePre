package com.easier.writepre.response;

import java.util.List;

import com.easier.writepre.entity.SjVodRemarkQueryInfo;

public class SjVodRemarkQueryResponse extends BaseResponse {
	
	private SjVodRemarkQueryBody repBody;
	
	public SjVodRemarkQueryBody getRepBody() {
		return repBody;
	}

	public void setRepBody(SjVodRemarkQueryBody repBody) {
		this.repBody = repBody;
	}

	public class SjVodRemarkQueryBody{
		private List<SjVodRemarkQueryInfo> list;

		public List<SjVodRemarkQueryInfo> getList() {
			return list;
		}

		public void setList(List<SjVodRemarkQueryInfo> list) {
			this.list = list;
		}
	}
}
