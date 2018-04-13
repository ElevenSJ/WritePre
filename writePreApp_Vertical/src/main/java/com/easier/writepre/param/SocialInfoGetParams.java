package com.easier.writepre.param;

/**
 * 社交版参数配置项参数
 * 
 * @author kai.zhong
 * 
 */
public class SocialInfoGetParams extends BaseBodyParams  {

	public SocialInfoGetParams() {
		super();
	}
	@Override
	public String getProNo() {
		return "app_sys_prop";
	}
	@Override
	public String getUrl() {
		return  "/writePieWeb/login";
	}

}
