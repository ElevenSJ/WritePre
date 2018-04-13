package com.easier.writepre.param;

import com.easier.writepre.utils.SPUtils;

/**
 * 考生修改身份信息后再次校验（用在考生第一次身份校验失败后，一天只能调用3次）
 */
public class TecXsfStuInfoVerifyParams extends BaseBodyParams {

	private String real_name;
	private String id_num;// 身份证号
	private String city;
	private String addr;		// 详细地址
	private String school_name;	// 培训机构或学校地址
	private String school_contact;	// 联系方式
	private String photo_url;// 证件照地址
	private String stu_type;// 报名类别  0小书法师 1专业人才


	public TecXsfStuInfoVerifyParams(String real_name, String id_num, String city, String addr, String school_name, String school_contact, String photo_url) {
		this.real_name = real_name;
		this.id_num = id_num;
		this.city = city;
		this.addr = addr;
		this.school_name = school_name;
		this.school_contact = school_contact;
		this.photo_url = photo_url;
	}

	public TecXsfStuInfoVerifyParams(String real_name, String id_num, String city, String addr, String school_name, String school_contact, String photo_url, String stu_type) {
		this.real_name = real_name;
		this.id_num = id_num;
		this.city = city;
		this.addr = addr;
		this.school_name = school_name;
		this.school_contact = school_contact;
		this.photo_url = photo_url;
		this.stu_type = stu_type;
	}

	@Override
	public String getProNo() {
		// TODO Auto-generated method stub
		return "tec_xsf_stu_info_verify";
	}
	
	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		return (boolean) SPUtils.instance().get(SPUtils.IS_LOGIN, false) ? "app" : "login";
	}

	public String getReal_name() {
		return real_name;
	}

	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}

	public String getId_num() {
		return id_num;
	}

	public void setId_num(String id_num) {
		this.id_num = id_num;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getSchool_name() {
		return school_name;
	}

	public void setSchool_name(String school_name) {
		this.school_name = school_name;
	}

	public String getSchool_contact() {
		return school_contact;
	}

	public void setSchool_contact(String school_contact) {
		this.school_contact = school_contact;
	}

	public String getPhoto_url() {
		return photo_url;
	}

	public void setPhoto_url(String photo_url) {
		this.photo_url = photo_url;
	}
}
