package com.easier.writepre.param;


public class EditPwdParams extends BaseBodyParams {

	private String oldPwd;
	
	private String newPwd;
	
	public EditPwdParams(String oldPwd, String newPwd) {
		this.oldPwd = oldPwd;
		this.newPwd = newPwd;
	}
	
	@Override
	public String getProNo() {
		return "1005";
	}

}
