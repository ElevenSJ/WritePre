package com.easier.writepre.param;

import com.easier.writepre.utils.SPUtils;

public class PkSignUpParams extends BaseBodyParams {

	private String pk_id; // 大赛id

	private String pk_cata_id; // 参赛碑帖id

	private String role;
	
	private String teacher="";	// 指导老师

	private ExtInfo ext_info;	// 指导老师
	
	

	public PkSignUpParams(String pk_id,String pk_cata_id, String role, String teacher,ExtInfo ext_info) {
//		super();
		this.pk_id = pk_id;
		this.pk_cata_id = pk_cata_id;
		this.role = role;
		this.teacher = teacher==null?"":teacher;
		this.ext_info = ext_info;
	}

	@Override
	public String getProNo() {
		// TODO Auto-generated method stub
		return "sj_pk_signup";
	}
	
	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		return (boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false) ? "app" : "login";
	}

	public static class ExtInfo {
		private String uname;
		private String age;
		private String school;
		private String grade;

		public ExtInfo(String uname, String age, String school, String grade) {
			this.uname = uname;
			this.age = age;
			this.school = school;
			this.grade = grade;

		}

		public ExtInfo() {

		}
	}
}
