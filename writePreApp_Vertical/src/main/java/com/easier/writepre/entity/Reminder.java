package com.easier.writepre.entity;

public class Reminder {

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private int id;

	private String title;

	private String time;

	private String week;

	private String remark;

	private String toggle;

	public String getRemark() {
		return remark;
	}

	public String getTime() {
		return time;
	}

	public String getTitle() {
		return title;
	}

	public String getToggle() {
		return toggle;
	}

	public String getWeek() {
		return week;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setToggle(String toggle) {
		this.toggle = toggle;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String getIsDeleteButtonVisible() {
		return isDeleteButtonVisible;
	}

	public void setIsDeleteButtonVisible(String isDeleteButtonVisible) {
		this.isDeleteButtonVisible = isDeleteButtonVisible;
	}

	private String isDeleteButtonVisible;

}
