package com.easier.writepre.response;

import java.util.List;
import com.easier.writepre.entity.ChildCategoryList;

public class CategoryResponse extends BaseResponse {

	private CategoryResponseBody repBody;

	public CategoryResponseBody getRepBody() {
		return repBody;
	}

	public void setRepBody(CategoryResponseBody repBody) {
		this.repBody = repBody;
	}

	public class CategoryResponseBody {

		public List<ChildCategoryList> getList() {
			return list;
		}

		public void setList(List<ChildCategoryList> list) {
			this.list = list;
		}

		private List<ChildCategoryList> list;

	}
}
