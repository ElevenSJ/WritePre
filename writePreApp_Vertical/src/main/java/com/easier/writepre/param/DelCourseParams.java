package com.easier.writepre.param;

import com.easier.writepre.ui.CourseCatalogActivityNew;

public class DelCourseParams extends BaseBodyParams {

	private  String cs_id;

	private  String csvod_id;

	private  String type;

	@Override
	public String getProNo() {
		return type.equals(CourseCatalogActivityNew.vodTypeTag)?"doc_user_fav_csvod_del":"res_my_course_del";
	}

	public DelCourseParams(String cs_id,String type) {
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
