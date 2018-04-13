package com.easier.writepre.response;

import java.util.List;

import com.easier.writepre.entity.TopicCommentsInfo;

public class TopicCommentsResponse extends BaseResponse {
	
	private TopicCommentsBody repBody;
	
	public TopicCommentsBody getRepBody() {
		return repBody;
	}

	public void setRepBody(TopicCommentsBody repBody) {
		this.repBody = repBody;
	}

	public class TopicCommentsBody{
		
		private List<TopicCommentsInfo> list;

		public List<TopicCommentsInfo> getList() {
			return list;
		}

		public void setList(List<TopicCommentsInfo> list) {
			this.list = list;
		}
		
	}
}
