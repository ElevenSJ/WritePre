package com.easier.writepre.param;

/**
 * 
 * 绑定个推clientId
 * 
 */
public class BindClientIdParams extends BaseBodyParams {
	
	private String getui_cid;
	private String getui_app_id;
	
	public BindClientIdParams(String getui_cid,String appId) {
		this.getui_cid = getui_cid;
		this.getui_app_id = appId;
	}

	@Override
	public String getProNo() {
		return "sj_msg_bind_cid";
	}

	@Override
	public String getUrl() {
		return "app";
	}

}
