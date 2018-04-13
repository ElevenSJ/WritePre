package com.easier.writepre.response;

public class AddFavWordResponse extends BaseResponse {

	private AddFavResponseBody repBody;

	public AddFavResponseBody getRepBody() {
		return repBody;
	}

	public void setRepBody(AddFavResponseBody repBody) {
		this.repBody = repBody;
	}

	public class AddFavResponseBody {

		private String fav_id;

		public String getFav_id() {
			return fav_id;
		}

		public void setFav_id(String fav_id) {
			this.fav_id = fav_id;
		}
	}
}
