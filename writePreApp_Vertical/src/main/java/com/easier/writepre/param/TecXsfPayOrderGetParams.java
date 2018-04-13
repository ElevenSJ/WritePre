package com.easier.writepre.param;

import com.easier.writepre.pay.PayManager;
import com.easier.writepre.utils.SPUtils;

/**
 * 获取支付订单信息
 */
public class TecXsfPayOrderGetParams extends BaseBodyParams {

	private int type ;
	private String pay_type ;	// alipay|upmppay|weixin
	private String stu_type ;	// 0小书法师 1书法专业人才

	public TecXsfPayOrderGetParams(int type,String stu_type) {
		this.type=type;
		this.stu_type = stu_type;
		switch (type){
			case PayManager.PAY_ALIPAY:
				this.pay_type="alipay";
				break;
			case PayManager.PAY_WECHAT:
				this.pay_type="weixin";
				break;
			case PayManager.PAY_UP:
				this.pay_type="upmppay";
				break;
		}
	}

	@Override
	public String getProNo() {
		// TODO Auto-generated method stub
		return "tec_xsf_pay_order_get";
	}
	
	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		return (boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false) ? "app" : "login";
	}
}
