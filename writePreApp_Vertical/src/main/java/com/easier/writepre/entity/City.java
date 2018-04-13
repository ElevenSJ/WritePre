package com.easier.writepre.entity;

public class City {
	public String sname;
	public String full_spell;
	public Double longitude;
	public Double latitude;

	
	public String getSname() {
		return sname;
	}

	public void setSname(String sname) {
		this.sname = sname;
	}

	public String getFull_spell() {
		return full_spell;
	}

	public void setFull_spell(String full_spell) {
		this.full_spell = full_spell;
	}
	
	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public City(String sname, String full_spell, Double longitude,
			Double latitude) {
		super();
		this.sname = sname;
		this.full_spell = full_spell;
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public City(String sname, String full_spell) {
		super();
		this.sname = sname;
		this.full_spell = full_spell;
	}
	
}
