package com.easier.writepre.param;

/**
 * 查询申请老师详情
 */
public class GetApplyForTeacherParams extends BaseBodyParams {

	@Override
	public String getProNo() {
		return "sj_user_teacher_req_query";
	}

	@Override
	public String getUrl() {
		return "app";
	}

}
