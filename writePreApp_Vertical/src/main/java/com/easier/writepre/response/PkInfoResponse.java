package com.easier.writepre.response;

/**
 * 
 * 点赞返回
 * 
 * @author kai.zhong
 * 
 */
public class PkInfoResponse extends BaseResponse {

	private Repbody repBody;

	public void setRepBody(Repbody repBody) {
		this.repBody = repBody;
	}

	public Repbody getRepBody() {
		return repBody;
	}

	public class Repbody {

		private String _id;

		private String running;

		private String banner_img;

		private String report_url;

		private String ctime;

		private String stage;

		private String stage_img;

		private String news_title;

		public String get_id() {
			return _id;
		}

		public void set_id(String _id) {
			this._id = _id;
		}

		public String getRunning() {
			return running;
		}

		public void setRunning(String running) {
			this.running = running;
		}

		public String getBanner_img() {
			return banner_img;
		}

		public void setBanner_img(String banner_img) {
			this.banner_img = banner_img;
		}

		public String getReport_url() {
			return report_url;
		}

		public void setReport_url(String report_url) {
			this.report_url = report_url;
		}

		public String getCtime() {
			return ctime;
		}

		public void setCtime(String ctime) {
			this.ctime = ctime;
		}

		public String getStage() {
			return stage;
		}

		public void setStage(String stage) {
			this.stage = stage;
		}

		public String getStage_img() {
			return stage_img;
		}

		public void setStage_img(String stage_img) {
			this.stage_img = stage_img;
		}

		public String getNews_title() {
			return news_title;
		}

		public void setNews_title(String news_title) {
			this.news_title = news_title;
		}
	}
}