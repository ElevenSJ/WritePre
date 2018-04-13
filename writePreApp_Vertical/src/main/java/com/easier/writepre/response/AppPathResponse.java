package com.easier.writepre.response;

import com.easier.writepre.entity.AppPathInfo;

import java.util.List;

public class AppPathResponse extends BaseResponse {


	private AppPathResponse.AppPathBody repBody;

	public AppPathResponse.AppPathBody getRepBody() {
		return repBody;
	}

	public void setRepBody(AppPathResponse.AppPathBody repBody) {
		this.repBody = repBody;
	}

	public class AppPathBody {
		private List<AppPathInfo> list;

		public List<AppPathInfo> getList() {
			return list;
		}

		public void setList(List<AppPathInfo> list) {
			this.list = list;
		}
	}

}
