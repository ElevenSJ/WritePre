package com.easier.writepre.response;

public class PkSignUpResponse extends BaseResponse {
	
	private PkSignUpBody  repBody;
	
	public PkSignUpBody getRepBody() {
		return repBody;
	}

	public void setRepBody(PkSignUpBody repBody) {
		this.repBody = repBody;
	}

	public class PkSignUpBody{
		
		private String works_no;
		private String works_id;
		public String getWorks_no() {
			return works_no;
		}
		public void setWorks_no(String works_no) {
			this.works_no = works_no;
		}
		public String getWorks_id() {
			return works_id;
		}
		public void setWorks_id(String works_id) {
			this.works_id = works_id;
		}
		
		
	}
}
