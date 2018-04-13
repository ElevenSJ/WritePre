package com.easier.writepre.param;

import com.easier.writepre.ui.CourseCatalogActivityNew;

public class AddCourseParams extends BaseBodyParams {

	private  String cs_id;
	private  String type;
	private  String csvod_id;

	@Override
	public String getProNo() {
		return type.equals(CourseCatalogActivityNew.vodTypeTag)?"doc_user_fav_csvod_add":"2006";
	}

	public AddCourseParams(String cs_id,String type) {
		this.type = type;
		if (type.equals(CourseCatalogActivityNew.vodTypeTag)){
			this.csvod_id = cs_id;
		}else{
			this.cs_id = cs_id;
		}
	}

	@Override
	public String getUrl() {
		return "/writePieWeb/app";
	}

}
