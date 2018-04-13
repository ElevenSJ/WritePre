package com.easier.writepre.response;

import com.easier.writepre.entity.PkContentInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 人气排序作品列表和作品列表查询(学生) 共用返回报文
 * 
 * @author kai.zhong
 * 
 */
public class PkWorksQueryStudentResponse extends BaseResponse {

	private Repbody repBody;

	public Repbody getRepBody() {
		return repBody;
	}

	public void setRepBody(Repbody repBody) {
		this.repBody = repBody;
	}


	public class Repbody {

		private List<PkContentInfo> list;

		public void setList(List<PkContentInfo> list) {
			this.list = list;
		}

		public List<PkContentInfo> getList() {
			return list;
		}

	}

}