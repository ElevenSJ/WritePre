package com.easier.writepre.param;

/**
 * 
 * oss sts token
 * 
 * @author kai.zhong
 * 
 */
public class OssTokenParams extends BaseBodyParams {
	
	public OssTokenParams() {
		//super();
	}
	
	@Override
	public String getProNo() {
		return "sj_sts_token";
	}

	@Override
	public String getUrl() {
		return "app";
	}

}
