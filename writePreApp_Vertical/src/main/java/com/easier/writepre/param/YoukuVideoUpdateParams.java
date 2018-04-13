package com.easier.writepre.param;

/**
 * 上传视频至优酷后 作品更新
 * 
 * @author
 * 
 */
public class YoukuVideoUpdateParams extends BaseBodyParams {

	private String works_id, yk_video_id;

	public YoukuVideoUpdateParams(String works_id, String yk_video_id) {
		this.works_id = works_id;
		this.yk_video_id = yk_video_id;
	}

	@Override
	public String getProNo() {
		return "sj_pk_works_vod_update";
	}

	@Override
	public String getUrl() {
		return "app";
		// return "login";
	}

}
