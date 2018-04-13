package com.easier.writepre.response;

import java.util.List;

/**
 * 
 * @author kai.zhong
 * 
 */
public class PkWorksDetailResponse extends BaseResponse {

	private Repbody repBody;

	public void setRepBody(Repbody repBody) {
		this.repBody = repBody;
	}

	public Repbody getRepBody() {
		return repBody;
	}

	public class Repbody {

		private String _id="";

		private String user_id="";

		private String uname="";

		private String head_img="";

		private String real_name="";

		private String teacher="";

		private String school_name="";

		private String pk_id="";

		private String cata_title="";

		private String works_no="";

		private String title="";

		private String desc="";

		private String status="";

		private String role="";

		private String pk_cata_id="";

		private String ok_num="";

		private String remark_num="";

		private String yk_video_id="";

		private String yk_video_state="";

		private String yk_thumbnail="";

		private String city="";

		private List<Object> coord;

		private String vod_url="";

		private Works0 works_0;

		private Works1 works_1;

		private Works2 works_2;

		private String uptime="";

		private String ctime="";

		private String stage="";

		private String is_ok="";

		private String stage_end="";
		
		private String is_teacher = "";// 1 是， 0或其他  否

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

		public String getUname() {
			return uname;
		}

		public void setUname(String uname) {
			this.uname = uname;
		}

		public String getHead_img() {
			return head_img;
		}

		public void setHead_img(String head_img) {
			this.head_img = head_img;
		}

		public String getReal_name() {
			return real_name;
		}

		public void setReal_name(String real_name) {
			this.real_name = real_name;
		}

		public String getTeacher() {
			return teacher;
		}

		public void setTeacher(String teacher) {
			this.teacher = teacher;
		}

		public String getSchool_name() {
			return school_name;
		}

		public void setSchool_name(String school_name) {
			this.school_name = school_name;
		}

		public String getPk_id() {
			return pk_id;
		}

		public void setPk_id(String pk_id) {
			this.pk_id = pk_id;
		}

		public String getCata_title() {
			return cata_title;
		}

		public void setCata_title(String cata_title) {
			this.cata_title = cata_title;
		}

		public String getWorks_no() {
			return works_no;
		}

		public void setWorks_no(String works_no) {
			this.works_no = works_no;
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

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getRole() {
			return role;
		}

		public void setRole(String role) {
			this.role = role;
		}

		public String getPk_cata_id() {
			return pk_cata_id;
		}

		public void setPk_cata_id(String pk_cata_id) {
			this.pk_cata_id = pk_cata_id;
		}

		public String getOk_num() {
			return ok_num;
		}

		public void setOk_num(String ok_num) {
			this.ok_num = ok_num;
		}

		public String getRemark_num() {
			return remark_num;
		}

		public void setRemark_num(String remark_num) {
			this.remark_num = remark_num;
		}

		public String getYk_video_id() {
			return yk_video_id;
		}

		public void setYk_video_id(String yk_video_id) {
			this.yk_video_id = yk_video_id;
		}

		public String getYk_video_state() {
			return yk_video_state;
		}

		public void setYk_video_state(String yk_video_state) {
			this.yk_video_state = yk_video_state;
		}

		public String getYk_thumbnail() {
			return yk_thumbnail;
		}

		public void setYk_thumbnail(String yk_thumbnail) {
			this.yk_thumbnail = yk_thumbnail;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public List<Object> getCoord() {
			return coord;
		}

		public void setCoord(List<Object> coord) {
			this.coord = coord;
		}

		public String getVod_url() {
			return vod_url;
		}

		public void setVod_url(String vod_url) {
			this.vod_url = vod_url;
		}

		public Works0 getWorks_0() {
			return works_0;
		}

		public void setWorks_0(Works0 works_0) {
			this.works_0 = works_0;
		}

		public Works1 getWorks_1() {
			return works_1;
		}

		public void setWorks_1(Works1 works_1) {
			this.works_1 = works_1;
		}

		public Works2 getWorks_2() {
			return works_2;
		}

		public void setWorks_2(Works2 works_2) {
			this.works_2 = works_2;
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

		public String getStage() {
			return stage;
		}

		public void setStage(String stage) {
			this.stage = stage;
		}

		public String getIs_ok() {
			return is_ok;
		}

		public void setIs_ok(String is_ok) {
			this.is_ok = is_ok;
		}

		public String getStage_end() {
			return stage_end;
		}

		public void setStage_end(String stage_end) {
			this.stage_end = stage_end;
		}

		public String getIs_teacher() {
			return is_teacher;
		}

		public void setIs_teacher(String is_teacher) {
			this.is_teacher = is_teacher;
		}
		

	}

	public class Works0 {

		private String p_url;

		private String imgs[];

		public String getP_url() {
			return p_url;
		}

		public void setP_url(String p_url) {
			this.p_url = p_url;
		}

		public String[] getImgs() {
			return imgs;
		}

		public void setImgs(String[] imgs) {
			this.imgs = imgs;
		}
	}

	public class Works1 {
		private String p_url;

		private String imgs[];

		public String getP_url() {
			return p_url;
		}

		public void setP_url(String p_url) {
			this.p_url = p_url;
		}

		public String[] getImgs() {
			return imgs;
		}

		public void setImgs(String[] imgs) {
			this.imgs = imgs;
		}
	}

	public class Works2 {
		private String p_url;

		private String imgs[];

		public String getP_url() {
			return p_url;
		}

		public void setP_url(String p_url) {
			this.p_url = p_url;
		}

		public String[] getImgs() {
			return imgs;
		}

		public void setImgs(String[] imgs) {
			this.imgs = imgs;
		}
	}
}