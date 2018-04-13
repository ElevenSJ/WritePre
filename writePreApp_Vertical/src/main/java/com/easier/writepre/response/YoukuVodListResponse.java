package com.easier.writepre.response;

import java.util.List;

/**
 * 大赛视频列表(章节)
 * 
 * @author kai.zhong
 * 
 */
public class YoukuVodListResponse extends BaseResponse {

	private YoukuVodBody repBody;

	public YoukuVodBody getRepBody() {
		return repBody;
	}

	public void setRepBody(YoukuVodBody repBody) {
		this.repBody = repBody;
	}

	public class YoukuVodBody {

		private List<YoukuVodInfo> list;

		public List<YoukuVodInfo> getList() {
			return list;
		}

		public void setList(List<YoukuVodInfo> list) {
			this.list = list;
		}
	}

	public class YoukuVodInfo {

		private String _id;

		private String group_id;

		private String yk_gp_id;

		private String yk_video_id;

		private String video_id;

		private String title;

		private String desc;

		private int sort;

		public String get_id() {
			return _id;
		}

		public void set_id(String _id) {
			this._id = _id;
		}

		public String getGroup_id() {
			return group_id;
		}

		public void setGroup_id(String group_id) {
			this.group_id = group_id;
		}

		public String getYk_gp_id() {
			return yk_gp_id;
		}

		public void setYk_gp_id(String yk_gp_id) {
			this.yk_gp_id = yk_gp_id;
		}

		public String getYk_video_id() {
			return yk_video_id;
		}

		public void setYk_video_id(String yk_video_id) {
			this.yk_video_id = yk_video_id;
		}

		public String getVideo_id() {
			return video_id;
		}

		public void setVideo_id(String video_id) {
			this.video_id = video_id;
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

		public int getSort() {
			return sort;
		}

		public void setSort(int sort) {
			this.sort = sort;
		}

		public String getCtime() {
			return ctime;
		}

		public void setCtime(String ctime) {
			this.ctime = ctime;
		}

		private String ctime;

	}

}
