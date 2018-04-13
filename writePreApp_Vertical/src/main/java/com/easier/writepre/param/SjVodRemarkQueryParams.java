package com.easier.writepre.param;

import com.easier.writepre.utils.SPUtils;

public class SjVodRemarkQueryParams extends BaseBodyParams {
	
	private String group_id, last_id, count;
	
//	public String getGroup_id() {
//		return group_id;
//	}
//
//	public void setGroup_id(String group_id) {
//		this.group_id = group_id;
//	}
//
//	public String getLast_id() {
//		return last_id;
//	}
//
//	public void setLast_id(String last_id) {
//		this.last_id = last_id;
//	}
//
//	public String getCount() {
//		return count;
//	}
//
//	public void setCount(String count) {
//		this.count = count;
//	}
	
	public SjVodRemarkQueryParams(String group_id, String last_id,
			String count) {
		this.group_id = group_id;
		this.last_id = last_id;
		this.count = count;
	}

	@Override
	public String getProNo() {
		return "sj_vod_remark_query";
	}
	
	@Override
	public String getUrl() {
		return (boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false) ? "app" : "login";
	}
}
