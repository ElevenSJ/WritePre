package com.easier.writepre.response;

import java.util.List;

import com.easier.writepre.entity.BannersInfo;

public class BannersResponse extends BaseResponse {

	private BannersBody repBody;

	public BannersBody getRepBody() {
		return repBody;
	}

	public void setRepBody(BannersBody repBody) {
		this.repBody = repBody;
	}

	public class BannersBody {
		private List<BannersInfo> list;

		public List<BannersInfo> getList() {
			return list;
		}

		public void setList(List<BannersInfo> list) {
			this.list = list;
		}
	}
}
