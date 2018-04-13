package com.easier.writepre.response;

import java.io.Serializable;
import java.util.List;

import com.easier.writepre.entity.MarkNo;

/**
 * 标签
 * 
 * @author dqt
 *
 */
public class MarkResponse extends BaseResponse {
	private Repbody repBody;

	public void setRepBody(Repbody repBody) {
		this.repBody = repBody;
	}

	public Repbody getRepBody() {
		return repBody;
	}

	public class Repbody {

		private List<MarkNo> list;

		public void setList(List<MarkNo> list) {
			this.list = list;
		}

		public List<MarkNo> getList() {
			return list;
		}

	}
}