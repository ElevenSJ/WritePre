package com.easier.writepre.param;

/**
 * 广场中全部和精华参数
 * 
 * @author kai.zhong
 * 
 */
public class SquareContentGetParams extends BaseBodyParams {

	private boolean flag = true; // 用于区分全部和精华接口 true 表示全部 false表示精华

	private String last_id;
	private int count;
	private String topic_id="";

	public SquareContentGetParams(String last_id, int count, boolean flag) {
		this.count = count;
		this.last_id = last_id;
		this.flag = flag;
	}
	public SquareContentGetParams(String last_id, int count, String topic_id) {
		this(last_id,count,true);
		this.topic_id = topic_id;
	}

	@Override
	public String getProNo() {
		return flag ? "sj_square_post_query" : "sj_square_post_gooded";
	}

	@Override
	public String getUrl() {
		return "login";
	}

}
