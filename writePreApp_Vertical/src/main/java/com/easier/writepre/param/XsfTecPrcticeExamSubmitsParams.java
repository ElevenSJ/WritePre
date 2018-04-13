package com.easier.writepre.param;

import com.easier.writepre.utils.SPUtils;

import java.util.List;

/**
 * 提交实践考试试卷
 *
 * @author zhaomaohan
 */
public class XsfTecPrcticeExamSubmitsParams extends BaseBodyParams {
	private String stu_type;

	private String pkg_id;

	private List<Items> items;

	public XsfTecPrcticeExamSubmitsParams() {
//		super();
	}

	public XsfTecPrcticeExamSubmitsParams(String stu_type,String pkg_id,List<Items> items) {
//		super();
		this.stu_type = stu_type;
		this.pkg_id = pkg_id;
		this.items = items;
	}

	public class Items {
		private String pra_id;
		private String pic_url_1;
		private String pic_url_2;
		private String video_url;

	}
	@Override
	public String getProNo() {
		// TODO Auto-generated method stub
		return "tec_xsf_exam_practice_submit";
	}
	
	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		return  "app";
	}

}
