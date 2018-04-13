package com.easier.writepre.param;

import com.easier.writepre.entity.ApplyForTeacherStatusInfo;

import android.text.TextUtils;

/**
 * 提交老师申請
 */
public class CommitApplyForTeacherParams extends BaseBodyParams {
	private String req_id = "";
	private String real_name = "";
	private String sex = "";
	private String birth_day = "";// yyyyMMdd
	private String city = "";
	private String school_name = "";
	private String tel = "";
	private String tech_pic = "";

	@Override
	public String getProNo() {
		return TextUtils.isEmpty(req_id) ? "sj_user_teacher_req" : "sj_user_teacher_req_modify";
	}

	@Override
	public String getUrl() {
		return "app";
	}

	public CommitApplyForTeacherParams(ApplyForTeacherStatusInfo statusInfo) {
		req_id = statusInfo.get_id();
		real_name = statusInfo.getReal_name();
		sex = statusInfo.getSex();
		birth_day = statusInfo.getBirth_day();
		city = statusInfo.getCity();
		school_name = statusInfo.getSchool_name();
		tel = statusInfo.getTel();
		tech_pic = statusInfo.getTech_pic();
	}

}
