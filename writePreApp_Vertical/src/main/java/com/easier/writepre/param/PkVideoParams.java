package com.easier.writepre.param;

/**
 * 大赛视频列表
 * 
 * @author kai.zhong
 * 
 */
public class PkVideoParams extends BaseBodyParams {

	private String loc;

//	public String getLoc() {
//		return loc;
//	}
//
//	public void setLoc(String loc) {
//		this.loc = loc;
//	}

	public PkVideoParams(String loc) {
		this.loc = loc;
	}

	@Override
	public String getProNo() {
		return "sj_pk_vod_list";
	}

	@Override
	public String getUrl() {
		return "app";
		// return "login";
	}

}
