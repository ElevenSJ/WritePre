package com.easier.writepre.response;

import java.util.List;

/**
 * 碑帖图片列表
 * 
 */
public class ResBeiTiePicsResponse extends BaseResponse {

	private ResBeiTiePicsBody repBody;

	public ResBeiTiePicsBody getRepBody() {
		return repBody;
	}

	public void setRepBody(ResBeiTiePicsBody repBody) {
		this.repBody = repBody;
	}

	public class ResBeiTiePicsBody {

		private List<ResBeiTiePicsInfo> list;

		public void setList(List<ResBeiTiePicsInfo> list) {
			this.list = list;
		}

		public List<ResBeiTiePicsInfo> getList() {
			return list;
		}

	}

	public class ResBeiTiePicsInfo {

		private String _id;

		private String beitie_id;

		private String pic_url;

		private String title;

		private String sort;

		private String ctime;

		public String get_id() {
			return _id;
		}

		public void set_id(String _id) {
			this._id = _id;
		}

		public String getBeitie_id() {
			return beitie_id;
		}

		public void setBeitie_id(String beitie_id) {
			this.beitie_id = beitie_id;
		}

		public String getPic_url() {
			return pic_url;
		}

		public void setPic_url(String pic_url) {
			this.pic_url = pic_url;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getSort() {
			return sort;
		}

		public void setSort(String sort) {
			this.sort = sort;
		}

		public String getCtime() {
			return ctime;
		}

		public void setCtime(String ctime) {
			this.ctime = ctime;
		}

	}
}
