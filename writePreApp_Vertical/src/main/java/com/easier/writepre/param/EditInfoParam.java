package com.easier.writepre.param;

import java.util.List;
import android.text.TextUtils;

public class EditInfoParam extends BaseBodyParams {

	private String uname;

	private String birth_day;

	private String age;

	private String sex;

	private String addr;

	private String fav;

	private String interest;

	private String qq;

	private String bei_tie;

	private String fav_font;

	private String stu_time;

	private String school;

	private String company;

	private String profession;

	private String signature;

	private String email0;

	private String real_name;

	private String good_at;

	private List<Double> coord;

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getBirth_day() {
		return birth_day;
	}

	public void setBirth_day(String birth_day) {
		this.birth_day = birth_day;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getSex() {
		return TextUtils.isEmpty(sex) ? "男" : sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getFav() {
		return fav;
	}

	public void setFav(String fav) {
		this.fav = fav;
	}

	public String getInterest() {
		return interest;
	}

	public void setInterest(String interest) {
		this.interest = interest;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getBei_tie() {
		return bei_tie;
	}

	public void setBei_tie(String bei_tie) {
		this.bei_tie = bei_tie;
	}

	public String getFav_font() {
		return fav_font;
	}

	public void setFav_font(String fav_font) {
		this.fav_font = fav_font;
	}

	public String getStu_time() {
		return stu_time;
	}

	public void setStu_time(String stu_time) {
		this.stu_time = stu_time;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public List<Double> getCoord() {
		return coord;
	}

	public void setCoord(List<Double> coord) {
		this.coord = coord;
	}

	public String getEmail0() {
		return email0;
	}

	public void setEmail0(String email0) {
		this.email0 = email0;
	}

	public String getReal_name() {
		return real_name;
	}

	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}

	public String getGoodat() {
		return good_at;
	}

	public void setGoodat(String goodat) {
		this.good_at = goodat;
	}

	public EditInfoParam(String uname, String birth_day, String age,
			String sex, String addr, String fav, String interest, String qq,
			String bei_tie, String fav_font, String stu_time, String school,
			String company, String profession, String signature, String email0,
			String real_name, List<Double> coord, String goodat) {
		// super();
		this.uname = uname;
		this.birth_day = birth_day;
		this.age = age;
		this.sex = sex;
		this.addr = addr;
		this.fav = fav;
		this.interest = interest;
		this.qq = qq;
		this.bei_tie = bei_tie;
		this.fav_font = fav_font;
		this.stu_time = stu_time;
		this.school = school;
		this.company = company;
		this.profession = profession;
		this.signature = signature;
		this.email0 = email0;
		this.real_name = real_name;
		this.coord = coord;
		this.good_at = goodat;
	}

	@Override
	public String getProNo() {
		return "1006";
	}

}
