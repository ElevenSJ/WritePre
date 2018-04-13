package com.easier.writepre.response;

import java.util.List;
import com.easier.writepre.entity.WordFontListInfo;

public class SearchWordResponse extends BaseResponse {

	private SearchWordResponseBody repBody;

	public class SearchWordResponseBody {
		private List<WordFontListInfo> list;

		public List<WordFontListInfo> getList() {
			return list;
		}

		public void setList(List<WordFontListInfo> list) {
			this.list = list;
		}

	}

	public SearchWordResponseBody getRepBody() {
		return repBody;
	}

	public void setRepBody(SearchWordResponseBody repBody) {
		this.repBody = repBody;
	}
}
