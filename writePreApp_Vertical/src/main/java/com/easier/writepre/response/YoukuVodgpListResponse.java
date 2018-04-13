package com.easier.writepre.response;

import java.util.List;

/**
 * 大会视频列表
 * 
 * @author kai.zhong
 * 
 */
public class YoukuVodgpListResponse extends BaseResponse {

	private YoukuVodgpBody repBody;

	public YoukuVodgpBody getRepBody() {
		return repBody;
	}

	public void setRepBody(YoukuVodgpBody repBody) {
		this.repBody = repBody;
	}

	public class YoukuVodgpBody {

		private List<YoukuVodgpInfo> list;

		public List<YoukuVodgpInfo> getList() {
			return list;
		}

		public void setList(List<YoukuVodgpInfo> list) {
			this.list = list;
		}
	}

	public class YoukuVodgpInfo {

		private String _id;

		private String yk_gp_id;

		private String title;

		private String desc;

		private String face_url;

		private int sort_num;

		private String is_pub;

		private String is_rec;

		private String uptime;

		private String ctime;

		public String get_id() {
			return _id;
		}

		public void set_id(String _id) {
			this._id = _id;
		}

		public String getYk_gp_id() {
			return yk_gp_id;
		}

		public void setYk_gp_id(String yk_gp_id) {
			this.yk_gp_id = yk_gp_id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public String getFace_url() {
			return face_url;
		}

		public void setFace_url(String face_url) {
			this.face_url = face_url;
		}

		public int getSort_num() {
			return sort_num;
		}

		public void setSort_num(int sort_num) {
			this.sort_num = sort_num;
		}

		public String getIs_pub() {
			return is_pub;
		}

		public void setIs_pub(String is_pub) {
			this.is_pub = is_pub;
		}

		public String getIs_rec() {
			return is_rec;
		}

		public void setIs_rec(String is_rec) {
			this.is_rec = is_rec;
		}

		public String getUptime() {
			return uptime;
		}

		public void setUptime(String uptime) {
			this.uptime = uptime;
		}

		public String getCtime() {
			return ctime;
		}

		public void setCtime(String ctime) {
			this.ctime = ctime;
		}

	}

}
