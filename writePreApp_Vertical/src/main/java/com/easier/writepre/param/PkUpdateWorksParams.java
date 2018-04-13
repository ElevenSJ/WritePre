package com.easier.writepre.param;

import java.util.List;

import com.easier.writepre.utils.SPUtils;

public class PkUpdateWorksParams extends BaseBodyParams {
	private String pk_id;

	private String works_id;
	
	private List<ImgUrlUpdateWork> imgs;

	private String vod_url;

	private String stage;
	
	public PkUpdateWorksParams() {
//		super();
	}

	public PkUpdateWorksParams(String pk_id, String works_id, List<ImgUrlUpdateWork> imgs,
			String vod_url, String stage) {
//		super();
		this.pk_id = pk_id;
		this.works_id = works_id;
		this.imgs = imgs;
		this.vod_url = vod_url;
		this.stage = stage;
	}

	public class ImgUrlUpdateWork {

		private String url;

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUrl() {
			return url;
		}

	}
	@Override
	public String getProNo() {
		// TODO Auto-generated method stub
		return "sj_pk_works_update";
	}
	
	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		return (boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false) ? "app" : "login";
	}

}
