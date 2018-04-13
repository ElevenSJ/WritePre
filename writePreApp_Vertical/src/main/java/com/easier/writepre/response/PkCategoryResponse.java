package com.easier.writepre.response;

import java.util.List;

import com.easier.writepre.entity.PkCategoryInfo;

public class PkCategoryResponse extends BaseResponse {
	private PkCategoryBody repBody;
	
	public PkCategoryBody getRepBody() {
		return repBody;
	}

	public void setRepBody(PkCategoryBody repBody) {
		this.repBody = repBody;
	}

	public class PkCategoryBody{
		private List<PkCategoryInfo> list;

		public List<PkCategoryInfo> getList() {
			return list;
		}

		public void setList(List<PkCategoryInfo> list) {
			this.list = list;
		}
		
	}
}
