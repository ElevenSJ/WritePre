package com.easier.writepre.param;

import com.easier.writepre.utils.SPUtils;

public class SjVodRemarkDelParams extends BaseBodyParams {
	
	private String remark_id;
	
//	public String getRemark_id() {
//		return remark_id;
//	}
//
//	public void setRemark_id(String remark_id) {
//		this.remark_id = remark_id;
//	}
	
	public SjVodRemarkDelParams(String remark_id) {
		this.remark_id = remark_id;
	}

	@Override
	public String getProNo() {
		return "sj_vod_remark_del";
	}

	@Override
	public String getUrl() {
		return (boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false) ? "app" : "login";
	}
}
