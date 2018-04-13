package com.easier.writepre.param;

public class UpdateStudyParams extends BaseBodyParams {

	private final String ref_lsn_id;// 课程id 不能为空

	private final String cata_id;// 目录id  DIY传index  不能为空


	@Override
	public String getProNo() {
		return "2009";
	}

	public UpdateStudyParams(String ref_lsn_id, String cata_id) {
		//2009 课程--学习进度更新
		this.ref_lsn_id = ref_lsn_id;
		this.cata_id = cata_id;
	}

	@Override
	public String getUrl() {
		return "/writePieWeb/app";
	}

}
