package com.easier.writepre.param;

import com.easier.writepre.utils.SPUtils;

/**
 * 考生信息查询
 */
public class TecXsfStuInfoGetParams extends BaseBodyParams {

	private String stu_type;// 报名类别  0小书法师 1专业人才

	public TecXsfStuInfoGetParams() {
	}

	public TecXsfStuInfoGetParams(String stu_type) {
		this.stu_type = stu_type;
	}

	@Override
	public String getProNo() {
		// TODO Auto-generated method stub
		return "tec_xsf_stu_info_get";
	}
	
	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		return (boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false) ? "app" : "login";
	}
}
