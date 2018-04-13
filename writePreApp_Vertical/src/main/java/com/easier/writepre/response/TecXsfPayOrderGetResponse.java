package com.easier.writepre.response;
/**
 * 获取支付订单信息
 */
public class TecXsfPayOrderGetResponse extends BaseResponse {

	private TecXsfPayOrderGetBody repBody;

	public TecXsfPayOrderGetBody getRepBody() {
		return repBody;
	}

	public void setRepBody(TecXsfPayOrderGetBody repBody) {
		this.repBody = repBody;
	}

	public class TecXsfPayOrderGetBody {

		private String sign_str;        // 签名后字符串

		public String getSign_str() {
			return sign_str;
		}

		public void setSign_str(String sign_str) {
			this.sign_str = sign_str;
		}
	}
}