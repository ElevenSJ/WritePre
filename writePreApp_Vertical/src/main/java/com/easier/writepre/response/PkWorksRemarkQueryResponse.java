package com.easier.writepre.response;

import java.util.List;

import com.easier.writepre.entity.PkWorksRemarkQueryInfo;

public class PkWorksRemarkQueryResponse extends BaseResponse {
	
	private PkWorksRemarkQueryBody repBody;
	
	public PkWorksRemarkQueryBody getRepBody() {
		return repBody;
	}

	public void setRepBody(PkWorksRemarkQueryBody repBody) {
		this.repBody = repBody;
	}

	public class PkWorksRemarkQueryBody{
		private List<PkWorksRemarkQueryInfo> list;

		public List<PkWorksRemarkQueryInfo> getList() {
			return list;
		}

		public void setList(List<PkWorksRemarkQueryInfo> list) {
			this.list = list;
		}
	}
}
