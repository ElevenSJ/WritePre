package com.easier.writepre.response;

import com.easier.writepre.entity.PkContentInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 查询推荐优秀大赛作品
 * 
 * @author zhaomaohan
 * 
 */
public class PkWorksRecommendResponse extends BaseResponse {

	private Repbody repBody;

	public Repbody getRepBody() {
		return repBody;
	}

	public void setRepBody(Repbody repBody) {
		this.repBody = repBody;
	}

	public class Repbody {

		private String tip_title;

		private List<PkContentInfo> list;

		public String getTip_title() {
			return tip_title;
		}

		public void setTip_title(String tip_title) {
			this.tip_title = tip_title;
		}

		public void setList(List<PkContentInfo> list) {
			this.list = list;
		}

		public List<PkContentInfo> getList() {
			return list;
		}

	}

}