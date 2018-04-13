package com.easier.writepre.response;

public class FavoriteOrNotResponse extends BaseResponse {

	private FavoriteOrNotBody repBody;

	public FavoriteOrNotBody getRepBody() {
		return repBody;
	}

	public void setRepBody(FavoriteOrNotBody repBody) {
		this.repBody = repBody;
	}

	public class FavoriteOrNotBody {

		private String is_fav;

		public String getIs_fav() {
			return is_fav;
		}

		public void setIs_fav(String is_fav) {
			this.is_fav = is_fav;
		}

	}
}
