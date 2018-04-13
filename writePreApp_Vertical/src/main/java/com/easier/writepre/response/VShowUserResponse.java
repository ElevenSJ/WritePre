package com.easier.writepre.response;

import java.io.Serializable;
import java.util.List;

/**
 * v展响应
 *
 * @author zhaomaohan
 * 
 */
public class VShowUserResponse extends BaseResponse {

	private Repbody repBody;

	public Repbody getRepBody() {
		return repBody;
	}

	public void setRepBody(Repbody repBody) {
		this.repBody = repBody;
	}

	public class UserInfo implements Serializable {

		private String _id;

		private String user_id;

		private String photo_url;

		private String real_name;

		private String title;

		private String desc;

		private String ctime;

		public String get_id() {
			return _id;
		}

		public void set_id(String _id) {
			this._id = _id;
		}

		public String getUser_id() {
			return user_id;
		}

		public void setUser_id(String user_id) {
			this.user_id = user_id;
		}

		public String getPhoto_url() {
			return photo_url;
		}

		public void setPhoto_url(String photo_url) {
			this.photo_url = photo_url;
		}

		public String getReal_name() {
			return real_name;
		}

		public void setReal_name(String real_name) {
			this.real_name = real_name;
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

		public String getCtime() {
			return ctime;
		}

		public void setCtime(String ctime) {
			this.ctime = ctime;
		}

	}

	public class Repbody {

		private List<UserInfo> list;

		public void setList(List<UserInfo> list) {
			this.list = list;
		}

		public List<UserInfo> getList() {
			return list;
		}

	}

}