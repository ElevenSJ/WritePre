package com.easier.writepre.entity;

public class CircleApplyMember extends CircleMemberBase{

	private Boolean is_circle_member = false;//自己设的属性，与接口无关，判断当前是否已通过

	public Boolean getIs_circle_member() {
		return is_circle_member;
	}

	public void setIs_circle_member(Boolean is_circle_member) {
		this.is_circle_member = is_circle_member;
	}
}
