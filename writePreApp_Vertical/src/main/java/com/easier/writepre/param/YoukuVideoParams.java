package com.easier.writepre.param;

/**
 * 大会视频列表
 * 
 * @author kai.zhong
 * 
 */
public class YoukuVideoParams extends BaseBodyParams {

	private String last_id, count, group_id;

	private int flag; // 0 标识推荐视频 1 标识视频列表 2 标识章节

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public YoukuVideoParams() {
	}

	public YoukuVideoParams(String last_id, String count) {
		this.last_id = last_id;
		this.count = count;
	}

	public YoukuVideoParams(String group_id) {
		this.group_id = group_id;
	}

	@Override
	public String getProNo() {
		if (flag == 0) {
			return "sj_pk_vodgp_rec_list";
		} else if (flag == 1) {
			return "sj_pk_vodgp_list";
		} else
			return "sj_pk_vod_list";
	}

	@Override
	public String getUrl() {
		// return UserConfig.getInstance().isLogin() ? "app" : "login";
		return "login";
	}

}
