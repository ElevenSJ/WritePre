package com.easier.writepre.param;

public class UserFeedbackParams extends BaseBodyParams {
	private String memo;
	
	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	public UserFeedbackParams(String memo) {
//		super();
		this.memo = memo;
	}

	@Override
	public String getProNo() {
		// TODO Auto-generated method stub
		return "3003";
	}

}
