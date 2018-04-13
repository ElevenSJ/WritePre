package com.easier.writepre.response;

import java.util.List;

import com.easier.writepre.entity.PkNewsInfo;

public class PkNewsResponse extends BaseResponse {
	
	private PkNewsBody repBody;
	
	public PkNewsBody getRepBody() {
		return repBody;
	}

	public void setRepBody(PkNewsBody repBody) {
		this.repBody = repBody;
	}

	public class PkNewsBody{
		private List<PkNewsInfo> list;

		public List<PkNewsInfo> getList() {
			return list;
		}

		public void setList(List<PkNewsInfo> list) {
			this.list = list;
		}
	}
}
