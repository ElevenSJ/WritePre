package com.easier.writepre.response;

import java.util.List;

/**
 * 朝代列表查询返回结果集
 * 
 */
public class ResPeriodResponse extends BaseResponse {

	private ResPeriodBody repBody;

	public ResPeriodBody getRepBody() {
		return repBody;
	}

	public void setRepBody(ResPeriodBody repBody) {
		this.repBody = repBody;
	}

	public class ResPeriodBody {

		private List<ResPeriodInfo> list;

		public void setList(List<ResPeriodInfo> list) {
			this.list = list;
		}

		public List<ResPeriodInfo> getList() {
			return list;
		}

	}

	public class ResPeriodInfo {

		private String _id;

		private String text;

		private String desc;
		
		private String icon_url;
		
		private String type;

		private String link_url;

		private String p_id;

		private String sort;

		private String ctime;

		public String get_id() {
			return _id;
		}

		public void set_id(String _id) {
			this._id = _id;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public String getIcon_url() {
			return icon_url;
		}

		public void setIcon_url(String icon_url) {
			this.icon_url = icon_url;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getLink_url() {
			return link_url;
		}

		public void setLink_url(String link_url) {
			this.link_url = link_url;
		}

		public String getP_id() {
			return p_id;
		}

		public void setP_id(String p_id) {
			this.p_id = p_id;
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
