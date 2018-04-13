package com.easier.writepre.param;

public class UpdateUserNickPatams extends BaseBodyParams {

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

	private String coord[];

	public UpdateUserNickPatams(String uname, String birth_day, String age,
			String sex, String addr, String fav, String interest, String qq,
			String bei_tie, String fav_font, String stu_time, String school,
			String company, String profession, String signature, String email0,
			String real_name, String coord[]) {
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
	}

	@Override
	public String getProNo() {
		return "1006";
	}

}
